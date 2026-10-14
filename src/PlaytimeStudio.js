import PlaytimeStudioSpec from './specs/NativePlaytimeStudio';
import { wrapPlaytimePromise } from './PlaytimeException';
import { normalizeSealedOptions } from './Playtime';

export const PlaytimeCampaignStatus = Object.freeze({
  AVAILABLE: 0,
  PENDING: 1,
  INSTALLED: 2,
  FAILED: 3,
});

export const PlaytimeImpressionType = Object.freeze({
  DEFAULT: 0,
  ENGAGED: 1,
});

export const TosConsentAction = Object.freeze({
  ACCEPT: 0,
  WITHDRAW: 1,
});

export const StudioPlacement = Object.freeze({
  Navbar: 'navbar',
  Icon: 'icon',
  EarnTab: 'earn_tab',
  GameTab: 'game_tab',
  HomePage: 'home_page',
  NotificationInbox: 'notification_inbox',
  Notification: 'notification',
  MiniGame: 'mini_game',
  OrderPage: 'order_page',
  RedeemSection: 'redeem_section',
  ScanReceipt: 'scan_receipt',
  StepConverter: 'step_converter',
});

export const DisplayFormat = Object.freeze({
  Icon: 'icon',
  Notification: 'notification',
  NotificationInbox: 'notification_inbox',
  Carousel: 'carousel',
  CustomCatalog: 'custom_catalog',
  Banner: 'banner',
  Popup: 'popup',
  Interstitial: 'interstitial',
});

const PlaytimeStudio = {
  updateTosConsent: function(action) {
    return wrapPlaytimePromise(PlaytimeStudioSpec.updateTosConsent(action));
  },

  getCampaigns: function(campaignOptions) {
    return wrapPlaytimePromise(PlaytimeStudioSpec.getCampaigns(normalizeSealedOptions(campaignOptions)));
  },

  getOfferDetails: function(tokens, campaignOptions) {
    return wrapPlaytimePromise(PlaytimeStudioSpec.getOfferDetails(tokens, normalizeSealedOptions(campaignOptions)));
  },

  getInstalledCampaigns: function(options) {
    return wrapPlaytimePromise(PlaytimeStudioSpec.getInstalledCampaigns(normalizeSealedOptions(options)));
  },

  openInStore: function(campaign, useInlineInstall) {
    return wrapPlaytimePromise(PlaytimeStudioSpec.openInStore(campaign, useInlineInstall));
  },

  openInstalledCampaign: function(campaign) {
    return wrapPlaytimePromise(PlaytimeStudioSpec.openInstalledCampaign(campaign));
  },

  showPermissionsPrompt: function() {
    return wrapPlaytimePromise(PlaytimeStudioSpec.showPermissionsPrompt());
  },

  registerRewardsConnect: function() {
    return wrapPlaytimePromise(PlaytimeStudioSpec.registerRewardsConnect());
  },

  resetRewardsConnect: function() {
    return wrapPlaytimePromise(PlaytimeStudioSpec.resetRewardsConnect());
  },

  executeCampaignImpression: function(campaign, impressionType) {
    return wrapPlaytimePromise(PlaytimeStudioSpec.executeCampaignImpression(campaign, impressionType));
  },

  executeOfferImpression: function(token, appID) {
    return wrapPlaytimePromise(PlaytimeStudioSpec.executeOfferImpression(token, appID));
  },

  showCampaignDetails: function(campaign) {
    return wrapPlaytimePromise(PlaytimeStudioSpec.showCampaignDetails(campaign));
  },

  showOfferDetails: function(token, appID) {
    return wrapPlaytimePromise(PlaytimeStudioSpec.showOfferDetails(token, appID));
  },

  showInstalledCampaigns: function() {
    return wrapPlaytimePromise(PlaytimeStudioSpec.showInstalledCampaigns());
  },

  openChatbot: function(campaign = null) {
    return wrapPlaytimePromise(PlaytimeStudioSpec.openChatbot(campaign));
  },
};

export default PlaytimeStudio;
