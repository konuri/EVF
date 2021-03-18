import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { fromEvent, merge } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';
import { OrderDetailsService } from './order-details.service';
import { OrderSource } from './order.service';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss'],
})
export class HomeComponent implements OnInit, AfterViewInit {
  displayedColumns = [
    'Order Id',
    'Order From',
    'First Name',
    'Last Name',
    'Total Items',
    'Total Amount Paid',
    'Tip',
    'Address',
    'Zip Code',
    'Phone',
  ];
  displayedKeys = ['orderId', 'orderFrom', 'firstName', 'lastName', 'quantity', 'subTotal', 'tip', 'address', 'zipcode', 'phone'];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild('input') input: ElementRef;
  dataSource: OrderSource;

  constructor(private coursesService: OrderDetailsService, private cdf: ChangeDetectorRef) {
    // this.paginator.pageSize = 20;
  }

  ngOnInit(): void {
    this.dataSource = new OrderSource(this.coursesService);
    this.dataSource.loadOrders('', '', 'asc', 0, 20);
  }

  ngAfterViewInit(): void {
    // server-side search
    fromEvent(this.input.nativeElement, 'keyup')
      .pipe(
        debounceTime(150),
        distinctUntilChanged(),
        tap(() => {
          this.paginator.pageIndex = 0;
          this.paginator.pageSize = 20;
          this.loadLessonsPage();
        })
      )
      .subscribe();

    // reset the paginator after sorting
    this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));

    // on sort or paginate events, load a new page
    merge(this.sort.sortChange, this.paginator.page)
      .pipe(tap(() => this.loadLessonsPage()))
      .subscribe();

    this.dataSource.counter$
      .pipe(
        tap(count => {
          this.paginator.length = count;
          this.cdf.detectChanges();
        })
      )
      .subscribe();
  }

  loadLessonsPage(): void {
    this.dataSource.loadOrders(
      this.input.nativeElement.value,
      this.sort.active,
      this.sort.direction,
      this.paginator.pageIndex,
      this.paginator.pageSize
    );
  }
}
