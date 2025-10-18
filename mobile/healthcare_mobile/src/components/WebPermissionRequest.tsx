import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Alert,
  Modal,
  ScrollView,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../store';
import {
  requestLocationPermission,
  requestCameraPermission,
  requestMicrophonePermission,
  requestNotificationPermission,
  requestAllPermissions,
} from '../store/slices/permissionSlice';
import { PermissionRequest } from '../types';
import { webPermissionService } from '../services/webPermissionService';

interface WebPermissionRequestProps {
  visible: boolean;
  onClose: () => void;
  onComplete: () => void;
}

const WebPermissionRequest: React.FC<WebPermissionRequestProps> = ({
  visible,
  onClose,
  onComplete,
}) => {
  const dispatch = useDispatch<AppDispatch>();
  const { permissions, loading } = useSelector((state: RootState) => state.permission);
  const [currentStep, setCurrentStep] = useState(0);
  const [browserInfo, setBrowserInfo] = useState({ name: 'Unknown', version: 'Unknown' });
  const [supportedFeatures, setSupportedFeatures] = useState({
    geolocation: false,
    camera: false,
    microphone: false,
    notifications: false,
    sensors: false,
    serviceWorker: false,
  });

  useEffect(() => {
    if (webPermissionService.isWebEnvironment()) {
      setBrowserInfo(webPermissionService.getBrowserInfo());
      setSupportedFeatures(webPermissionService.getSupportedFeatures());
    }
  }, []);

  const permissionRequests: PermissionRequest[] = [
    {
      type: 'location',
      title: '위치 정보',
      message: '건강 데이터 수집을 위해 위치 정보가 필요합니다.',
      required: true,
    },
    {
      type: 'camera',
      title: '카메라',
      message: '건강 상태 사진 촬영을 위해 카메라 권한이 필요합니다.',
      required: false,
    },
    {
      type: 'microphone',
      title: '마이크',
      message: '음성 메모 기능을 위해 마이크 권한이 필요합니다.',
      required: false,
    },
    {
      type: 'notifications',
      title: '알림',
      message: '건강 상태 알림을 위해 알림 권한이 필요합니다.',
      required: true,
    },
  ];

  const handlePermissionRequest = async (type: string) => {
    Alert.alert(
      '웹 권한 안내',
      '웹에서는 브라우저 설정에서 권한을 직접 관리해주세요.\n\nChrome: 설정 > 개인정보 보호 및 보안 > 사이트 설정\nFirefox: 설정 > 개인정보 보호 및 보안 > 권한',
      [{ text: '확인' }]
    );
  };

  const handleRequestAll = async () => {
    Alert.alert(
      '웹 권한 안내',
      '웹에서는 브라우저 설정에서 모든 권한을 직접 관리해주세요.\n\nChrome: 설정 > 개인정보 보호 및 보안 > 사이트 설정\nFirefox: 설정 > 개인정보 보호 및 보안 > 권한',
      [{ text: '확인', onPress: onComplete }]
    );
  };

  const handleNext = () => {
    if (currentStep < permissionRequests.length - 1) {
      setCurrentStep(currentStep + 1);
    } else {
      onComplete();
    }
  };

  const handleSkip = () => {
    if (currentStep < permissionRequests.length - 1) {
      setCurrentStep(currentStep + 1);
    } else {
      onComplete();
    }
  };

  const getPermissionStatus = (type: string) => {
    switch (type) {
      case 'location':
        return permissions.location;
      case 'camera':
        return permissions.camera;
      case 'microphone':
        return permissions.microphone;
      case 'notifications':
        return permissions.notifications;
      default:
        return 'undetermined';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'granted':
        return <Ionicons name="checkmark-circle" size={24} color="#4CAF50" />;
      case 'denied':
        return <Ionicons name="close-circle" size={24} color="#F44336" />;
      default:
        return <Ionicons name="help-circle" size={24} color="#FF9800" />;
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'granted':
        return '허용됨';
      case 'denied':
        return '거부됨';
      default:
        return '미확정';
    }
  };

  const isFeatureSupported = (type: string) => {
    switch (type) {
      case 'location':
        return supportedFeatures.geolocation;
      case 'camera':
        return supportedFeatures.camera;
      case 'microphone':
        return supportedFeatures.microphone;
      case 'notifications':
        return supportedFeatures.notifications;
      default:
        return true;
    }
  };

  const currentPermission = permissionRequests[currentStep];
  const isSupported = isFeatureSupported(currentPermission.type);

  return (
    <Modal visible={visible} animationType="slide" presentationStyle="pageSheet">
      <View style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.title}>웹 권한 설정</Text>
          <Text style={styles.subtitle}>
            {currentStep + 1} / {permissionRequests.length}
          </Text>
        </View>

        <ScrollView style={styles.content}>
          <View style={styles.browserInfo}>
            <Text style={styles.browserInfoTitle}>브라우저 정보</Text>
            <Text style={styles.browserInfoText}>
              {browserInfo.name} {browserInfo.version}
            </Text>
          </View>

          <View style={styles.permissionCard}>
            <View style={styles.permissionIcon}>
              <Ionicons
                name={
                  currentPermission.type === 'location'
                    ? 'location'
                    : currentPermission.type === 'camera'
                    ? 'camera'
                    : currentPermission.type === 'microphone'
                    ? 'mic'
                    : 'notifications'
                }
                size={48}
                color={isSupported ? '#2196F3' : '#ccc'}
              />
            </View>

            <Text style={styles.permissionTitle}>{currentPermission.title}</Text>
            <Text style={styles.permissionMessage}>{currentPermission.message}</Text>

            {!isSupported && (
              <View style={styles.unsupportedWarning}>
                <Ionicons name="warning" size={20} color="#FF9800" />
                <Text style={styles.unsupportedText}>
                  이 브라우저에서는 {currentPermission.title} 기능을 지원하지 않습니다.
                </Text>
              </View>
            )}

            <View style={styles.statusContainer}>
              <View style={styles.statusRow}>
                {getStatusIcon(getPermissionStatus(currentPermission.type))}
                <Text style={styles.statusText}>
                  {getStatusText(getPermissionStatus(currentPermission.type))}
                </Text>
              </View>
            </View>

            {isSupported && (
              <TouchableOpacity
                style={styles.requestButton}
                onPress={() => handlePermissionRequest(currentPermission.type)}
                disabled={loading}
              >
                <Text style={styles.requestButtonText}>
                  권한 안내
                </Text>
              </TouchableOpacity>
            )}
          </View>

          <View style={styles.infoCard}>
            <Text style={styles.infoTitle}>웹 권한 사용 목적</Text>
            <View style={styles.infoList}>
              <View style={styles.infoItem}>
                <Ionicons name="location" size={16} color="#666" />
                <Text style={styles.infoText}>위치: 건강 데이터 수집 및 분석</Text>
              </View>
              <View style={styles.infoItem}>
                <Ionicons name="camera" size={16} color="#666" />
                <Text style={styles.infoText}>카메라: 건강 상태 사진 촬영</Text>
              </View>
              <View style={styles.infoItem}>
                <Ionicons name="mic" size={16} color="#666" />
                <Text style={styles.infoText}>마이크: 음성 메모 및 녹음</Text>
              </View>
              <View style={styles.infoItem}>
                <Ionicons name="notifications" size={16} color="#666" />
                <Text style={styles.infoText}>알림: 건강 상태 알림 및 리마인더</Text>
              </View>
            </View>
          </View>

          <View style={styles.featuresCard}>
            <Text style={styles.featuresTitle}>브라우저 지원 기능</Text>
            <View style={styles.featuresList}>
              <View style={styles.featureItem}>
                <Ionicons 
                  name={supportedFeatures.geolocation ? "checkmark-circle" : "close-circle"} 
                  size={16} 
                  color={supportedFeatures.geolocation ? "#4CAF50" : "#F44336"} 
                />
                <Text style={styles.featureText}>위치 정보</Text>
              </View>
              <View style={styles.featureItem}>
                <Ionicons 
                  name={supportedFeatures.camera ? "checkmark-circle" : "close-circle"} 
                  size={16} 
                  color={supportedFeatures.camera ? "#4CAF50" : "#F44336"} 
                />
                <Text style={styles.featureText}>카메라</Text>
              </View>
              <View style={styles.featureItem}>
                <Ionicons 
                  name={supportedFeatures.microphone ? "checkmark-circle" : "close-circle"} 
                  size={16} 
                  color={supportedFeatures.microphone ? "#4CAF50" : "#F44336"} 
                />
                <Text style={styles.featureText}>마이크</Text>
              </View>
              <View style={styles.featureItem}>
                <Ionicons 
                  name={supportedFeatures.notifications ? "checkmark-circle" : "close-circle"} 
                  size={16} 
                  color={supportedFeatures.notifications ? "#4CAF50" : "#F44336"} 
                />
                <Text style={styles.featureText}>알림</Text>
              </View>
              <View style={styles.featureItem}>
                <Ionicons 
                  name={supportedFeatures.sensors ? "checkmark-circle" : "close-circle"} 
                  size={16} 
                  color={supportedFeatures.sensors ? "#4CAF50" : "#F44336"} 
                />
                <Text style={styles.featureText}>센서</Text>
              </View>
              <View style={styles.featureItem}>
                <Ionicons 
                  name={supportedFeatures.serviceWorker ? "checkmark-circle" : "close-circle"} 
                  size={16} 
                  color={supportedFeatures.serviceWorker ? "#4CAF50" : "#F44336"} 
                />
                <Text style={styles.featureText}>Service Worker</Text>
              </View>
            </View>
          </View>
        </ScrollView>

        <View style={styles.footer}>
          <TouchableOpacity style={styles.skipButton} onPress={handleSkip}>
            <Text style={styles.skipButtonText}>건너뛰기</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.nextButton} onPress={handleNext}>
            <Text style={styles.nextButtonText}>다음</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.bottomActions}>
          <TouchableOpacity style={styles.cancelButton} onPress={onClose}>
            <Text style={styles.cancelButtonText}>취소</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.allButton} onPress={handleRequestAll}>
            <Text style={styles.allButtonText}>권한 안내</Text>
          </TouchableOpacity>
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    backgroundColor: '#fff',
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
    marginTop: 5,
  },
  content: {
    flex: 1,
    padding: 20,
  },
  browserInfo: {
    backgroundColor: '#e3f2fd',
    borderRadius: 8,
    padding: 15,
    marginBottom: 20,
  },
  browserInfoTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#1976d2',
    marginBottom: 5,
  },
  browserInfoText: {
    fontSize: 14,
    color: '#1976d2',
  },
  permissionCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  permissionIcon: {
    alignItems: 'center',
    marginBottom: 15,
  },
  permissionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    textAlign: 'center',
    marginBottom: 10,
  },
  permissionMessage: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
    lineHeight: 24,
    marginBottom: 20,
  },
  unsupportedWarning: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff3e0',
    borderRadius: 8,
    padding: 10,
    marginBottom: 15,
  },
  unsupportedText: {
    fontSize: 14,
    color: '#f57c00',
    marginLeft: 8,
    flex: 1,
  },
  statusContainer: {
    alignItems: 'center',
    marginBottom: 20,
  },
  statusRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statusText: {
    fontSize: 16,
    color: '#333',
    marginLeft: 8,
  },
  requestButton: {
    backgroundColor: '#2196F3',
    borderRadius: 8,
    padding: 15,
    alignItems: 'center',
  },
  requestButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  infoCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  infoTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  infoList: {
    gap: 10,
  },
  infoItem: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  infoText: {
    fontSize: 14,
    color: '#666',
    marginLeft: 10,
    flex: 1,
  },
  featuresCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  featuresTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  featuresList: {
    gap: 8,
  },
  featureItem: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  featureText: {
    fontSize: 14,
    color: '#666',
    marginLeft: 10,
  },
  footer: {
    flexDirection: 'row',
    padding: 20,
    gap: 10,
  },
  skipButton: {
    flex: 1,
    backgroundColor: '#f0f0f0',
    borderRadius: 8,
    padding: 15,
    alignItems: 'center',
  },
  skipButtonText: {
    color: '#666',
    fontSize: 16,
    fontWeight: '600',
  },
  nextButton: {
    flex: 1,
    backgroundColor: '#2196F3',
    borderRadius: 8,
    padding: 15,
    alignItems: 'center',
  },
  nextButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  bottomActions: {
    flexDirection: 'row',
    padding: 20,
    paddingTop: 0,
    gap: 10,
  },
  cancelButton: {
    flex: 1,
    backgroundColor: '#f0f0f0',
    borderRadius: 8,
    padding: 15,
    alignItems: 'center',
  },
  cancelButtonText: {
    color: '#666',
    fontSize: 16,
    fontWeight: '600',
  },
  allButton: {
    flex: 1,
    backgroundColor: '#4CAF50',
    borderRadius: 8,
    padding: 15,
    alignItems: 'center',
  },
  allButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
});

export default WebPermissionRequest;
