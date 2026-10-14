import RNPlaytimeSdk from './specs/NativeRNPlaytimeSdk';
import { wrapPlaytimePromise } from './PlaytimeException';
import { DisplayFormat } from './PlaytimeStudio';
import { NativeEventEmitter } from 'react-native';

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

// TODO: currently there is a known issue here, placement key is used for both
// CatalogPlacement & StudioPlacement. Since both models have the same values 
// it is not a issue currently, but this should be addressed at some point.
const SEALED_OPTION_FIELDS = Object.freeze({
  placement: [CatalogPlacement, true],
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

// region Playtime SDK events
//
// A publisher-supplied listener object cannot cross the native bridge, so events are
// delivered through a NativeEventEmitter instead: the native side emits a device event
// under PLAYTIME_EVENT_NAME, and this module forwards each one to the registered
// listener's `didReceive` callback. The hand-written logic below is NOT regenerated.

const PLAYTIME_EVENT_NAME = 'onPlaytimeEvent';

const playtimeEventEmitter = new NativeEventEmitter(RNPlaytimeSdk);

// The JS-side subscription bridging the native device event to the publisher's listener.
let playtimeEventSubscription = null;

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

  addEventListener: function(eventListener) {
    if (playtimeEventSubscription !== null) {
      playtimeEventSubscription.remove();
      playtimeEventSubscription = null;
    }
    playtimeEventSubscription = playtimeEventEmitter.addListener(
      PLAYTIME_EVENT_NAME,
      function(event) {
        eventListener.didReceive(event);
      }
    );
    return wrapPlaytimePromise(RNPlaytimeSdk.addEventListener());
  },

  removeEventListener: function() {
    if (playtimeEventSubscription !== null) {
      playtimeEventSubscription.remove();
      playtimeEventSubscription = null;
    }
    return wrapPlaytimePromise(RNPlaytimeSdk.removeEventListener());
  },

  executeImpressionEvent: function(catalogPlacement) {
    return wrapPlaytimePromise(
      RNPlaytimeSdk.executeImpressionEvent(sealedValue(catalogPlacement, CatalogPlacement, true))
    );
  },
};
