import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  getVersionCode(): Promise<number>;
  downloadApp(apkUrl: string): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('Updater');
