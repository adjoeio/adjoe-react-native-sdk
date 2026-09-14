import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';
import type { UnsafeObject } from 'react-native/Libraries/Types/CodegenTypes';

export interface Spec extends TurboModule {
  teardown(): Promise<void>;
  getStatus(): Promise<UnsafeObject>;
  initPlaytime(options: UnsafeObject): Promise<void>;
  showCatalog(catalogOptions: UnsafeObject): Promise<void>;
  executeImpressionEvent(catalogPlacement: UnsafeObject): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNPlaytimeSdk');
