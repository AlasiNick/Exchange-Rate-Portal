import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { ZardButtonComponent } from '@shared/components/button/button.component';
import { ZardComboboxComponent, ZardComboboxOption } from '@shared/components/combobox/combobox.component';
import { ZardTableComponent, ZardTableHeadComponent, ZardTableRowComponent, ZardTableHeaderComponent } from '@shared/components/table/table.component';
import { ZardLoaderComponent } from '@shared/components/loader/loader.component';
import { ZardAlertComponent } from '@shared/components/alert/alert.component';
import { ZardIconComponent } from '@shared/components/icon/icon.component';
import { ZardPaginationComponent, ZardPaginationContentComponent } from '@shared/components/pagination/pagination.component';
import { CurrencyService } from 'src/app/services/CurrencyService';
import { FxRateService } from 'src/app/services/FxRateService';
import { ZardPaginationModule } from '@shared/components/pagination/pagination.module';

@Component({
  selector: 'app-historical-data',
  imports: [
    ZardComboboxComponent, ZardButtonComponent, ZardTableComponent, ZardTableHeadComponent, ZardTableRowComponent, 
    ZardTableHeaderComponent, ZardLoaderComponent, ZardAlertComponent, ZardIconComponent, ZardPaginationComponent, 
    ZardPaginationModule 
  ],
  templateUrl: './historical-data.html',
  styleUrl: './historical-data.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HistoricalData implements OnInit {
  currenciesForList: ZardComboboxOption[] = [];
  private fxRateService = inject(FxRateService);
  private currencyService = inject(CurrencyService);
  
  currency = signal<string>('');
  historicalData = signal<any[] | null>(null);
  loaderIsActive = signal(false);
  showError = signal(false);
  
  sortColumn = signal<'rateToEur' | 'rateDate'>('rateDate');
  sortDirection = signal<'asc' | 'desc'>('desc');

  currentPage = signal(1);
  pageSize = 10;
  paginatedData = signal<any[]>([]);

  ngOnInit(): void {
    this.loadCurrencies();
  }

  private loadCurrencies(): void {
    this.currencyService.getCurrencies().subscribe({
      next: (data: any) => {
        this.currenciesForList = data.map((c: any) => ({
          value: c.code,
          label: `${c.code}${c.name ? ' - ' + c.name : ''}`,
        }));
      },
      error: (err) => console.error('Currency load error:', err),
    });
  }

  onSelect(option: ZardComboboxOption): void {
    this.currency.set(option.value as string);
    this.showError.set(false);
  }

  showData(): void {
    if (!this.currency()) {
      this.showError.set(true);
      return;
    }

    this.showError.set(false);
    this.loaderIsActive.set(true);
    this.historicalData.set(null);
    this.currentPage.set(1);

    this.fxRateService.getHistory(this.currency()).subscribe({
      next: (data: any) => {
        this.historicalData.set(this.applySorting(data));
        this.updatePaginatedData();
      },
      error: (err) => {
        console.error('Historical data load error:', err);
        this.historicalData.set([]);
      },
      complete: () => this.loaderIsActive.set(false),
    });
  }

  sortBy(column:'rateToEur' | 'rateDate'): void {
    const currentData = this.historicalData();
    if (!currentData) return;

    if (this.sortColumn() === column) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortColumn.set(column);
      this.sortDirection.set(column === 'rateDate' ? 'desc' : 'asc');
    }

    const sortedData = this.applySorting(currentData);
    this.historicalData.set(sortedData);
    this.currentPage.set(1); 
    this.updatePaginatedData();
  }

  private applySorting(data: any[]): any[] {
    if (!data || data.length === 0) return data;

    return [...data].sort((a, b) => {
      let aValue: any = a[this.sortColumn()];
      let bValue: any = b[this.sortColumn()];

      // Handle date sorting
      if (this.sortColumn() === 'rateDate') {
        aValue = new Date(aValue).getTime();
        bValue = new Date(bValue).getTime();
      }

      if (this.sortColumn() === 'rateToEur') {
        aValue = Number(aValue);
        bValue = Number(bValue);
      }

      if (aValue < bValue) return this.sortDirection() === 'asc' ? -1 : 1;
      if (aValue > bValue) return this.sortDirection() === 'asc' ? 1 : -1;
      return 0;
    });
  }

  isSorted(column: 'rateToEur' | 'rateDate'): boolean {
    return this.sortColumn() === column;
  }

    onPageChange(page: number): void {
    this.currentPage.set(page);
    this.updatePaginatedData();
  }

  private updatePaginatedData(): void {
    const allData = this.historicalData();
    if (!allData) {
      this.paginatedData.set([]);
      return;
    }

    const startIndex = (this.currentPage() - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    const pageData = allData.slice(startIndex, endIndex);
    
    this.paginatedData.set(pageData);
  }

  get totalPages(): number {
    const allData = this.historicalData();
    if (!allData) return 0;
    return Math.ceil(allData.length / this.pageSize);
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
  }
}