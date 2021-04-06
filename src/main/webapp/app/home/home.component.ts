import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
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
  horizontalPosition: MatSnackBarHorizontalPosition = 'right';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  displayedColumns = [
    'Order Id',
    'Order From',
    'Full Name',
    'Region Code',
    'Total Items',
    'Total Amount Paid',
    'Tip',
    'Address',
    'Zip Code',
    'Phone',
    'Submitted',
    'Action'
  ];
  displayedKeys = ['orderId', 'orderFrom', 'fullName', 'regionCode', 'quantity', 'subTotal', 'tip', 'address', 'zipcode', 'phone', 'doordashStatus','action'];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild('input') input: ElementRef;
  dataSource: OrderSource;

  constructor(private orderDetailService: OrderDetailsService, private cdf: ChangeDetectorRef, private _snackBar: MatSnackBar) {
     
  }

  ngOnInit(): void {
    this.dataSource = new OrderSource(this.orderDetailService);
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

  sendforAutomation(data): void {
    this.dataSource.sendforAutomation(data.id).subscribe(response => {
      this.loadLessonsPage();
      this.openSnackBar(response);
       },error=>{
        this.loadLessonsPage();
        this.openSnackBar(error);
       });
  }
  openSnackBar(msg: string): void{
    this._snackBar.open(msg, 'End now', {
      duration: 5000,
      horizontalPosition: this.horizontalPosition,
      verticalPosition: this.verticalPosition,
    });
  }
}
