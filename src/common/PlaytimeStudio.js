import { NativeModules, Platform } from 'react-native';

const PlaytimeStudio = Platform.select({
  ios: NativeModules.PlaytimeStudio,
  android: NativeModules.PlaytimeStudio,
});

export default PlaytimeStudio;