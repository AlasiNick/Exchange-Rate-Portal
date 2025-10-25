import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageLayout } from '../../layouts/page-layout/page-layout';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [PageLayout],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomePage { }
