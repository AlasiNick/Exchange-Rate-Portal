import { ChangeDetectionStrategy, Component, OnInit, signal, inject} from '@angular/core';
import { ZardAlertComponent } from '@shared/components/alert/alert.component';
import { ZardButtonComponent } from '@shared/components/button/button.component';
import { ZardComboboxComponent, ZardComboboxOption } from '@shared/components/combobox/combobox.component';
import { ZardDatePickerComponent } from '@shared/components/date-picker/date-picker.component';
import { ZardInputDirective } from '@shared/components/input/input.directive';
import { ZardLoaderComponent } from '@shared/components/loader/loader.component';
import { CurrencyService } from 'src/app/services/CurrencyService';
import { FxRateService } from 'src/app/services/FxRateService';

@Component({
  selector: 'app-calculator',
  imports: [ZardComboboxComponent, ZardDatePickerComponent, ZardInputDirective, ZardButtonComponent, ZardLoaderComponent, ZardAlertComponent],
  templateUrl: './calculator.html',
  styleUrl: './calculator.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})

export class Calculator implements OnInit {
  selectedDate = signal<Date | null>(new Date());
  currenciesForList: ZardComboboxOption[] = [];
  currencyAmount = 0;
  currencyFrom = '';
  currencyTo = '';
  calculationResult: any | null = null;
  loaderIsActive = signal(false);
  showError = signal(false);
  inputsChanged = signal(false);

  maxDate = new Date();
  minDate = new Date(Date.now() - 90 * 24 * 60 * 60 * 1000);

  private currencyService = inject(CurrencyService);
  private fxRateService = inject(FxRateService);

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

  onAmountInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = parseFloat(input.value);
    this.currencyAmount = isNaN(value) ? 0 : value;
    this.markInputsChanged();
  }

  onSelectFrom(option: ZardComboboxOption): void {
    this.currencyFrom = option.value as string;
    this.markInputsChanged();
  }

  onSelectTo(option: ZardComboboxOption): void {
    this.currencyTo = option.value as string;
    this.markInputsChanged();
  }

  onDateChange(date: Date | null): void {
    this.selectedDate.set(date);
    this.markInputsChanged();
  }

  private markInputsChanged(): void {
    this.inputsChanged.set(true);
  }

  private formatDate(date: Date | null): string | undefined {
    if (!date) return undefined;
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    
    return `${year}-${month}-${day}`;
  }

  onCalculate(): void {
    if (this.currencyAmount <= 0 || !this.currencyFrom || !this.currencyTo) {
      this.showError.set(true);
      return;
    }

    this.showError.set(false);
    this.loaderIsActive.set(true);
    this.inputsChanged.set(false);

    this.fxRateService
      .convertCurrency(
        this.currencyAmount,
        this.currencyFrom,
        this.currencyTo,
        this.formatDate(this.selectedDate())
      )
      .subscribe({
        next: (result) => (this.calculationResult = result),
        error: (err) => {
          console.error('Conversion error:', err);
          this.inputsChanged.set(true);
        },
        complete: () => this.loaderIsActive.set(false),
      });
  }

    shouldShowResult(): boolean {
    return !!this.calculationResult && !this.inputsChanged();
  }
}
