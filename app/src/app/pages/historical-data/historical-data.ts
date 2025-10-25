import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-historical-data',
  imports: [],
  templateUrl: './historical-data.html',
  styleUrl: './historical-data.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HistoricalData { }
