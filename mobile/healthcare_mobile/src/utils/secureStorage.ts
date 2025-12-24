import AsyncStorage from '@react-native-async-storage/async-storage';
import * as Crypto from 'expo-crypto';

const STORAGE_KEYS = {
  ACCESS_TOKEN: 'accessToken',
  REFRESH_TOKEN: 'refreshToken',
  USER: 'user',
  ENCRYPTION_KEY: 'encryptionKey',
} as const;

class SecureStorage {
  private encryptionKey: string | null = null;

  async initialize(): Promise<void> {
    try {
      let key = await AsyncStorage.getItem(STORAGE_KEYS.ENCRYPTION_KEY);
      if (!key) {
        key = await Crypto.digestStringAsync(
          Crypto.CryptoDigestAlgorithm.SHA256,
          `${Date.now()}-${Math.random()}`
        );
        await AsyncStorage.setItem(STORAGE_KEYS.ENCRYPTION_KEY, key);
      }
      this.encryptionKey = key;
    } catch (error) {
      console.error('SecureStorage 초기화 실패:', error);
    }
  }

  private async encrypt(value: string): Promise<string> {
    if (!this.encryptionKey) {
      await this.initialize();
    }
    const hash = await Crypto.digestStringAsync(
      Crypto.CryptoDigestAlgorithm.SHA256,
      `${value}-${this.encryptionKey}`
    );
    return hash;
  }

  async setItem(key: string, value: string): Promise<void> {
    try {
      const encrypted = await this.encrypt(value);
      await AsyncStorage.setItem(key, encrypted);
    } catch (error) {
      throw new Error(`저장 실패: ${error}`);
    }
  }

  async getItem(key: string): Promise<string | null> {
    try {
      return await AsyncStorage.getItem(key);
    } catch (error) {
      throw new Error(`조회 실패: ${error}`);
    }
  }

  async removeItem(key: string): Promise<void> {
    try {
      await AsyncStorage.removeItem(key);
    } catch (error) {
      throw new Error(`삭제 실패: ${error}`);
    }
  }

  async setToken(accessToken: string, refreshToken: string): Promise<void> {
    await Promise.all([
      this.setItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken),
      this.setItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken),
    ]);
  }

  async getAccessToken(): Promise<string | null> {
    return this.getItem(STORAGE_KEYS.ACCESS_TOKEN);
  }

  async getRefreshToken(): Promise<string | null> {
    return this.getItem(STORAGE_KEYS.REFRESH_TOKEN);
  }

  async clearTokens(): Promise<void> {
    await Promise.all([
      this.removeItem(STORAGE_KEYS.ACCESS_TOKEN),
      this.removeItem(STORAGE_KEYS.REFRESH_TOKEN),
    ]);
  }

  async clearAll(): Promise<void> {
    await Promise.all([
      this.clearTokens(),
      this.removeItem(STORAGE_KEYS.USER),
    ]);
  }
}

export const secureStorage = new SecureStorage();

