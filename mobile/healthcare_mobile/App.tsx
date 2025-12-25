import React, { useEffect } from 'react';
import { Provider } from 'react-redux';
import { StatusBar } from 'expo-status-bar';
import { store } from './src/store';
import AppNavigator from './src/navigation/AppNavigator';
import NetworkStatus from './src/components/NetworkStatus';
import { ErrorBoundary } from './src/components/ErrorBoundary';
import { secureStorage } from './src/utils/secureStorage';

export default function App() {
  useEffect(() => {
    secureStorage.initialize();
  }, []);

  return (
    <ErrorBoundary>
      <Provider store={store}>
        <StatusBar style="auto" />
        <AppNavigator />
        <NetworkStatus />
      </Provider>
    </ErrorBoundary>
  );
}
