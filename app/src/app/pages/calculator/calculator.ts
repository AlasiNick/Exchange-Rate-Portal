import { ChangeDetectionStrategy, Component, OnInit, signal} from '@angular/core';
import { ZardComboboxComponent, ZardComboboxOption } from '@shared/components/combobox/combobox.component';
import { ZardDatePickerComponent } from '@shared/components/date-picker/date-picker.component';
import { ZardInputDirective } from '@shared/components/input/input.directive';
import { CurrencyService } from 'src/app/services/CurrencyService';

interface Currency {
  code: string;
  name?: string;
}

@Component({
  selector: 'app-calculator',
  imports: [ZardComboboxComponent, ZardDatePickerComponent, ZardInputDirective],
  templateUrl: './calculator.html',
  styleUrl: './calculator.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})

export class Calculator implements OnInit {
  selectedDate = signal<Date | null>(null);
  currencies = signal<any[] | null>(null);
  currenciesForList: ZardComboboxOption[] = [];
  
  constructor(private currencyService: CurrencyService) {}

  ngOnInit(): void {
    this.loadCurrencies();
  }

  private loadCurrencies(): void {
    this.currencyService.getCurrencies().subscribe({
      next: (data: any) => {
        console.log('Currencies loaded:', data);
        this.currencies.set(data);
        this.prepareCurrencyOptions(data);
      },
      error: (error: any) => {
        console.error('Error loading currencies:', error);
        // Consider adding user-friendly error handling here
      }
    });
  }

  private prepareCurrencyOptions(currencies: any[]): void {
    const options: ZardComboboxOption[] = currencies.map((currency: any) => ({
      value: currency.code,
      label: currency.code + (currency.name ? ` - ${currency.name}` : ''),
    }));

    this.currenciesForList.push(...options);
  }

  onDateChange(date: Date | null): void {
    this.selectedDate.set(date);
    console.log('Selected date:', date);
  }

  onSelect(option: ZardComboboxOption): void {
    console.log('Selected currency:', option);
  }
}
