import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class FxRateService {
  private baseUrl = 'http://localhost:8080/rates';

  constructor(private http: HttpClient) {}

  getRates(date?: string) {
    const params = date ? new HttpParams().set('date', date) : undefined;
    return this.http.get(`${this.baseUrl}`, { params });
  }

  convertCurrency(amount: number, from: string, to: string, date?: string) {
    let params = new HttpParams()
      .set('amount', amount.toString())
      .set('from', from)
      .set('to', to);
    if (date) params = params.set('date', date);

    return this.http.get(`${this.baseUrl}/convert`, { params });
  }

  getHistory(currencyCode: string) {
    return this.http.get(`${this.baseUrl}/history/${currencyCode}`);
  }
}