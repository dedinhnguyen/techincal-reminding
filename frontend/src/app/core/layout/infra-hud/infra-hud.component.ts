import { Component, OnInit, inject } from '@angular/core';
import { JsonPipe, KeyValuePipe } from '@angular/common';
import { InfraService } from '../../../services/infra.service';

@Component({
  selector: 'app-infra-hud',
  standalone: true,
  imports: [KeyValuePipe],
  templateUrl: './infra-hud.component.html',
  styleUrl: './infra-hud.component.css',
})
export class InfraHudComponent implements OnInit {
  readonly infraService = inject(InfraService);

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.infraService.fetchHealth().subscribe();
  }

  close(): void {
    this.infraService.toggleHud(false);
  }
}
