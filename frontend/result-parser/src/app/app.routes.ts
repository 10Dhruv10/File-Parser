import { Component } from '@angular/core';
import { Routes } from '@angular/router';
import { UploadFileComponent } from './upload-file/upload-file.component';
import { AgentChatComponent } from './agent-chat/agent-chat.component';

export const routes: Routes = [

    {
        path: '',
        component: UploadFileComponent
    },
    
    {
        path: 'agentchat',
        component: AgentChatComponent
    }


]
