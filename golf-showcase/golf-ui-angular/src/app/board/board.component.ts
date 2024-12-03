import { Component, OnInit } from '@angular/core';
import { ApiService } from '../api.service';
import { CommonModule } from '@angular/common';
import { Tournament } from './tournament';
import { ScoreBoard } from './ScoreBoard';
import { Client } from '@stomp/stompjs';

@Component({
  selector: 'app-board',
  imports: [CommonModule],
  templateUrl: './board.component.html',
  styleUrl: './board.component.less'
})


export class BoardComponent implements OnInit {
  tournament?: Tournament;
  isLoading = true;
  error: string | null = null;
  scoreBoard?: ScoreBoard;
  private webSocket?: WebSocket;
    
  constructor(private apiService: ApiService) {
    
   /* this.webSocket = new WebSocket('ws://localhost:8080/topic/scores');
    this.webSocket.onmessage = (event) => {

      this.getScores();
      alert("event"+event);
    };*/
  }

  ngOnInit(): void {
    this.tournament = new Tournament();
    //this.tournament.holes = [4,5,3,4,5,4,4,3,4,4,4,4,4,5,4,3,5,3];
    

    this.apiService.getData().subscribe({
      next: (response) => {
        this.tournament = response;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Error fetching data';
        this.isLoading = false;
      }
    });
    this.getScores();

    const SOCKET_URL = 'ws://localhost:8080/websocket';
    const stompClient = new Client({
      brokerURL: SOCKET_URL,
      reconnectDelay: 5000, // Reconnect every 5 seconds if connection is lost
      debug: (str: string) => {
        console.log(str); // Logs debug messages (optional)
      },
      onConnect: () => {
        console.log('Connected to STOMP WebSocket');
      },
      onDisconnect: () => {
        console.log('Disconnected from STOMP WebSocket');
      },
      onStompError: (error: any) => {
        console.error('STOMP Error:', error);
      },
    });
    stompClient.activate(); // Establish WebSocket connection

    stompClient.onConnect = () => {
      console.log('STOMP connected');
      
      stompClient.subscribe('/topic/scores', (message) => {
        if (message.body) {
          
          var msg = JSON.parse(message.body);
          if(msg["alert"]) {
            alert(msg["alert"]);
          }
        }
        this.getScores();
      });
    };

  }

  getHoles(): any {
    return this.tournament?.holes
  }

  getPars(): any {
    return this.tournament?.pars
  }

  getScores(): any{
    this.apiService.getScores().subscribe({
      next: (response) => {
        this.scoreBoard = response;
        console.log("scores"+this.scoreBoard)
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Error fetching data';
        this.isLoading = false;
      }
    });
    return this.scoreBoard;
  }
}
