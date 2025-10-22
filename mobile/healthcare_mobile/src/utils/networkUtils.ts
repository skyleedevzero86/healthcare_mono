import NetInfo from '@react-native-community/netinfo';

export interface NetworkState {
  isConnected: boolean;
  type: string | null;
  isInternetReachable: boolean | null;
}

export const checkNetworkConnection = async (): Promise<NetworkState> => {
  try {
    const state = await NetInfo.fetch();
    return {
      isConnected: state.isConnected ?? false,
      type: state.type,
      isInternetReachable: state.isInternetReachable,
    };
  } catch (error) {
    console.error('Network check failed:', error);
    return {
      isConnected: false,
      type: null,
      isInternetReachable: false,
    };
  }
};

export const isNetworkAvailable = async (): Promise<boolean> => {
  const networkState = await checkNetworkConnection();
  return networkState.isConnected && networkState.isInternetReachable === true;
};

export const getNetworkErrorMessage = (networkState: NetworkState): string => {
  if (!networkState.isConnected) {
    return 'No internet connection. Please check your network settings.';
  }
  
  if (networkState.isInternetReachable === false) {
    return 'Internet is not reachable. Please check your connection.';
  }
  
  return 'Network connection is available.';
};

