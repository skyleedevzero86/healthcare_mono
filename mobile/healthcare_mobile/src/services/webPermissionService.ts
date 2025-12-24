import { PermissionStatus, LocationData, SensorData } from '../types';

class WebPermissionService {
  async requestLocationPermission(): Promise<boolean> {
    try {
      if (!navigator.geolocation) {
        throw new Error('이 브라우저는 지오로케이션을 지원하지 않습니다.');
      }

      return new Promise((resolve) => {
        navigator.geolocation.getCurrentPosition(
          () => resolve(true),
          () => resolve(false),
          { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
        );
      });
    } catch (error) {
      console.error('위치 권한 오류:', error);
      return false;
    }
  }

  async requestCameraPermission(): Promise<boolean> {
    try {
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        throw new Error('이 브라우저는 카메라를 지원하지 않습니다.');
      }

      const stream = await navigator.mediaDevices.getUserMedia({ video: true });
      stream.getTracks().forEach(track => track.stop());
      return true;
    } catch (error) {
      console.error('카메라 권한 오류:', error);
      return false;
    }
  }

  async requestMicrophonePermission(): Promise<boolean> {
    try {
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        throw new Error('이 브라우저는 마이크를 지원하지 않습니다.');
      }

      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      stream.getTracks().forEach(track => track.stop());
      return true;
    } catch (error) {
      console.error('마이크 권한 오류:', error);
      return false;
    }
  }

  async requestNotificationPermission(): Promise<boolean> {
    try {
      if (!('Notification' in window)) {
        throw new Error('이 브라우저는 알림을 지원하지 않습니다.');
      }

      if (Notification.permission === 'granted') {
        return true;
      }

      if (Notification.permission === 'denied') {
        return false;
      }

      const permission = await Notification.requestPermission();
      return permission === 'granted';
    } catch (error) {
      console.error('알림 권한 오류:', error);
      return false;
    }
  }

  async checkAllPermissions(): Promise<PermissionStatus> {
    try {
      const [locationGranted, cameraGranted, microphoneGranted, notificationGranted] = await Promise.all([
        this.checkLocationPermission(),
        this.checkCameraPermission(),
        this.checkMicrophonePermission(),
        this.checkNotificationPermission(),
      ]);

      return {
        location: locationGranted ? 'granted' : 'denied',
        camera: cameraGranted ? 'granted' : 'denied',
        microphone: microphoneGranted ? 'granted' : 'denied',
        healthData: 'granted',
        notifications: notificationGranted ? 'granted' : 'denied',
      };
    } catch (error) {
      console.error('권한 확인 오류:', error);
      return {
        location: 'undetermined',
        camera: 'undetermined',
        microphone: 'undetermined',
        healthData: 'undetermined',
        notifications: 'undetermined',
      };
    }
  }

  private async checkLocationPermission(): Promise<boolean> {
    return new Promise((resolve) => {
      if (!navigator.geolocation) {
        resolve(false);
        return;
      }

      navigator.geolocation.getCurrentPosition(
        () => resolve(true),
        () => resolve(false),
        { enableHighAccuracy: true, timeout: 1000, maximumAge: 0 }
      );
    });
  }

  private async checkCameraPermission(): Promise<boolean> {
    try {
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        return false;
      }

      const stream = await navigator.mediaDevices.getUserMedia({ video: true });
      stream.getTracks().forEach(track => track.stop());
      return true;
    } catch {
      return false;
    }
  }

  private async checkMicrophonePermission(): Promise<boolean> {
    try {
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        return false;
      }

      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      stream.getTracks().forEach(track => track.stop());
      return true;
    } catch {
      return false;
    }
  }

  private async checkNotificationPermission(): Promise<boolean> {
    if (!('Notification' in window)) {
      return false;
    }
    return Notification.permission === 'granted';
  }

  async getCurrentLocation(): Promise<LocationData | null> {
    try {
      const hasPermission = await this.requestLocationPermission();
      if (!hasPermission) {
        throw new Error('Location permission denied');
      }

      return new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            resolve({
              latitude: position.coords.latitude,
              longitude: position.coords.longitude,
              accuracy: position.coords.accuracy || 0,
              timestamp: position.timestamp,
            });
          },
          (error) => {
            reject(new Error(`위치 오류: ${error.message}`));
          },
          { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
        );
      });
    } catch (error) {
      console.error('위치 가져오기 오류:', error);
      return null;
    }
  }

  async startLocationTracking(callback: (location: LocationData) => void): Promise<() => void> {
    try {
      const hasPermission = await this.requestLocationPermission();
      if (!hasPermission) {
        throw new Error('위치 권한이 거부되었습니다');
      }

      const watchId = navigator.geolocation.watchPosition(
        (position) => {
          callback({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
            accuracy: position.coords.accuracy || 0,
            timestamp: position.timestamp,
          });
        },
        (error) => {
          console.error('위치 추적 오류:', error);
        },
        { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
      );

      return () => navigator.geolocation.clearWatch(watchId);
    } catch (error) {
      console.error('위치 추적 시작 오류:', error);
      return () => {};
    }
  }

  async takePicture(): Promise<string | null> {
    try {
      const hasPermission = await this.requestCameraPermission();
      if (!hasPermission) {
        throw new Error('카메라 권한이 거부되었습니다');
      }

      const stream = await navigator.mediaDevices.getUserMedia({ video: true });
      const video = document.createElement('video');
      video.srcObject = stream;
      video.play();

      return new Promise((resolve) => {
        video.addEventListener('loadedmetadata', () => {
          const canvas = document.createElement('canvas');
          canvas.width = video.videoWidth;
          canvas.height = video.videoHeight;
          const ctx = canvas.getContext('2d');
          ctx?.drawImage(video, 0, 0);
          
          stream.getTracks().forEach(track => track.stop());
          resolve(canvas.toDataURL('image/jpeg', 0.8));
        });
      });
    } catch (error) {
      console.error('사진 촬영 오류:', error);
      return null;
    }
  }

  async selectImageFromLibrary(): Promise<string | null> {
    try {
      return new Promise((resolve) => {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'image/*';
        input.onchange = (event) => {
          const file = (event.target as HTMLInputElement).files?.[0];
          if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
              resolve(e.target?.result as string);
            };
            reader.readAsDataURL(file);
          } else {
            resolve(null);
          }
        };
        input.click();
      });
    } catch (error) {
      console.error('이미지 선택 오류:', error);
      return null;
    }
  }

  async startSensorTracking(callback: (sensorData: SensorData) => void): Promise<() => void> {
    try {
      if (!('DeviceMotionEvent' in window) && !('DeviceOrientationEvent' in window)) {
        throw new Error('이 브라우저는 디바이스 센서를 지원하지 않습니다.');
      }

      let accelerometerData = { x: 0, y: 0, z: 0 };
      let gyroscopeData = { x: 0, y: 0, z: 0 };
      let magnetometerData = { x: 0, y: 0, z: 0 };

      const handleMotion = (event: DeviceMotionEvent) => {
        if (event.acceleration) {
          accelerometerData = {
            x: event.acceleration.x || 0,
            y: event.acceleration.y || 0,
            z: event.acceleration.z || 0,
          };
        }
        if (event.rotationRate) {
          gyroscopeData = {
            x: event.rotationRate.alpha || 0,
            y: event.rotationRate.beta || 0,
            z: event.rotationRate.gamma || 0,
          };
        }
      };

      const handleOrientation = (event: DeviceOrientationEvent) => {
        magnetometerData = {
          x: event.alpha || 0,
          y: event.beta || 0,
          z: event.gamma || 0,
        };
      };

      window.addEventListener('devicemotion', handleMotion);
      window.addEventListener('deviceorientation', handleOrientation);

      const interval = setInterval(() => {
        callback({
          accelerometer: accelerometerData,
          gyroscope: gyroscopeData,
          magnetometer: magnetometerData,
          timestamp: Date.now(),
        });
      }, 100);

      return () => {
        window.removeEventListener('devicemotion', handleMotion);
        window.removeEventListener('deviceorientation', handleOrientation);
        clearInterval(interval);
      };
    } catch (error) {
      console.error('센서 추적 시작 오류:', error);
      return () => {};
    }
  }

  async sendNotification(title: string, body: string): Promise<void> {
    try {
      const hasPermission = await this.requestNotificationPermission();
      if (!hasPermission) {
        throw new Error('알림 권한이 거부되었습니다');
      }

      if ('serviceWorker' in navigator && 'PushManager' in window) {
        const registration = await navigator.serviceWorker.ready;
        await registration.showNotification(title, {
          body,
          icon: '/icon-192x192.png',
          badge: '/badge-72x72.png',
        });
      } else {
        new Notification(title, { body });
      }
    } catch (error) {
      console.error('알림 전송 오류:', error);
    }
  }

  showPermissionAlert(permissionType: string, onPress: () => void): void {
    const message = `${permissionType} 기능을 사용하려면 브라우저 설정에서 권한을 허용해주세요.`;
    if (confirm(message)) {
      onPress();
    }
  }

  async openAppSettings(): Promise<void> {
    alert('브라우저 설정에서 권한을 관리할 수 있습니다.');
  }

  async requestAllPermissions(): Promise<PermissionStatus> {
    try {
      const [locationGranted, cameraGranted, microphoneGranted, notificationGranted] = await Promise.all([
        this.requestLocationPermission(),
        this.requestCameraPermission(),
        this.requestMicrophonePermission(),
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
      console.error('모든 권한 요청 오류:', error);
      return {
        location: 'denied',
        camera: 'denied',
        microphone: 'denied',
        healthData: 'denied',
        notifications: 'denied',
      };
    }
  }

  isWebEnvironment(): boolean {
    return typeof window !== 'undefined' && typeof document !== 'undefined';
  }

  getBrowserInfo(): { name: string; version: string } {
    const userAgent = navigator.userAgent;
    let browserName = 'Unknown';
    let browserVersion = 'Unknown';

    if (userAgent.indexOf('Chrome') > -1) {
      browserName = 'Chrome';
      browserVersion = userAgent.match(/Chrome\/(\d+)/)?.[1] || 'Unknown';
    } else if (userAgent.indexOf('Firefox') > -1) {
      browserName = 'Firefox';
      browserVersion = userAgent.match(/Firefox\/(\d+)/)?.[1] || 'Unknown';
    } else if (userAgent.indexOf('Safari') > -1) {
      browserName = 'Safari';
      browserVersion = userAgent.match(/Version\/(\d+)/)?.[1] || 'Unknown';
    } else if (userAgent.indexOf('Edge') > -1) {
      browserName = 'Edge';
      browserVersion = userAgent.match(/Edge\/(\d+)/)?.[1] || 'Unknown';
    }

    return { name: browserName, version: browserVersion };
  }

  getSupportedFeatures(): {
    geolocation: boolean;
    camera: boolean;
    microphone: boolean;
    notifications: boolean;
    sensors: boolean;
    serviceWorker: boolean;
  } {
    return {
      geolocation: 'geolocation' in navigator,
      camera: !!(navigator.mediaDevices && navigator.mediaDevices.getUserMedia),
      microphone: !!(navigator.mediaDevices && navigator.mediaDevices.getUserMedia),
      notifications: 'Notification' in window,
      sensors: 'DeviceMotionEvent' in window || 'DeviceOrientationEvent' in window,
      serviceWorker: 'serviceWorker' in navigator,
    };
  }
}

export const webPermissionService = new WebPermissionService();
