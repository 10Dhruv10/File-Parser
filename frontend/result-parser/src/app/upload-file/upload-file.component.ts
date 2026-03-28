import { HttpClient, HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { afterNextRender, Component, inject, Input, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatInputModule} from '@angular/material/input';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
import { finalize, Subscription } from 'rxjs';

@Component({
  selector: 'app-upload-file',
  standalone: true,
  imports: [MatIconModule, MatButtonModule, MatProgressBarModule, NgxExtendedPdfViewerModule, MatInputModule],
  templateUrl: './upload-file.component.html',
  styleUrl: './upload-file.component.css'
})
export class UploadFileComponent {

    filesUploaded = signal<File[]>([]);
    filePreview = signal<string[]>([]);
    fileTypeValid: boolean = true;
    selectedIndex = signal<number>(0);
    progressValue = signal<number>(0);

    private http = inject(HttpClient);


    //code for previewing files
    onFileUpload(event: Event){
      console.log(event);
      const input = event.target as HTMLInputElement;
      const fileList = input.files;
      if (!fileList) return;
      
      if (fileList){

        for (let i=0; i<fileList.length; i++){
          const file = fileList[i];
          
          if (!(file?.type === "image/jpeg" || file?.type === "image/png" || file?.type === "application/pdf")){
            this.fileTypeValid = false;
            window.alert("please enter a valid type (image/pdf) for ");
            break;
          }
        }

        if (this.fileTypeValid === true){
          const listOfAllFiles = [];
          for (let i=0; i<fileList.length; i++){
            const file = URL.createObjectURL(fileList[i]);
            listOfAllFiles.push(file);
          }

          this.filePreview.set(listOfAllFiles);
          this.filesUploaded.set(Array.from(fileList));

        }
      }
    }
    
    //later change 0 based indexing to 1-based
    changeIndex(value: number){
      if (0 <= this.selectedIndex() + value  &&  this.selectedIndex() + value < this.filesUploaded().length){
        this.selectedIndex.set(this.selectedIndex() + value);
      }
      else{
        this.selectedIndex.set(0);
        window.alert("choose a valid numeric value only")
      }
    }

    changeIndexOnSelect(event: any){
      const value = event.target.value
      if (value==='' || value===null){
        return
      }

      const num = Number(value);
      if (0 <= num && num < this.filesUploaded().length){
        this.selectedIndex.set(num);
      }
      else{
        this.selectedIndex.set(0);
        window.alert("choose a valid numeric value only")
      }
    }

    
    //code for sending to backend
    onSubmit(){
        if (this.filesUploaded().length > 0){
            const formData = new FormData();
            for (let i=0; i<this.filesUploaded().length; i++){
              formData.append("filesFromAngular", this.filesUploaded()[i]!);       //this way is right, i dont quiet get it, why same key multiple value
            }   

            this.http.post("http://localhost:8080/backendApi/upload", formData, {
                reportProgress: true,
                observe: 'events'
              })
              .subscribe({ 
                next: (event) => {
                  
                  if (event.type === HttpEventType.UploadProgress) {
                    this.progressValue.set(Math.round(100 * event.loaded / (event.total ?? 1)));
                  }

                  if (event.type === HttpEventType.Response) {
                    console.log(event.body);
                  }

                  
                },
                error: (err) => console.log(err)
              });
        }
       
    }
}
      

      /*
      const file = input.files?.[0];
      this.fileType = file?.type;
      console.log(event);
      console.log(file);

      if (!file || !(this.fileType === "image/jpeg" || this.fileType === "image/png" || this.fileType === "application/pdf")){
        this.fileType = undefined;
        window.alert("please enter a valid type (image/pdf)");
        return;
      }

      this.filePreview.set(URL.createObjectURL(file));
      this.fileUploaded.set(file);
    }
    */

    
   


    
  //   uploadProgress: number | null = null;
  //   uploadSubscription: Subscription | null = null;
  //   uploadError: string | null = null;

  //   private http = inject(HttpClient);

  //   onFileSelected(event: Event) {
  //     const input = event.target as HTMLInputElement;
  //     const file = input.files?.[0];
      
  //       if (file) {
  //         this.uploadError = null;
  //           this.fileName = file.name;
  //           const formData = new FormData();
  //           formData.append("thumbnail", file);

  //       }
  //   }

  // cancelUpload() {
  //   this.uploadSub?.unsubscribe();
  //   this.reset();
  // }

  // reset() {
  //   this.uploadProgress = null;
  //   this.uploadSub = null;
  // }

