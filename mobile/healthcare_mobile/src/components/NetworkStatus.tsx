import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { checkNetworkConnection, NetworkState } from '../utils/networkUtils';

interface NetworkStatusProps {
  onRetry?: () => void;
}

const NetworkStatus: React.FC<NetworkStatusProps> = ({ onRetry }) => {
  const [networkState, setNetworkState] = useState<NetworkState>({
    isConnected: true,
    type: null,
    isInternetReachable: true,
  });
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    const checkConnection = async () => {
      const state = await checkNetworkConnection();
      setNetworkState(state);
      setIsVisible(!state.isConnected || state.isInternetReachable === false);
    };

    checkConnection();
    
    const interval = setInterval(checkConnection, 5000);
    return () => clearInterval(interval);
  }, []);

  if (!isVisible) {
    return null;
  }

  return (
    <View style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.title}>네트워크 연결 문제</Text>
        <Text style={styles.message}>
          {!networkState.isConnected 
            ? '인터넷 연결이 감지되지 않았습니다.'
            : '인터넷에 연결할 수 없습니다.'
          }
        </Text>
        <Text style={styles.subMessage}>
          네트워크 설정을 확인하고 다시 시도해주세요.
        </Text>
        {onRetry && (
          <TouchableOpacity style={styles.retryButton} onPress={onRetry}>
            <Text style={styles.retryButtonText}>다시 시도</Text>
          </TouchableOpacity>
        )}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    backgroundColor: '#ff4444',
    zIndex: 1000,
  },
  content: {
    padding: 16,
    alignItems: 'center',
  },
  title: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 8,
  },
  message: {
    fontSize: 14,
    color: '#fff',
    textAlign: 'center',
    marginBottom: 4,
  },
  subMessage: {
    fontSize: 12,
    color: '#fff',
    textAlign: 'center',
    opacity: 0.8,
  },
  retryButton: {
    marginTop: 12,
    paddingHorizontal: 20,
    paddingVertical: 8,
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    borderRadius: 4,
  },
  retryButtonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
});

export default NetworkStatus;

