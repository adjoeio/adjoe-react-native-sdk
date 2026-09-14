import RNPlaytimeSdk from './specs/NativeRNPlaytimeSdk';
import { wrapPlaytimePromise } from './PlaytimeException';

const getPlaytimeParams = (params, uaChannel) => {
  // if first params is an object then proceed as playtimeParams
  // otherwise first params is uaNetwork
  var playtimeParams = {};
  if (typeof params === "object" && params !== null) {
    playtimeParams = params;
  } else if (typeof params === "string") {
    playtimeParams["uaNetwork"] = params;
    if (uaChannel !== null) {
      playtimeParams["uaChannel"] = uaChannel;
    }
  }
  return playtimeParams;
};

export const PlaytimeCampaignsState = Object.freeze({
  READY: 0,
  BLOCKED: 1,
  VPN_DETECTED: 2,
  GEO_MISMATCH: 3,
  NOT_INITIALIZED: 4,
});

export const PlaytimeGender = Object.freeze({
  MALE: 0,
  FEMALE: 1,
  UNKNOWN: 2,
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

export const CatalogPlacement = Object.freeze({
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

export const AppCategory = Object.freeze({
  GAMING: 'gaming',
  NON_GAMING: 'non-gaming',
  BOTH: 'both',
});

const isCustomValue = (value, predefined) =>
  !Object.values(predefined).includes(value);

const sealedValue = (value, predefined, supportsCustom) => {
  if (value === null || value === undefined) {
    return value;
  }
  // Already normalized (or a value built by an earlier call) -- keep it as is so
  // normalization stays idempotent.
  if (typeof value === 'object') {
    return value;
  }
  if (!supportsCustom) {
    return { value };
  }
  return { value, isCustom: isCustomValue(value, predefined) };
};

const SEALED_OPTION_FIELDS = Object.freeze({
  placement: [StudioPlacement, true],
  category: [AppCategory, false],
  displayFormat: [DisplayFormat, true],
});

export const normalizeSealedOptions = (options) => {
  if (options === null || options === undefined || typeof options !== 'object') {
    return options;
  }
  const normalized = { ...options };
  for (const [key, [predefined, supportsCustom]] of Object.entries(SEALED_OPTION_FIELDS)) {
    if (key in normalized) {
      normalized[key] = sealedValue(normalized[key], predefined, supportsCustom);
    }
  }
  return normalized;
};

// endregion

export default {
  initPlaytime: function(options) {
    return wrapPlaytimePromise(RNPlaytimeSdk.initPlaytime(options));
  },

  showCatalog: function(catalogOptions) {
    return wrapPlaytimePromise(RNPlaytimeSdk.showCatalog(normalizeSealedOptions(catalogOptions)));
  },

  teardown: function() {
    return wrapPlaytimePromise(RNPlaytimeSdk.teardown());
  },

  getStatus: function() {
    return wrapPlaytimePromise(RNPlaytimeSdk.getStatus());
  },

  executeImpressionEvent: function(catalogPlacement) {
    return wrapPlaytimePromise(
      RNPlaytimeSdk.executeImpressionEvent(sealedValue(catalogPlacement, CatalogPlacement, true))
    );
  },
};
