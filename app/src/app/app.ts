import { Component, signal, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DarkModeService } from '@shared/services/DarkModeService';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('app');
  private readonly darkmodeService = inject(DarkModeService);

    ngOnInit(): void {
    this.darkmodeService.initTheme();
  }
}
