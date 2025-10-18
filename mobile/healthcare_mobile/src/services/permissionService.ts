import { Platform, Alert, Linking } from 'react-native';
import * as Location from 'expo-location';
import * as Camera from 'expo-camera';
import * as MediaLibrary from 'expo-media-library';
import * as Notifications from 'expo-notifications';
import { PermissionStatus, LocationData, SensorData } from '../types';
import { isWeb, getWebBrowserInfo, getWebSupportedFeatures, requestWebPermission, getWebLocation, getWebCamera, getWebImageFromLibrary, sendWebNotification } from '../utils/platform';

class PermissionService {
  async requestLocationPermission(): Promise<boolean> {
    try {
      if (isWeb) {
        return await requestWebPermission('geolocation');
      }
      const { status } = await Location.requestForegroundPermissionsAsync();
      return status === 'granted';
    } catch (error) {
      console.error('Location permission error:', error);
      return false;
    }
  }

  async requestCameraPermission(): Promise<boolean> {
    try {
      if (isWeb) {
        return await requestWebPermission('camera');
      }
      const { status } = await Camera.requestCameraPermissionsAsync();
      return status === 'granted';
    } catch (error) {
      console.error('Camera permission error:', error);
      return false;
    }
  }

  async requestMicrophonePermission(): Promise<boolean> {
    try {
      if (isWeb) {
        return await requestWebPermission('microphone');
      }
      const { status } = await Camera.requestMicrophonePermissionsAsync();
      return status === 'granted';
    } catch (error) {
      console.error('Microphone permission error:', error);
      return false;
    }
  }

  async requestMediaLibraryPermission(): Promise<boolean> {
    try {
      const { status } = await MediaLibrary.requestPermissionsAsync();
      return status === 'granted';
    } catch (error) {
      console.error('Media library permission error:', error);
      return false;
    }
  }

  async requestNotificationPermission(): Promise<boolean> {
    try {
      if (isWeb) {
        return await requestWebPermission('notifications');
      }
      const { status } = await Notifications.requestPermissionsAsync();
      return status === 'granted';
    } catch (error) {
      console.error('Notification permission error:', error);
      return false;
    }
  }

  async checkAllPermissions(): Promise<PermissionStatus> {
    try {
      const [locationStatus, cameraStatus, microphoneStatus, mediaStatus, notificationStatus] = await Promise.all([
        Location.getForegroundPermissionsAsync(),
        Camera.getCameraPermissionsAsync(),
        Camera.getMicrophonePermissionsAsync(),
        MediaLibrary.getPermissionsAsync(),
        Notifications.getPermissionsAsync(),
      ]);

      return {
        location: locationStatus.status,
        camera: cameraStatus.status,
        microphone: microphoneStatus.status,
        healthData: 'granted',
        notifications: notificationStatus.status,
      };
    } catch (error) {
      console.error('Permission check error:', error);
      return {
        location: 'undetermined',
        camera: 'undetermined',
        microphone: 'undetermined',
        healthData: 'undetermined',
        notifications: 'undetermined',
      };
    }
  }

  async getCurrentLocation(): Promise<LocationData | null> {
    try {
      if (isWeb) {
        const location = await getWebLocation();
        if (location) {
          return {
            latitude: location.latitude,
            longitude: location.longitude,
            accuracy: location.accuracy,
            timestamp: Date.now(),
          };
        }
        return null;
      }

      const hasPermission = await this.requestLocationPermission();
      if (!hasPermission) {
        throw new Error('Location permission denied');
      }

      const location = await Location.getCurrentPositionAsync({
        accuracy: Location.Accuracy.High,
      });

      return {
        latitude: location.coords.latitude,
        longitude: location.coords.longitude,
        accuracy: location.coords.accuracy || 0,
        timestamp: location.timestamp,
      };
    } catch (error) {
      console.error('Get location error:', error);
      return null;
    }
  }

  async startLocationTracking(callback: (location: LocationData) => void): Promise<() => void> {
    try {
      const hasPermission = await this.requestLocationPermission();
      if (!hasPermission) {
        throw new Error('Location permission denied');
      }

      const subscription = await Location.watchPositionAsync(
        {
          accuracy: Location.Accuracy.High,
          timeInterval: 1000,
          distanceInterval: 1,
        },
        (location) => {
          callback({
            latitude: location.coords.latitude,
            longitude: location.coords.longitude,
            accuracy: location.coords.accuracy || 0,
            timestamp: location.timestamp,
          });
        }
      );

      return () => subscription.remove();
    } catch (error) {
      console.error('Start location tracking error:', error);
      return () => {};
    }
  }

  async takePicture(): Promise<string | null> {
    try {
      if (isWeb) {
        return await getWebCamera();
      }

      const hasPermission = await this.requestCameraPermission();
      if (!hasPermission) {
        throw new Error('Camera permission denied');
      }

      const result = await Camera.launchCameraAsync({
        mediaTypes: Camera.MediaTypeOptions.Images,
        allowsEditing: true,
        aspect: [4, 3],
        quality: 0.8,
      });

      if (!result.canceled && result.assets && result.assets.length > 0) {
        return result.assets[0].uri;
      }

      return null;
    } catch (error) {
      console.error('Take picture error:', error);
      return null;
    }
  }

  async selectImageFromLibrary(): Promise<string | null> {
    try {
      if (isWeb) {
        return await getWebImageFromLibrary();
      }

      const hasPermission = await this.requestMediaLibraryPermission();
      if (!hasPermission) {
        throw new Error('Media library permission denied');
      }

      const result = await Camera.launchImageLibraryAsync({
        mediaTypes: Camera.MediaTypeOptions.Images,
        allowsEditing: true,
        aspect: [4, 3],
        quality: 0.8,
      });

      if (!result.canceled && result.assets && result.assets.length > 0) {
        return result.assets[0].uri;
      }

      return null;
    } catch (error) {
      console.error('Select image error:', error);
      return null;
    }
  }

  async startSensorTracking(callback: (sensorData: SensorData) => void): Promise<() => void> {
    try {
      const accelerometerSubscription = Location.Accelerometer.addListener(({ x, y, z }) => {
        callback({
          accelerometer: { x, y, z },
          gyroscope: { x: 0, y: 0, z: 0 },
          magnetometer: { x: 0, y: 0, z: 0 },
          timestamp: Date.now(),
        });
      });

      const gyroscopeSubscription = Location.Gyroscope.addListener(({ x, y, z }) => {
        callback({
          accelerometer: { x: 0, y: 0, z: 0 },
          gyroscope: { x, y, z },
          magnetometer: { x: 0, y: 0, z: 0 },
          timestamp: Date.now(),
        });
      });

      const magnetometerSubscription = Location.Magnetometer.addListener(({ x, y, z }) => {
        callback({
          accelerometer: { x: 0, y: 0, z: 0 },
          gyroscope: { x: 0, y: 0, z: 0 },
          magnetometer: { x, y, z },
          timestamp: Date.now(),
        });
      });

      return () => {
        accelerometerSubscription.remove();
        gyroscopeSubscription.remove();
        magnetometerSubscription.remove();
      };
    } catch (error) {
      console.error('Start sensor tracking error:', error);
      return () => {};
    }
  }

  async sendNotification(title: string, body: string): Promise<void> {
    try {
      if (isWeb) {
        await sendWebNotification(title, body);
        return;
      }

      const hasPermission = await this.requestNotificationPermission();
      if (!hasPermission) {
        throw new Error('Notification permission denied');
      }

      await Notifications.scheduleNotificationAsync({
        content: {
          title,
          body,
        },
        trigger: null,
      });
    } catch (error) {
      console.error('Send notification error:', error);
    }
  }

  showPermissionAlert(permissionType: string, onPress: () => void): void {
    Alert.alert(
      '권한이 필요합니다',
      `${permissionType} 기능을 사용하려면 권한이 필요합니다. 설정에서 권한을 허용해주세요.`,
      [
        { text: '취소', style: 'cancel' },
        { text: '설정으로 이동', onPress },
      ]
    );
  }

  async openAppSettings(): Promise<void> {
    try {
      await Linking.openSettings();
    } catch (error) {
      console.error('Open settings error:', error);
    }
  }

  async requestAllPermissions(): Promise<PermissionStatus> {
    try {
      const [locationGranted, cameraGranted, microphoneGranted, mediaGranted, notificationGranted] = await Promise.all([
        this.requestLocationPermission(),
        this.requestCameraPermission(),
        this.requestMicrophonePermission(),
        this.requestMediaLibraryPermission(),
        this.requestNotificationPermission(),
      ]);

      return {
        location: locationGranted ? 'granted' : 'denied',
        camera: cameraGranted ? 'granted' : 'denied',
        microphone: microphoneGranted ? 'granted' : 'denied',
        healthData: 'granted',
        notifications: notificationGranted ? 'granted' : 'denied',
      };
    } catch (error) {
      console.error('Request all permissions error:', error);
      return {
        location: 'denied',
        camera: 'denied',
        microphone: 'denied',
        healthData: 'denied',
        notifications: 'denied',
      };
    }
  }
}

export const permissionService = new PermissionService();
