import { Injectable } from "@angular/core";
import { signal } from "@angular/core";

@Injectable({providedIn: 'root'})
export class jobIdService{
    private jobId = signal<string | null>(null);

    setJobId(jobId: string){
        this.jobId.set(jobId);
    }

    getJobId(){
        return this.jobId();
    }

}