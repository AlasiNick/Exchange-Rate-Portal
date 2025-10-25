import { Routes } from '@angular/router';

import { HomePage } from './pages/home/home';
import { Calculator } from './pages/calculator/calculator';
import { HistoricalData } from './pages/historical-data/historical-data';
import { PageLayout } from './layouts/page-layout/page-layout';

export const routes: Routes = [
  {
    path: '',
    component: PageLayout,
    children: [
      {
        path: '',
        component: HomePage,
      },
      {
        path: 'calculator',
        component: Calculator,
      },
      {
        path: 'historical-data',
        component: HistoricalData,
      },
    ],
  },
];
