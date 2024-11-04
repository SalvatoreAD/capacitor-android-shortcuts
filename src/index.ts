import { registerPlugin } from '@capacitor/core';

import type { AndroidShortcutsPlugin } from './definitions';

const AndroidShortcuts =
  registerPlugin<AndroidShortcutsPlugin>('AndroidShortcuts');

export * from './definitions';
export { AndroidShortcuts };
