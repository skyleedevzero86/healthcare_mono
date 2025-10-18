import Constants from 'expo-constants';

const getEnvVar = (key: string, defaultValue: string = ''): string => {
  return Constants.expoConfig?.extra?.[key] || defaultValue;
};

export const API_CONFIG = {
  baseURL: getEnvVar('EXPO_PUBLIC_API_BASE_URL', 'http://localhost:8080'),
  timeout: parseInt(getEnvVar('EXPO_PUBLIC_API_TIMEOUT', '10000')),
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
};

export const SERVICE_PORTS = {
  DISCOVERY: getEnvVar('EXPO_PUBLIC_SERVICE_DISCOVERY_PORT', '8761'),
  CONFIG: getEnvVar('EXPO_PUBLIC_SERVICE_CONFIG_PORT', '8888'),
  GATEWAY: getEnvVar('EXPO_PUBLIC_API_GATEWAY_PORT', '8080'),
  AUTH: getEnvVar('EXPO_PUBLIC_SERVICE_AUTH_PORT', '8082'),
  HEALTHCARE: getEnvVar('EXPO_PUBLIC_SERVICE_HEALTHCARE_PORT', '8084'),
  COMMUNITY: getEnvVar('EXPO_PUBLIC_SERVICE_COMMUNITY_PORT', '8085'),
  USERMANAGEMENT: getEnvVar('EXPO_PUBLIC_SERVICE_USERMANAGEMENT_PORT', '8086'),
  WEB: getEnvVar('EXPO_PUBLIC_WEB_HEALTHCARE_PORT', '8081'),
};

export const ENV_CONFIG = {
  ENVIRONMENT: getEnvVar('EXPO_PUBLIC_ENVIRONMENT', 'development'),
  DEBUG_MODE: getEnvVar('EXPO_PUBLIC_DEBUG_MODE', 'false') === 'true',
  JWT_SECRET: getEnvVar('EXPO_PUBLIC_JWT_SECRET', 'healthcare-Mono-jwt-secret-key-for-authentication-service-2025-dev'),
  ACCESS_TOKEN_EXPIRY: parseInt(getEnvVar('EXPO_PUBLIC_ACCESS_TOKEN_EXPIRY', '3600000')),
  REFRESH_TOKEN_EXPIRY: parseInt(getEnvVar('EXPO_PUBLIC_REFRESH_TOKEN_EXPIRY', '86400000')),
};

export const API_RESPONSE_CODES = {
  SUCCESS: '0000',
  PARAM_VALID_ERR: '1001',
  AUTH_ERR: '1002',
  EXP_JWT_TOKEN_ERR: '1003',
  INVALID_JWT_TOKEN_ERR: '1004',
  DUPLICATE_KEY_ERR: '2001',
  DUPLICATE_CODE: '2002',
  RESULT_IS_EMPTY: '3001',
  UPDATE_FAIL: '3002',
  INSERT_FAIL: '3003',
  UNKOWN_ERR: '5001',
} as const;

export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500,
} as const;

export const API_ENDPOINTS = {
  AUTH: {
    SIGNIN: '/auth/v1/signin',
    SIGNUP: '/auth/v1/signup',
    REFRESH: '/auth/v1/refresh',
    LOGOUT: '/auth/v1/logout',
    DUPLICATE_ID: '/auth/v1/duplicateId',
    DUPLICATE_EMAIL: '/auth/v1/duplicateEmail',
    FIND_USER_ID: '/auth/v1/findUserId',
    FIND_USER_PW: '/auth/v1/findUserPw',
    UPDATE_USER_PW: '/auth/v1/updateUserPw',
  },
  HEALTHCARE: {
    INSERT_HEALTH_INFO: '/healthcare/v1/insertHealthInfo',
    HEALTH_INFO: '/healthcare/v1/healthInfo',
    HEALTH_INFO_CHART: '/healthcare/v1/healthInfoChart',
    REALTIME_BIODATA: '/healthcare/v1/realtimeBiodata',
    GRAPH_BIODATA: '/healthcare/v1/graphBiodata',
    DAILY_DATA: '/healthcare/v1/dailydata',
    HEALTH_SCORE_LIST: '/healthcare/v1/healthScoreList',
    CHAT_AI: '/healthcare/v1/chat_ai',
  },
  COMMUNITY: {
    WRITE_BOARD: '/community/v1/writeBoard',
    FIND_BOARD: '/community/v1/findBoard',
    FIND_BOARD_LIST: '/community/v1/findBoardList',
    UPDATE_BOARD: '/community/v1/updateBoard',
  },
  MANAGEMENT: {
    USER_INFO: '/management/v1/userInfo',
    UPDATE_USER_INFO: '/management/v1/updateUserInfo',
    DELETE_USER_INFO: '/management/v1/deleteUserInfo',
    UPDATE_PASSWD: '/management/v1/updatePasswd',
    USER_LIST: '/management/v1/list',
  },
} as const;
