import { Platform } from 'react-native';

export const isWeb = Platform.OS === 'web';
export const isIOS = Platform.OS === 'ios';
export const isAndroid = Platform.OS === 'android';

export const getPlatformInfo = () => {
  return {
    platform: Platform.OS,
    version: Platform.Version,
    isWeb,
    isIOS,
    isAndroid,
  };
};

export const getWebBrowserInfo = () => {
  if (!isWeb || typeof window === 'undefined') {
    return { name: 'Unknown', version: 'Unknown' };
  }

  const userAgent = window.navigator.userAgent;
  let browserName = 'Unknown';
  let browserVersion = 'Unknown';

  if (userAgent.indexOf('Chrome') > -1 && userAgent.indexOf('Edg') === -1) {
    browserName = 'Chrome';
    browserVersion = userAgent.match(/Chrome\/(\d+)/)?.[1] || 'Unknown';
  } else if (userAgent.indexOf('Firefox') > -1) {
    browserName = 'Firefox';
    browserVersion = userAgent.match(/Firefox\/(\d+)/)?.[1] || 'Unknown';
  } else if (userAgent.indexOf('Safari') > -1 && userAgent.indexOf('Chrome') === -1) {
    browserName = 'Safari';
    browserVersion = userAgent.match(/Version\/(\d+)/)?.[1] || 'Unknown';
  } else if (userAgent.indexOf('Edg') > -1) {
    browserName = 'Edge';
    browserVersion = userAgent.match(/Edg\/(\d+)/)?.[1] || 'Unknown';
  } else if (userAgent.indexOf('Opera') > -1 || userAgent.indexOf('OPR') > -1) {
    browserName = 'Opera';
    browserVersion = userAgent.match(/(?:Opera|OPR)\/(\d+)/)?.[1] || 'Unknown';
  }

  return { name: browserName, version: browserVersion };
};

export const getWebSupportedFeatures = () => {
  if (!isWeb || typeof window === 'undefined') {
    return {
      geolocation: false,
      camera: false,
      microphone: false,
      notifications: false,
      sensors: false,
      serviceWorker: false,
      localStorage: false,
      sessionStorage: false,
    };
  }

  return {
    geolocation: 'geolocation' in navigator,
    camera: !!(navigator.mediaDevices && navigator.mediaDevices.getUserMedia),
    microphone: !!(navigator.mediaDevices && navigator.mediaDevices.getUserMedia),
    notifications: 'Notification' in window,
    sensors: 'DeviceMotionEvent' in window || 'DeviceOrientationEvent' in window,
    serviceWorker: 'serviceWorker' in navigator,
    localStorage: 'localStorage' in window,
    sessionStorage: 'sessionStorage' in window,
  };
};

export const getWebPermissions = async () => {
  if (!isWeb) {
    return {
      geolocation: 'undetermined',
      camera: 'undetermined',
      microphone: 'undetermined',
      notifications: 'undetermined',
    };
  }

  const permissions = {
    geolocation: 'undetermined',
    camera: 'undetermined',
    microphone: 'undetermined',
    notifications: 'undetermined',
  };

  try {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(
        () => { permissions.geolocation = 'granted'; },
        () => { permissions.geolocation = 'denied'; },
        { timeout: 1000 }
      );
    }
  } catch (error) {
    permissions.geolocation = 'denied';
  }

  try {
    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
      const stream = await navigator.mediaDevices.getUserMedia({ video: true });
      stream.getTracks().forEach(track => track.stop());
      permissions.camera = 'granted';
    }
  } catch (error) {
    permissions.camera = 'denied';
  }

  try {
    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      stream.getTracks().forEach(track => track.stop());
      permissions.microphone = 'granted';
    }
  } catch (error) {
    permissions.microphone = 'denied';
  }

  if ('Notification' in window) {
    permissions.notifications = Notification.permission;
  }

  return permissions;
};

export const requestWebPermission = async (type: 'geolocation' | 'camera' | 'microphone' | 'notifications') => {
  if (!isWeb) {
    return false;
  }

  try {
    switch (type) {
      case 'geolocation':
        if (!('geolocation' in navigator)) return false;
        return new Promise((resolve) => {
          navigator.geolocation.getCurrentPosition(
            () => resolve(true),
            () => resolve(false),
            { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
          );
        });

      case 'camera':
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) return false;
        const cameraStream = await navigator.mediaDevices.getUserMedia({ video: true });
        cameraStream.getTracks().forEach(track => track.stop());
        return true;

      case 'microphone':
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) return false;
        const micStream = await navigator.mediaDevices.getUserMedia({ audio: true });
        micStream.getTracks().forEach(track => track.stop());
        return true;

      case 'notifications':
        if (!('Notification' in window)) return false;
        if (Notification.permission === 'granted') return true;
        if (Notification.permission === 'denied') return false;
        const permission = await Notification.requestPermission();
        return permission === 'granted';

      default:
        return false;
    }
  } catch (error) {
    console.error(`Web permission request failed for ${type}:`, error);
    return false;
  }
};

export const getWebLocation = (): Promise<{ latitude: number; longitude: number; accuracy: number } | null> => {
  if (!isWeb || !('geolocation' in navigator)) {
    return Promise.resolve(null);
  }

  return new Promise((resolve) => {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        resolve({
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
          accuracy: position.coords.accuracy || 0,
        });
      },
      (error) => {
        console.error('Geolocation error:', error);
        resolve(null);
      },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
    );
  });
};

export const getWebCamera = (): Promise<string | null> => {
  if (!isWeb || !navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
    return Promise.resolve(null);
  }

  return new Promise(async (resolve) => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ video: true });
      const video = document.createElement('video');
      video.srcObject = stream;
      video.play();

      video.addEventListener('loadedmetadata', () => {
        const canvas = document.createElement('canvas');
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        const ctx = canvas.getContext('2d');
        ctx?.drawImage(video, 0, 0);
        
        stream.getTracks().forEach(track => track.stop());
        resolve(canvas.toDataURL('image/jpeg', 0.8));
      });
    } catch (error) {
      console.error('Camera error:', error);
      resolve(null);
    }
  });
};

export const getWebImageFromLibrary = (): Promise<string | null> => {
  if (!isWeb) {
    return Promise.resolve(null);
  }

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
};

export const sendWebNotification = (title: string, body: string): Promise<void> => {
  if (!isWeb || !('Notification' in window)) {
    return Promise.resolve();
  }

  return new Promise(async (resolve) => {
    try {
      if (Notification.permission === 'granted') {
        new Notification(title, { body });
        resolve();
      } else if (Notification.permission !== 'denied') {
        const permission = await Notification.requestPermission();
        if (permission === 'granted') {
          new Notification(title, { body });
        }
        resolve();
      } else {
        resolve();
      }
    } catch (error) {
      console.error('Notification error:', error);
      resolve();
    }
  });
};
