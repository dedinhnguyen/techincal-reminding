import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { SearchService } from '../../../services/search.service';
import { InfraService } from '../../../services/infra.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  private readonly searchService = inject(SearchService);
  private readonly infraService = inject(InfraService);

  openCommandPalette(): void {
    this.searchService.toggleCommandPalette(true);
  }

  toggleInfraHud(): void {
    this.infraService.toggleHud();
  }
}
