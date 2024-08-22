import { useEffect } from 'react';
import { NativeModules, NativeEventEmitter } from 'react-native';

export interface NetworkStateCallback {
  onStateChange: (state: NetworkState) => void;
}

enum NetworkState {
  AVAILABLE = 'AVAILABLE',
  LOST = 'LOST',
  LOSING = 'LOSING',
  BLOCK_STATE_CHANGE = 'BLOCK_STATE_CHANGE',
  UNAVAILABLE = 'UNAVAILABLE',
}

const { NetworkListenerModule } = NativeModules;

function useListenNetworkState(prop: NetworkStateCallback): void {
  useEffect(() => {
    const eventEmitter = new NativeEventEmitter(NetworkListenerModule);
    eventEmitter.addListener('onNetworkStateChange', (event: any) => {
      switch (event.state) {
        case NetworkState.AVAILABLE:
          prop.onStateChange(NetworkState.AVAILABLE);
          break;
        case NetworkState.LOST:
          prop.onStateChange(NetworkState.LOST);
          break;
        case NetworkState.LOSING:
          prop.onStateChange(NetworkState.LOSING);
          break;
        case NetworkState.BLOCK_STATE_CHANGE:
          prop.onStateChange(NetworkState.BLOCK_STATE_CHANGE);
          break;
        case NetworkState.UNAVAILABLE:
          prop.onStateChange(NetworkState.UNAVAILABLE);
          break;
        default:
          break;
      }
    });
  }, []);
}

export default useListenNetworkState;
