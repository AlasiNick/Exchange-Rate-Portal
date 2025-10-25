import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class DarkModeService {
  private readonly storageKey = 'theme';
  private readonly platformId = inject(PLATFORM_ID);
  private readonly isBrowser = isPlatformBrowser(this.platformId);

  private get storage(): Storage | null {
    if (!this.isBrowser) {
      return null;
    }

    try {
      return window.localStorage;
    } catch {
      return null;
    }
  }

  initTheme(): void {
    if (!this.isBrowser) {
      return;
    }

    const savedTheme = this.storage?.getItem(this.storageKey);
    const prefersDark =
      typeof window.matchMedia === 'function' && window.matchMedia('(prefers-color-scheme: dark)').matches;
    const isDark = savedTheme === 'dark' || (!savedTheme && prefersDark);

    this.applyTheme(isDark ? 'dark' : 'light');
  }

  toggleTheme(): void {
    if (!this.isBrowser) {
      return;
    }

    const currentTheme = this.getCurrentTheme();
    this.applyTheme(currentTheme === 'dark' ? 'light' : 'dark');
  }

  getCurrentTheme(): 'light' | 'dark' {
    if (!this.isBrowser) {
      return 'light';
    }

    return (this.storage?.getItem(this.storageKey) as 'light' | 'dark') || 'light';
  }

  private applyTheme(theme: 'light' | 'dark'): void {
    if (!this.isBrowser) {
      return;
    }

    const html = document.documentElement;
    const isDark = theme === 'dark';

    html.classList.toggle('dark', isDark);
    html.setAttribute('data-theme', theme);
    html.style.colorScheme = theme;

    this.storage?.setItem(this.storageKey, theme);
  }
}
