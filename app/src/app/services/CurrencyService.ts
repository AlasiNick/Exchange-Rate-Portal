import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/env.dev';

@Injectable({ providedIn: 'root' })
export class CurrencyService {
  private baseUrl = `${environment.apiUrl}/currencies`;

  constructor(private http: HttpClient) {}

  getCurrencies() {
    return this.http.get(`${this.baseUrl}`);
  }
}