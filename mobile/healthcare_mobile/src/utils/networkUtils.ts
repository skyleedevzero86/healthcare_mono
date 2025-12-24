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
    console.error('네트워크 확인 실패:', error);
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
    return '인터넷 연결이 없습니다. 네트워크 설정을 확인해주세요.';
  }
  
  if (networkState.isInternetReachable === false) {
    return '인터넷에 연결할 수 없습니다. 연결을 확인해주세요.';
  }
  
  return '네트워크 연결이 정상입니다.';
};

