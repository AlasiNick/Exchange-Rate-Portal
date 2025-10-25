import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { ZardAvatarComponent } from '@shared/components/avatar/avatar.component';
import { ZardButtonComponent } from '@shared/components/button/button.component';
import { ZardBreadcrumbModule } from '@shared/components/breadcrumb/breadcrumb.module';
import { ZardMenuModule } from '@shared/components/menu/menu.module';
import { LayoutModule } from '@shared/components/layout/layout.module';
import { ZardTooltipModule } from '@shared/components/tooltip/tooltip';
import { ZardDividerComponent } from '@shared/components/divider/divider.component';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { ZardIconComponent } from '@shared/components/icon/icon.component';

@Component({
  selector: 'app-page-layout',
  imports: [LayoutModule, ZardButtonComponent, ZardBreadcrumbModule, ZardMenuModule, ZardTooltipModule, ZardDividerComponent,
     ZardAvatarComponent, RouterOutlet, RouterModule, ZardIconComponent],
  templateUrl: './page-layout.html',
  styleUrl: './page-layout.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PageLayout { 

  year = new Date().getFullYear();
  router: Router = inject(Router);
  avatar = { fallback: 'NU', alt: 'Nicki' };

    isActive(path: string): boolean {
    return this.router.url === path;
  }
}
