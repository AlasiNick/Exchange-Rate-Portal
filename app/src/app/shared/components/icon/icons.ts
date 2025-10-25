import {
  Home,
  User,
  Settings,
  Search,
  Menu,
  X,
  ChevronDown,
  ChevronUp,
  ChevronLeft,
  ChevronRight,
  LoaderCircle,
  Ellipsis,
  LucideAngularModule,
  Calendar,
  ChevronsUpDown,
  Check,
  LucideIconData,
  LayoutDashboard,
  Bell,
  // Add more icons as needed
} from 'lucide-angular';

export const ZARD_ICONS = {
  home: Home,
  user: User,
  settings: Settings,
  search: Search,
  menu: Menu,
  x: X,
  calendar: Calendar,
  check: Check,
  bell: Bell,
  'chevron-down': ChevronDown,
  'chevron-up': ChevronUp,
  'chevron-left': ChevronLeft,
  'chevron-right': ChevronRight,
  'loader-circle': LoaderCircle,
  ellipsis: Ellipsis,
  'layout-dashboard': LayoutDashboard,
  'chevrons-up-down': ChevronsUpDown,
  // Add more mappings as you need them
} as const;

export type ZardIcon = keyof typeof ZARD_ICONS | LucideIconData;
