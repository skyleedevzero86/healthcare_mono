import Constants from 'expo-constants';

export interface ExpoEnvironmentInfo {
  isExpoGo: boolean;
  isDevelopmentBuild: boolean;
  isProduction: boolean;
  platform: 'ios' | 'android' | 'web';
  version: string;
}

export const getExpoEnvironment = (): ExpoEnvironmentInfo => {
  const isExpoGo = Constants.appOwnership === 'expo';
  const isDevelopmentBuild = Constants.appOwnership === 'development';
  const isProduction = Constants.appOwnership === 'store';
  
  return {
    isExpoGo,
    isDevelopmentBuild,
    isProduction,
    platform: Constants.platform?.ios ? 'ios' : Constants.platform?.android ? 'android' : 'web',
    version: Constants.expoConfig?.version || '1.0.0',
  };
};

export const isExpoGo = (): boolean => {
  return getExpoEnvironment().isExpoGo;
};

export const isDevelopmentBuild = (): boolean => {
  return getExpoEnvironment().isDevelopmentBuild;
};

export const isProduction = (): boolean => {
  return getExpoEnvironment().isProduction;
};

export const getPlatform = (): 'ios' | 'android' | 'web' => {
  return getExpoEnvironment().platform;
};

export const getAppVersion = (): string => {
  return getExpoEnvironment().version;
};

