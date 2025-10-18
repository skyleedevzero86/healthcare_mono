import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  RefreshControl,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../../store';
import {
  checkPermissions,
  requestLocationPermission,
  requestCameraPermission,
  requestMicrophonePermission,
  requestNotificationPermission,
  getCurrentLocation,
  takePicture,
  selectImageFromLibrary,
  sendNotification,
  setLocation,
  setSensorData,
} from '../../store/slices/permissionSlice';
import { permissionService } from '../../services/permissionService';

const PermissionScreen: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { permissions, location, sensorData, loading } = useSelector(
    (state: RootState) => state.permission
  );

  const [isTrackingLocation, setIsTrackingLocation] = useState(false);
  const [isTrackingSensors, setIsTrackingSensors] = useState(false);

  useEffect(() => {
    dispatch(checkPermissions());
  }, [dispatch]);

  const onRefresh = () => {
    dispatch(checkPermissions());
  };

  const handleLocationPermission = async () => {
    try {
      await dispatch(requestLocationPermission()).unwrap();
    } catch (error) {
      Alert.alert('권한 요청 실패', error as string);
    }
  };

  const handleCameraPermission = async () => {
    try {
      await dispatch(requestCameraPermission()).unwrap();
    } catch (error) {
      Alert.alert('권한 요청 실패', error as string);
    }
  };

  const handleMicrophonePermission = async () => {
    try {
      await dispatch(requestMicrophonePermission()).unwrap();
    } catch (error) {
      Alert.alert('권한 요청 실패', error as string);
    }
  };

  const handleNotificationPermission = async () => {
    try {
      await dispatch(requestNotificationPermission()).unwrap();
    } catch (error) {
      Alert.alert('권한 요청 실패', error as string);
    }
  };

  const handleGetLocation = async () => {
    try {
      await dispatch(getCurrentLocation()).unwrap();
    } catch (error) {
      Alert.alert('위치 가져오기 실패', error as string);
    }
  };

  const handleTakePicture = async () => {
    try {
      const imageUri = await dispatch(takePicture()).unwrap();
      if (imageUri) {
        Alert.alert('사진 촬영 완료', `이미지 URI: ${imageUri}`);
      }
    } catch (error) {
      Alert.alert('사진 촬영 실패', error as string);
    }
  };

  const handleSelectImage = async () => {
    try {
      const imageUri = await dispatch(selectImageFromLibrary()).unwrap();
      if (imageUri) {
        Alert.alert('이미지 선택 완료', `이미지 URI: ${imageUri}`);
      }
    } catch (error) {
      Alert.alert('이미지 선택 실패', error as string);
    }
  };

  const handleSendNotification = async () => {
    try {
      await dispatch(
        sendNotification({
          title: '헬스케어 알림',
          body: '건강 상태를 확인해보세요!',
        })
      ).unwrap();
      Alert.alert('알림 전송 완료', '알림이 전송되었습니다.');
    } catch (error) {
      Alert.alert('알림 전송 실패', error as string);
    }
  };

  const handleStartLocationTracking = async () => {
    try {
      const stopTracking = await permissionService.startLocationTracking((location) => {
        dispatch(setLocation(location));
      });
      setIsTrackingLocation(true);

      setTimeout(() => {
        stopTracking();
        setIsTrackingLocation(false);
      }, 10000);
    } catch (error) {
      Alert.alert('위치 추적 실패', error as string);
    }
  };

  const handleStartSensorTracking = async () => {
    try {
      const stopTracking = await permissionService.startSensorTracking((sensorData) => {
        dispatch(setSensorData(sensorData));
      });
      setIsTrackingSensors(true);

      setTimeout(() => {
        stopTracking();
        setIsTrackingSensors(false);
      }, 10000);
    } catch (error) {
      Alert.alert('센서 추적 실패', error as string);
    }
  };

  const getPermissionStatus = (status: string) => {
    switch (status) {
      case 'granted':
        return { text: '허용됨', color: '#4CAF50', icon: 'checkmark-circle' };
      case 'denied':
        return { text: '거부됨', color: '#F44336', icon: 'close-circle' };
      case 'limited':
        return { text: '제한됨', color: '#FF9800', icon: 'warning' };
      default:
        return { text: '미확정', color: '#9E9E9E', icon: 'help-circle' };
    }
  };

  const PermissionCard = ({
    title,
    description,
    status,
    onRequest,
    onAction,
    actionText,
    actionDisabled = false,
  }: {
    title: string;
    description: string;
    status: string;
    onRequest: () => void;
    onAction: () => void;
    actionText: string;
    actionDisabled?: boolean;
  }) => {
    const statusInfo = getPermissionStatus(status);

    return (
      <View style={styles.permissionCard}>
        <View style={styles.permissionHeader}>
          <Text style={styles.permissionTitle}>{title}</Text>
          <View style={styles.statusContainer}>
            <Ionicons name={statusInfo.icon as any} size={20} color={statusInfo.color} />
            <Text style={[styles.statusText, { color: statusInfo.color }]}>
              {statusInfo.text}
            </Text>
          </View>
        </View>
        <Text style={styles.permissionDescription}>{description}</Text>
        <View style={styles.permissionActions}>
          <TouchableOpacity style={styles.requestButton} onPress={onRequest}>
            <Text style={styles.requestButtonText}>권한 요청</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.actionButton, actionDisabled && styles.disabledButton]}
            onPress={onAction}
            disabled={actionDisabled || status !== 'granted'}
          >
            <Text style={[styles.actionButtonText, actionDisabled && styles.disabledText]}>
              {actionText}
            </Text>
          </TouchableOpacity>
        </View>
      </View>
    );
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView
        style={styles.container}
        refreshControl={<RefreshControl refreshing={loading} onRefresh={onRefresh} />}
      >
        <View style={styles.header}>
          <Text style={styles.title}>권한 관리</Text>
          <Text style={styles.subtitle}>앱 기능 사용을 위한 권한을 관리하세요</Text>
        </View>

        <PermissionCard
          title="위치 정보"
          description="건강 데이터 수집 및 분석을 위한 위치 정보 접근"
          status={permissions.location}
          onRequest={handleLocationPermission}
          onAction={handleGetLocation}
          actionText="현재 위치 가져오기"
        />

        <PermissionCard
          title="카메라"
          description="건강 상태 사진 촬영을 위한 카메라 접근"
          status={permissions.camera}
          onRequest={handleCameraPermission}
          onAction={handleTakePicture}
          actionText="사진 촬영"
        />

        <PermissionCard
          title="갤러리"
          description="이미지 선택을 위한 갤러리 접근"
          status={permissions.camera}
          onRequest={handleCameraPermission}
          onAction={handleSelectImage}
          actionText="이미지 선택"
        />

        <PermissionCard
          title="마이크"
          description="음성 메모 및 녹음을 위한 마이크 접근"
          status={permissions.microphone}
          onRequest={handleMicrophonePermission}
          onAction={() => Alert.alert('마이크 기능', '음성 녹음 기능은 준비 중입니다.')}
          actionText="음성 녹음"
        />

        <PermissionCard
          title="알림"
          description="건강 상태 알림 및 리마인더를 위한 알림 접근"
          status={permissions.notifications}
          onRequest={handleNotificationPermission}
          onAction={handleSendNotification}
          actionText="테스트 알림"
        />

        <View style={styles.trackingSection}>
          <Text style={styles.sectionTitle}>실시간 추적</Text>
          
          <View style={styles.trackingCard}>
            <View style={styles.trackingHeader}>
              <Ionicons name="location" size={24} color="#2196F3" />
              <Text style={styles.trackingTitle}>위치 추적</Text>
              <View style={[styles.trackingStatus, isTrackingLocation && styles.activeStatus]}>
                <Text style={styles.trackingStatusText}>
                  {isTrackingLocation ? '추적 중' : '중지됨'}
                </Text>
              </View>
            </View>
            <Text style={styles.trackingDescription}>
              현재 위치를 실시간으로 추적합니다 (10초간)
            </Text>
            {location && (
              <View style={styles.locationInfo}>
                <Text style={styles.locationText}>
                  위도: {location.latitude.toFixed(6)}
                </Text>
                <Text style={styles.locationText}>
                  경도: {location.longitude.toFixed(6)}
                </Text>
                <Text style={styles.locationText}>
                  정확도: {location.accuracy.toFixed(2)}m
                </Text>
              </View>
            )}
            <TouchableOpacity
              style={[styles.trackingButton, isTrackingLocation && styles.activeButton]}
              onPress={handleStartLocationTracking}
              disabled={isTrackingLocation || permissions.location !== 'granted'}
            >
              <Text style={[styles.trackingButtonText, isTrackingLocation && styles.activeText]}>
                {isTrackingLocation ? '추적 중...' : '위치 추적 시작'}
              </Text>
            </TouchableOpacity>
          </View>

          <View style={styles.trackingCard}>
            <View style={styles.trackingHeader}>
              <Ionicons name="phone-portrait" size={24} color="#2196F3" />
              <Text style={styles.trackingTitle}>센서 추적</Text>
              <View style={[styles.trackingStatus, isTrackingSensors && styles.activeStatus]}>
                <Text style={styles.trackingStatusText}>
                  {isTrackingSensors ? '추적 중' : '중지됨'}
                </Text>
              </View>
            </View>
            <Text style={styles.trackingDescription}>
              가속도계, 자이로스코프, 자기계를 실시간으로 추적합니다 (10초간)
            </Text>
            {sensorData && (
              <View style={styles.sensorInfo}>
                <Text style={styles.sensorText}>
                  가속도: X={sensorData.accelerometer.x.toFixed(2)}, Y={sensorData.accelerometer.y.toFixed(2)}, Z={sensorData.accelerometer.z.toFixed(2)}
                </Text>
                <Text style={styles.sensorText}>
                  자이로: X={sensorData.gyroscope.x.toFixed(2)}, Y={sensorData.gyroscope.y.toFixed(2)}, Z={sensorData.gyroscope.z.toFixed(2)}
                </Text>
                <Text style={styles.sensorText}>
                  자기계: X={sensorData.magnetometer.x.toFixed(2)}, Y={sensorData.magnetometer.y.toFixed(2)}, Z={sensorData.magnetometer.z.toFixed(2)}
                </Text>
              </View>
            )}
            <TouchableOpacity
              style={[styles.trackingButton, isTrackingSensors && styles.activeButton]}
              onPress={handleStartSensorTracking}
              disabled={isTrackingSensors}
            >
              <Text style={[styles.trackingButtonText, isTrackingSensors && styles.activeText]}>
                {isTrackingSensors ? '추적 중...' : '센서 추적 시작'}
              </Text>
            </TouchableOpacity>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  container: {
    flex: 1,
    padding: 20,
  },
  header: {
    marginBottom: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
  },
  permissionCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    marginBottom: 15,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  permissionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 10,
  },
  permissionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
  },
  statusContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statusText: {
    fontSize: 14,
    fontWeight: '600',
    marginLeft: 5,
  },
  permissionDescription: {
    fontSize: 14,
    color: '#666',
    marginBottom: 15,
    lineHeight: 20,
  },
  permissionActions: {
    flexDirection: 'row',
    gap: 10,
  },
  requestButton: {
    flex: 1,
    backgroundColor: '#2196F3',
    borderRadius: 8,
    padding: 12,
    alignItems: 'center',
  },
  requestButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
  },
  actionButton: {
    flex: 1,
    backgroundColor: '#4CAF50',
    borderRadius: 8,
    padding: 12,
    alignItems: 'center',
  },
  actionButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
  },
  disabledButton: {
    backgroundColor: '#ccc',
  },
  disabledText: {
    color: '#999',
  },
  trackingSection: {
    marginTop: 20,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  trackingCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    marginBottom: 15,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  trackingHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  trackingTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginLeft: 10,
    flex: 1,
  },
  trackingStatus: {
    backgroundColor: '#f0f0f0',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 4,
  },
  activeStatus: {
    backgroundColor: '#4CAF50',
  },
  trackingStatusText: {
    fontSize: 12,
    color: '#666',
    fontWeight: '600',
  },
  trackingDescription: {
    fontSize: 14,
    color: '#666',
    marginBottom: 15,
    lineHeight: 20,
  },
  locationInfo: {
    backgroundColor: '#f8f9fa',
    borderRadius: 8,
    padding: 10,
    marginBottom: 15,
  },
  locationText: {
    fontSize: 12,
    color: '#666',
    marginBottom: 2,
  },
  sensorInfo: {
    backgroundColor: '#f8f9fa',
    borderRadius: 8,
    padding: 10,
    marginBottom: 15,
  },
  sensorText: {
    fontSize: 12,
    color: '#666',
    marginBottom: 2,
  },
  trackingButton: {
    backgroundColor: '#2196F3',
    borderRadius: 8,
    padding: 15,
    alignItems: 'center',
  },
  activeButton: {
    backgroundColor: '#4CAF50',
  },
  trackingButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  activeText: {
    color: '#fff',
  },
});

export default PermissionScreen;
