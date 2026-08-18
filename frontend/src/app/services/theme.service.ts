import { Injectable, signal, computed, effect } from '@angular/core';

export type ThemeMode = 'dark' | 'light' | 'system';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  private readonly THEME_KEY = 'devcompanion_theme';

  // Writable signal for the selected theme mode
  readonly themeMode = signal<ThemeMode>(this.getInitialTheme());

  // Computed signal for whether dark mode is currently active
  readonly isDark = computed(() => {
    const mode = this.themeMode();
    if (mode === 'system') {
      return window.matchMedia('(prefers-color-scheme: dark)').matches;
    }
    return mode === 'dark';
  });

  constructor() {
    // Effect to apply the theme class to <html> whenever themeMode or isDark changes
    effect(() => {
      const dark = this.isDark();
      const mode = this.themeMode();
      this.applyTheme(dark, mode);
    });

    // Listen for OS system theme changes
    if (typeof window !== 'undefined') {
      window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
        if (this.themeMode() === 'system') {
          this.applyTheme(e.matches, 'system');
        }
      });
    }
  }

  setTheme(mode: ThemeMode): void {
    this.themeMode.set(mode);
    localStorage.setItem(this.THEME_KEY, mode);
  }

  toggleTheme(): void {
    const nextMode: ThemeMode = this.isDark() ? 'light' : 'dark';
    this.setTheme(nextMode);
  }

  private getInitialTheme(): ThemeMode {
    if (typeof window !== 'undefined') {
      const saved = localStorage.getItem(this.THEME_KEY) as ThemeMode | null;
      if (saved && ['dark', 'light', 'system'].includes(saved)) {
        return saved;
      }
      return 'dark'; // Dark theme default for developer companion aesthetic
    }
    return 'dark';
  }

  private applyTheme(isDark: boolean, mode: ThemeMode): void {
    if (typeof document === 'undefined') return;

    const root = document.documentElement;
    if (isDark) {
      root.classList.add('dark');
      root.classList.remove('light');
      root.setAttribute('data-theme', 'dark');
      root.style.colorScheme = 'dark';
    } else {
      root.classList.remove('dark');
      root.classList.add('light');
      root.setAttribute('data-theme', 'light');
      root.style.colorScheme = 'light';
    }
  }
}
