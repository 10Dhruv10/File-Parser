from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from fastapi.middleware.cors import CORSMiddleware
from typing import List
from google import genai
from google.genai import types
import redis
import json

from dotenv import load_dotenv
import os
load_dotenv()

app = FastAPI()
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
redi = redis.Redis(host="localhost", port=6379, db=0, decode_responses=True)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:4200"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class Message(BaseModel):
    jobId: str
    text: str


class Subject(BaseModel):
    subjectName: str
    grade: str


class Student(BaseModel):
    name: str
    prn: str
    sgpa: float
    subjects: List[Subject]


class Payload(BaseModel):
    jobId: str
    students: List[Student]


@app.post("/studentData")
def receive_data(data: Payload):
    print("data received from Springboot: ", data)
    job_id, students = data.jobId, data.students

    '''
    redis cannot store python objects like list of dicts directly 
    so we need to convert the value to be stored to string using json.dumps,
    
    storing:
    redi.setex(key, TTL, value)
    
    retrieving as python object:
    value = redi.get(key)
    python_object = json.loads(value) 
    
    '''

    students_json = json.dumps([student.model_dump() for student in students])
    print("after dumping", students_json)
    redi.setex(job_id, 1800, students_json)
    print("this is how it looks", redi.get(job_id))

    return {
        "received jobId: ": job_id,
    }


from fastapi.concurrency import run_in_threadpool


@app.post("/chat")
async def receive_angular_data(data: Message):
    print("data received from angular: ", data)
    job_id, prompt = data.jobId, data.text
    students_json = redi.get(job_id)                           # equivalent to dict[key]

    if not students_json:
        raise HTTPException(status_code=404, detail=f"Job ID '{job_id}' not found")

    current_job_students = json.loads(students_json)           # convert string back to python object

    get_top_students_tool = {
        "name": "get_top_students",
        "description": "Gets top students sorted by SGPA in descending order.",
        "parameters": {
            "type": "object",
            "properties": {
                "n": {
                    "type": "integer",
                    "description": "Number of top students to return"
                }
            },
            "required": ["n"]
        }
    }

    def get_top_students(n: int) -> dict:
        sorted_students = sorted(current_job_students, key=lambda x: x["sgpa"], reverse=True)
        return {"top_students": sorted_students[:n]}

    tool_map = {"get_top_students": get_top_students}

    client = genai.Client(api_key=GEMINI_API_KEY)
    tools = types.Tool(function_declarations=[get_top_students_tool])
    config = types.GenerateContentConfig(
        tools=[tools],
        system_instruction=(
            f"You are a student data assistant. "
            f"There are {len(current_job_students)} students in the database. "
            f"Use tools to answer questions about them."
        )
    )

    response = await run_in_threadpool(
        client.models.generate_content,
        model="gemini-2.5-flash",
        contents=prompt,
        config=config
    )

    contents = [
        types.Content(role="user", parts=[types.Part(text=prompt)]),
        response.candidates[0].content,
    ]

    tool_was_called = False
    for part in response.candidates[0].content.parts:
        if part.function_call:
            tool_was_called = True
            fc = part.function_call
            print(f"Model wants to call: {fc.name}({dict(fc.args)})")

            fn = tool_map.get(fc.name)
            result = fn(**dict(fc.args))
            print(f"Tool result: {result}")

            contents.append(
                types.Content(role="user", parts=[
                    types.Part.from_function_response(
                        name=fc.name,
                        response={"result": result}
                    )
                ])
            )

    if tool_was_called:
        final_response = await run_in_threadpool(
            client.models.generate_content,
            model="gemini-2.5-flash",
            contents=contents,
            config=config
        )
        print("Final response:", final_response.text)
        return {"reply": final_response.text}

    print("Direct reply:", response.text)
    return {"reply": response.text}


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)
