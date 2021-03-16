import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SERVER_API_URL } from 'app/app.constants';
@Injectable({ providedIn: 'root' })
export class OrderDetailsService {
  constructor(private http: HttpClient) {}

  getOrderDetails(filter = '', sortBy = '', sortOrder = 'asc', pageNumber = 0, pageSize = 20): Observable<any> {
    return this.http
      .get(SERVER_API_URL + '/api/order-details/', {
        params: new HttpParams()
          .set('filter', filter)
          .set('sortOrder', sortOrder)
          .set('sortBy', sortBy)
          .set('offset', pageNumber.toString())
          .set('limit', pageSize.toString()),
      })
      .pipe(map(res => res));
  }
}
