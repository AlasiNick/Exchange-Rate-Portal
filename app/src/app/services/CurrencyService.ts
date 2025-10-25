import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class CurrencyService {
  private baseUrl = 'http://localhost:8080/currencies';

  constructor(private http: HttpClient) {}

  getCurrencies() {
    return this.http.get(`${this.baseUrl}`);
  }
}