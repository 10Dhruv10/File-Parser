from fastapi import FastAPI
from pydantic import BaseModel
from fastapi.middleware.cors import CORSMiddleware
from typing import List

app = FastAPI()

class Subject(BaseModel):
    subjectName: str
    grade: str

class Student(BaseModel):
    name: str
    prn: str
    sgpa: float
    subjects: List[Subject]

# class Payload(BaseModel):
#     students: List[Student]

@app.post("/studentData")
def receive_data(data: List[Student]):
    print(data)
    return {"count": len(data)}

origins = [
    "http://localhost:4200",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class Message(BaseModel):
    text: str

@app.post("/chat")
def receive_angular_data(data: Message):
    return {data.text}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)

