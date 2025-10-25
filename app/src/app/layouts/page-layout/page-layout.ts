import { ChangeDetectionStrategy, Component, signal } from '@angular/core';

import { LayoutComponent } from '@shared/components/layout/layout.component';
import { ContentComponent } from '@shared/components/layout/content.component';
import { SidebarComponent } from '@shared/components/layout/sidebar.component';
import { SidebarGroupComponent, SidebarGroupLabelComponent } from '@shared/components/layout/sidebar.component';
import { ZardButtonComponent } from '@shared/components/button/button.component';
import { ZardTooltipModule } from '@shared/components/tooltip/tooltip';
import { ZardMenuModule } from '@shared/components/menu/menu.module';
import { ZardSkeletonComponent } from '@shared/components/skeleton/skeleton.component';
import { ZardDividerComponent } from '@shared/components/divider/divider.component';
import { ZardAvatarComponent } from '@shared/components/avatar/avatar.component';
import { ZardBreadcrumbModule } from '@shared/components/breadcrumb/breadcrumb.module';
import { ZardIconComponent } from '@shared/components/icon/icon.component';

interface MenuItem {
  label: string;
  icon: string;
  submenu?: Array<{ label: string }>;
}

@Component({
  selector: 'app-page-layout',
  standalone: true,
  imports: [
    LayoutComponent,
    SidebarComponent,
    SidebarGroupComponent,
    SidebarGroupLabelComponent,
    ContentComponent,
    ZardButtonComponent,
    ZardTooltipModule,
    ZardMenuModule,
    ZardSkeletonComponent,
    ZardDividerComponent,
    ZardAvatarComponent,
    ZardBreadcrumbModule,
    ZardIconComponent,
  ],
  templateUrl: './page-layout.html',
  styleUrl: './page-layout.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PageLayout {
  protected readonly sidebarCollapsed = signal(false);

  protected readonly mainMenuItems: MenuItem[] = [
    { label: 'Dashboard', icon: 'icon-layout-dashboard' },
    { label: 'Projects', icon: 'icon-folder' },
    { label: 'Tasks', icon: 'icon-list-checks' },
    { label: 'Teams', icon: 'icon-users' },
  ];

  protected readonly workspaceMenuItems: MenuItem[] = [
    {
      label: 'Settings',
      icon: 'icon-settings',
      submenu: [{ label: 'General' }, { label: 'Billing' }, { label: 'Integrations' }],
    },
    {
      label: 'Notifications',
      icon: 'icon-bell',
      submenu: [{ label: 'Email' }, { label: 'In-app' }, { label: 'Security' }],
    },
    { label: 'Help Center', icon: 'icon-life-buoy' },
  ];

  protected readonly avatar = { fallback: 'ZU' };

  protected toggleSidebar(): void {
    this.sidebarCollapsed.update((collapsed) => !collapsed);
  }

  protected onCollapsedChange(collapsed: boolean): void {
    this.sidebarCollapsed.set(collapsed);
  }
}
