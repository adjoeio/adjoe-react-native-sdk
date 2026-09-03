import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';
import type { UnsafeObject } from 'react-native/Libraries/Types/CodegenTypes';

export interface Spec extends TurboModule {
  updateTosConsent(action: number): Promise<void>;
  getCampaigns(campaignOptions: UnsafeObject): Promise<UnsafeObject>;
  getOfferDetails(tokens: Array<string>, campaignOptions: UnsafeObject): Promise<UnsafeObject>;
  getInstalledCampaigns(options: UnsafeObject): Promise<UnsafeObject>;
  openInStore(campaign: UnsafeObject, useInlineInstall: boolean): Promise<void>;
  openInstalledCampaign(campaign: UnsafeObject): Promise<void>;
  openChatbot(campaign: UnsafeObject | null): Promise<void>;
  resetRewardsConnect(): Promise<void>;
  executeCampaignImpression(campaign: UnsafeObject, impressionType: number): Promise<void>;
  executeOfferImpression(token: string, appID: string): Promise<void>;
  registerRewardsConnect(): Promise<void>;
  showCampaignDetails(campaign: UnsafeObject): Promise<void>;
  showOfferDetails(token: string, appID: string): Promise<void>;
  showInstalledCampaigns(): Promise<void>;
  showPermissionsPrompt(): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('PlaytimeStudio');
