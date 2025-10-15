import Constants from 'expo-constants';
import { SERVICE_PORTS, ENV_CONFIG } from '../constants/api';

const getEnvVar = (key: string, defaultValue: string = ''): string => {
  return Constants.expoConfig?.extra?.[key] || defaultValue;
};


export const getServiceUrl = (service: keyof typeof SERVICE_PORTS, host: string = 'localhost'): string => {
  const port = SERVICE_PORTS[service];
  return `http://${host}:${port}`;
};

export const getApiGatewayUrl = (host: string = 'localhost'): string => {
  return getServiceUrl('GATEWAY', host);
};

export const getAuthServiceUrl = (host: string = 'localhost'): string => {
  return getServiceUrl('AUTH', host);
};

export const getHealthcareServiceUrl = (host: string = 'localhost'): string => {
  return getServiceUrl('HEALTHCARE', host);
};

export const getCommunityServiceUrl = (host: string = 'localhost'): string => {
  return getServiceUrl('COMMUNITY', host);
};

export const getUserManagementServiceUrl = (host: string = 'localhost'): string => {
  return getServiceUrl('USERMANAGEMENT', host);
};

export const getWebHealthcareServiceUrl = (host: string = 'localhost'): string => {
  return getServiceUrl('WEB', host);
};

export const isDevelopment = (): boolean => {
  return ENV_CONFIG.ENVIRONMENT === 'development';
};

export const isProduction = (): boolean => {
  return ENV_CONFIG.ENVIRONMENT === 'production';
};

export const isDebugMode = (): boolean => {
  return ENV_CONFIG.DEBUG_MODE;
};

export const getJwtConfig = () => {
  return {
    secret: ENV_CONFIG.JWT_SECRET,
    accessTokenExpiry: ENV_CONFIG.ACCESS_TOKEN_EXPIRY,
    refreshTokenExpiry: ENV_CONFIG.REFRESH_TOKEN_EXPIRY,
  };
};

export const getAllServicePorts = () => {
  return {
    discovery: SERVICE_PORTS.DISCOVERY,
    config: SERVICE_PORTS.CONFIG,
    gateway: SERVICE_PORTS.GATEWAY,
    auth: SERVICE_PORTS.AUTH,
    healthcare: SERVICE_PORTS.HEALTHCARE,
    community: SERVICE_PORTS.COMMUNITY,
    usermanagement: SERVICE_PORTS.USERMANAGEMENT,
    web: SERVICE_PORTS.WEB,
  };
};

export const validateEnvConfig = (): { isValid: boolean; errors: string[] } => {
  const errors: string[] = [];
  
  if (!getEnvVar('EXPO_PUBLIC_API_BASE_URL')) {
    errors.push('EXPO_PUBLIC_API_BASE_URL is required');
  }
  
  if (!getEnvVar('EXPO_PUBLIC_SERVICE_AUTH_PORT')) {
    errors.push('EXPO_PUBLIC_SERVICE_AUTH_PORT is required');
  }
  
  if (!getEnvVar('EXPO_PUBLIC_SERVICE_HEALTHCARE_PORT')) {
    errors.push('EXPO_PUBLIC_SERVICE_HEALTHCARE_PORT is required');
  }
  
  if (!getEnvVar('EXPO_PUBLIC_SERVICE_COMMUNITY_PORT')) {
    errors.push('EXPO_PUBLIC_SERVICE_COMMUNITY_PORT is required');
  }
  
  if (!getEnvVar('EXPO_PUBLIC_SERVICE_USERMANAGEMENT_PORT')) {
    errors.push('EXPO_PUBLIC_SERVICE_USERMANAGEMENT_PORT is required');
  }
  
  return {
    isValid: errors.length === 0,
    errors,
  };
};
