import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  faceVerification(): Promise<string>;
  faceVerificationStatus(): Promise<string>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('PlaytimeProtection');
