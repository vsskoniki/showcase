import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) { }

  // Replace with your API URL
  getData(): Observable<any> {
    return this.http.get('http://localhost:8080/api/tournament');
  }

  getScores(): Observable<any> {
    return this.http.get('http://localhost:8080/api/score-board');
  }

  postScore(formData: any) : Observable<any> {
    return this.http.post('http://localhost:8080/api/score', formData);
  }
}
