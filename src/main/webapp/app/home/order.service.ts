import { DataSource } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { OrderDetails } from './order-details';
import { OrderDetailsService } from './order-details.service';

export class OrderSource implements DataSource<OrderDetails> {
  private ordersSubject = new BehaviorSubject<OrderDetails[]>([]);

  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable();

  private counterSubject = new BehaviorSubject<number>(0);

  public counter$ = this.counterSubject.asObservable();

  constructor(private orderDetailsService: OrderDetailsService) {}

  loadOrders(filter: string, sortBy: string, sortDirection: string, pageIndex: number, pageSize: number): void {
    this.loadingSubject.next(true);

    this.orderDetailsService
      .getOrderDetails(filter, sortBy, sortDirection, pageIndex, pageSize)
      .pipe(
        catchError(() => of([])),
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(response => {
        this.ordersSubject.next(response.content);
        this.counterSubject.next(response.totalElements);
      });
  }

  connect(): Observable<OrderDetails[]> {
    return this.ordersSubject.asObservable();
  }

  disconnect(): void {
    this.ordersSubject.complete();
    this.loadingSubject.complete();
    this.counterSubject.complete();
  }
  sendforAutomation(id: string): Observable<any>{
      return this.orderDetailsService.sendforAutomation(id);
  }
}
