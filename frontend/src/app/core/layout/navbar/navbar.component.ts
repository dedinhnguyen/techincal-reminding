import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { SearchService } from '../../../services/search.service';
import { InfraService } from '../../../services/infra.service';
import { ThemeService, ThemeMode } from '../../../services/theme.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent {
  readonly searchService = inject(SearchService);
  readonly infraService = inject(InfraService);
  readonly themeService = inject(ThemeService);

  openCommandPalette(): void {
    this.searchService.toggleCommandPalette(true);
  }

  toggleInfraHud(): void {
    this.infraService.toggleHud();
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  setTheme(mode: ThemeMode): void {
    this.themeService.setTheme(mode);
  }
}
