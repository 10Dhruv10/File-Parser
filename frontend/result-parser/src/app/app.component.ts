import { Component } from '@angular/core';
import { UploadFileComponent } from "./upload-file/upload-file.component";
import { RouterOutlet } from "@angular/router";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [UploadFileComponent, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'result-parser';
}
