import type { PluginListenerHandle } from '@capacitor/core';

export type AvailableIconTypes = 'Bitmap' | 'Resource';

export interface ShortcutItem {
  /**
   * ID of the shortcut
   */
  id: string;
  /**
   * Sets the short title of a shortcut.
   * This is a mandatory field when publishing a new shortcut with ShortcutManager.addDynamicShortcuts(List) or ShortcutManager.setDynamicShortcuts(List).
   * This field is intended to be a concise description of a shortcut.
   * The recommended maximum length is 10 characters.
   */
  shortLabel: string;
  /**
   * Sets the text of a shortcut.
   * This field is intended to be more descriptive than the shortcut title. The launcher shows this instead of the short title when it has enough space.
   * The recommend maximum length is 25 characters.
   */
  longLabel: string;
  /**
   * Defines the icon of the shortcut.
   * You can set the icon as a BASE64-Bitmap or as a Resource name
   */
  urlIcon: string;
  /**
   * Data that is passed to the 'shortcut' event
   */
  data: string;
}

export interface AndroidShortcutsPlugin {
  /**
   * Checks if pinned shortcuts are supported on the device
   */
  isPinnedSupported(): Promise<{ result: boolean }>;
  /**
   * Add a pinned shortcut
   * @param options An option object for the pinned shortcut
   */
  pin(options: ShortcutItem): Promise<void>;
  /**
   * Add a listener to a shortcut tap event
   * @param eventName
   * @param listenerFunc
   */
  addListener(
    eventName: 'shortcut',
    listenerFunc: (response: { data: string }) => void,
  ): Promise<PluginListenerHandle>;
  /**
   * Removes all listeners.
   */
  removeAllListeners(): Promise<void>;
}
