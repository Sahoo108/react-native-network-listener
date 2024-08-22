import { NativeModules } from 'react-native';
const { AppNetworkStateModule } = NativeModules;
interface AppNetworkStateModule {
  getNativeNetworkState: () => Record<string, string>;
}
export default AppNetworkStateModule as AppNetworkStateModule;
