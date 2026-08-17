import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './core/layout/navbar/navbar.component';
import { CommandPaletteComponent } from './core/layout/command-palette/command-palette.component';
import { InfraHudComponent } from './core/layout/infra-hud/infra-hud.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    NavbarComponent,
    CommandPaletteComponent,
    InfraHudComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {}
