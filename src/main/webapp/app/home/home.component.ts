import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';

import { LoginModalService } from 'app/core/login/login-modal.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { HomeService } from './home.service';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  authSubscription?: Subscription;
  columnsHeaders = [
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
  columnKeys = ['orderId', 'orderFrom', 'firstName', 'lastName', 'quantity', 'subTotal', 'tip', 'address', 'zipcode', 'phone'];
  rows = [];
  page = 1;
  pageSize = 25;
  constructor(private accountService: AccountService, private loginModalService: LoginModalService, private homeService: HomeService) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
    this.homeService.getOrderDetails().subscribe(response => {
      this.rows = response;
    });
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginModalService.open();
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
}
