import { Component, inject, signal } from '@angular/core';
import { MatFormField, MatInputModule } from "@angular/material/input";
import { MatIcon } from "@angular/material/icon";
import { MatButton } from "@angular/material/button";
import { HttpClient } from '@angular/common/http';
import { jobIdService } from '../upload-file/job-id.service';
import { marked } from 'marked';

@Component({
  selector: 'app-agent-chat',
  imports: [MatFormField, MatInputModule, MatIcon, MatButton],
  templateUrl: './agent-chat.component.html',
  styleUrl: './agent-chat.component.css'
})
export class AgentChatComponent {
  value = signal<string>('');
  private http = inject(HttpClient)
  private jobIdService = inject(jobIdService)
  fastApiResponse : string = ''

  onInput(event: Event){
    const input = event.target as HTMLInputElement
    this.value.set(input.value)
  }

  sendMessage(){
    const jobId = this.jobIdService.getJobId();
    console.log(jobId)

    if (!jobId) {
      console.error("jobId missing");
      return;
    }

    if (this.value()!=''){
      this.http.post("http://127.0.0.1:8000/chat", {jobId: jobId, text: this.value()})
        .subscribe({
          next: (event: any) => {
            this.fastApiResponse = event.reply
            console.log(event)
          },
          error: (err) => {console.log(err)}
        })
    }

    console.log(this.value())
    this.value.set('')

  }


  getFormattedReply() {
    return marked(this.fastApiResponse);
  }

}
