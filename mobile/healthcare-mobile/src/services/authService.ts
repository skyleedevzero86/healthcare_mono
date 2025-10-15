import AsyncStorage from '@react-native-async-storage/async-storage';
import { apiService } from './api';
import { API_ENDPOINTS } from '../constants/api';
import { SigninRequest, SignupRequest, ApiResponse, JwtToken, User } from '../types';

class AuthService {
  async signin(credentials: SigninRequest): Promise<{ user: User; token: JwtToken }> {
    const response: ApiResponse<{ user: User; token: JwtToken }> = await apiService.post(
      API_ENDPOINTS.AUTH.SIGNIN,
      credentials
    );

    if (response.resultCode === '0000') {
      const { user, token } = response.resultData;
      
      await AsyncStorage.setItem('accessToken', token.accessToken);
      await AsyncStorage.setItem('refreshToken', token.refreshToken);
      await AsyncStorage.setItem('user', JSON.stringify(user));
      
      return { user, token };
    } else {
      throw new Error(response.resultMessage);
    }
  }

  async signup(userData: SignupRequest): Promise<void> {
    const response: ApiResponse<void> = await apiService.post(
      API_ENDPOINTS.AUTH.SIGNUP,
      userData
    );

    if (response.resultCode !== '0000') {
      throw new Error(response.resultMessage);
    }
  }

  async refreshToken(): Promise<JwtToken> {
    const refreshToken = await AsyncStorage.getItem('refreshToken');
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    const response: ApiResponse<JwtToken> = await apiService.post(
      API_ENDPOINTS.AUTH.REFRESH,
      { refreshToken }
    );

    if (response.resultCode === '0000') {
      const token = response.resultData;
      await AsyncStorage.setItem('accessToken', token.accessToken);
      await AsyncStorage.setItem('refreshToken', token.refreshToken);
      return token;
    } else {
      throw new Error(response.resultMessage);
    }
  }

  async logout(): Promise<void> {
    try {
      await apiService.post(API_ENDPOINTS.AUTH.LOGOUT);
    } catch (error) {
      console.warn('Logout API call failed:', error);
    } finally {
      await AsyncStorage.multiRemove(['accessToken', 'refreshToken', 'user']);
    }
  }

  async checkDuplicateId(userId: string): Promise<boolean> {
    const response: ApiResponse<{ isDuplicate: boolean }> = await apiService.post(
      API_ENDPOINTS.AUTH.DUPLICATE_ID,
      { userId }
    );

    if (response.resultCode === '0000') {
      return response.resultData.isDuplicate;
    } else {
      throw new Error(response.resultMessage);
    }
  }

  async checkDuplicateEmail(email: string): Promise<boolean> {
    const response: ApiResponse<{ isDuplicate: boolean }> = await apiService.post(
      API_ENDPOINTS.AUTH.DUPLICATE_EMAIL,
      { email }
    );

    if (response.resultCode === '0000') {
      return response.resultData.isDuplicate;
    } else {
      throw new Error(response.resultMessage);
    }
  }

  async findUserId(email: string): Promise<string> {
    const response: ApiResponse<{ userId: string }> = await apiService.post(
      API_ENDPOINTS.AUTH.FIND_USER_ID,
      { email }
    );

    if (response.resultCode === '0000') {
      return response.resultData.userId;
    } else {
      throw new Error(response.resultMessage);
    }
  }

  async findUserPw(userId: string, email: string): Promise<void> {
    const response: ApiResponse<void> = await apiService.post(
      API_ENDPOINTS.AUTH.FIND_USER_PW,
      { userId, email }
    );

    if (response.resultCode !== '0000') {
      throw new Error(response.resultMessage);
    }
  }

  async updateUserPw(userId: string, newPassword: string): Promise<void> {
    const response: ApiResponse<void> = await apiService.post(
      API_ENDPOINTS.AUTH.UPDATE_USER_PW,
      { userId, userPwEnc: newPassword }
    );

    if (response.resultCode !== '0000') {
      throw new Error(response.resultMessage);
    }
  }

  async getCurrentUser(): Promise<User | null> {
    try {
      const userString = await AsyncStorage.getItem('user');
      return userString ? JSON.parse(userString) : null;
    } catch (error) {
      console.error('Error getting current user:', error);
      return null;
    }
  }

  async isAuthenticated(): Promise<boolean> {
    const token = await AsyncStorage.getItem('accessToken');
    return !!token;
  }
}

export const authService = new AuthService();