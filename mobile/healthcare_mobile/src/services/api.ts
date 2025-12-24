import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { API_CONFIG, API_RESPONSE_CODES } from '../constants/api';
import { ApiResponse } from '../types';
import { secureStorage } from '../utils/secureStorage';
import { ErrorHandler } from '../utils/errorHandler';
import { retry, shouldRetryNetworkError, shouldRetryServerError } from '../utils/retryHandler';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: API_CONFIG.baseURL,
      timeout: API_CONFIG.timeout,
      headers: API_CONFIG.headers,
      maxRedirects: 5,
      validateStatus: (status) => status < 500,
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    this.api.interceptors.request.use(
      async (config) => {
        try {
          const token = await secureStorage.getAccessToken();
          if (token) {
            config.headers.Authorization = `Bearer ${token}`;
          }
        } catch (error) {
          const appError = ErrorHandler.normalizeError(error);
          return Promise.reject(appError);
        }
        return config;
      },
      (error) => {
        const appError = ErrorHandler.normalizeError(error);
        return Promise.reject(appError);
      }
    );

    this.api.interceptors.response.use(
      (response: AxiosResponse<ApiResponse>) => {
        if (response.data.resultCode !== API_RESPONSE_CODES.SUCCESS) {
          const errorMessage = ErrorHandler.getApiErrorMessage(response.data.resultCode);
          const error = new Error(errorMessage);
          (error as any).resultCode = response.data.resultCode;
          return Promise.reject(error);
        }
        return response;
      },
      async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest?._retry) {
          originalRequest._retry = true;

          try {
            const refreshToken = await secureStorage.getRefreshToken();
            if (refreshToken) {
              const response = await this.api.post('/auth/v1/refresh', {
                refreshToken,
              });

              if (response.data.resultCode === API_RESPONSE_CODES.SUCCESS) {
                const { accessToken, refreshToken: newRefreshToken } = response.data.resultData;
                await secureStorage.setToken(accessToken, newRefreshToken);

                originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                return this.api(originalRequest);
              }
            }
          } catch (refreshError) {
            await secureStorage.clearAll();
            const appError = ErrorHandler.normalizeError(refreshError);
            return Promise.reject(appError);
          }
        }

        const appError = ErrorHandler.normalizeError(error);
        return Promise.reject(appError);
      }
    );
  }

  async get<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return retry(
      async () => {
        const response = await this.api.get<ApiResponse<T>>(url, config);
        return response.data;
      },
      {
        shouldRetry: (error) => shouldRetryNetworkError(error) || shouldRetryServerError(error),
      }
    );
  }

  async post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return retry(
      async () => {
        const response = await this.api.post<ApiResponse<T>>(url, data, config);
        return response.data;
      },
      {
        shouldRetry: (error) => shouldRetryNetworkError(error) || shouldRetryServerError(error),
      }
    );
  }

  async put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return retry(
      async () => {
        const response = await this.api.put<ApiResponse<T>>(url, data, config);
        return response.data;
      },
      {
        shouldRetry: (error) => shouldRetryNetworkError(error) || shouldRetryServerError(error),
      }
    );
  }

  async delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return retry(
      async () => {
        const response = await this.api.delete<ApiResponse<T>>(url, config);
        return response.data;
      },
      {
        shouldRetry: (error) => shouldRetryNetworkError(error) || shouldRetryServerError(error),
      }
    );
  }
}

export const apiService = new ApiService();

