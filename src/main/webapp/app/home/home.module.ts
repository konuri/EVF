import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { EvfSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { MaterialModule } from './material.module';
@NgModule({
  imports: [EvfSharedModule, MaterialModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent],
})
export class EvfHomeModule {}
