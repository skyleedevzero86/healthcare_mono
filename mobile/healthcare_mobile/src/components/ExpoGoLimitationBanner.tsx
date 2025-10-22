import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Alert } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { isExpoGo, isDevelopmentBuild } from '../utils/expoEnvironment';

interface ExpoGoLimitationBannerProps {
  onDismiss?: () => void;
}

const ExpoGoLimitationBanner: React.FC<ExpoGoLimitationBannerProps> = ({ onDismiss }) => {
  if (!isExpoGo()) {
    return null;
  }

  const handleShowDevelopmentBuildInfo = () => {
    Alert.alert(
      '개발 빌드 사용법',
      'Expo Go의 제한사항을 해결하려면 개발 빌드를 사용하세요:\n\n1. EAS CLI 설치:\nnpm install -g @expo/eas-cli\n\n2. 로그인:\neas login\n\n3. 개발 빌드 생성:\neas build --profile development\n\n4. 빌드된 앱 설치 후 사용',
      [
        { text: '확인' },
        { text: '웹 버전 사용', onPress: () => {
          Alert.alert('웹 버전', '웹 버전에서는 모든 기능이 제한적으로 작동합니다.');
        }}
      ]
    );
  };

  return (
    <View style={styles.container}>
      <View style={styles.content}>
        <View style={styles.iconContainer}>
          <Ionicons name="warning" size={20} color="#FF9800" />
        </View>
        <View style={styles.textContainer}>
          <Text style={styles.title}>Expo Go 제한사항</Text>
          <Text style={styles.message}>
            미디어 라이브러리와 푸시 알림 기능이 제한됩니다. 개발 빌드를 사용하세요.
          </Text>
        </View>
        <TouchableOpacity style={styles.actionButton} onPress={handleShowDevelopmentBuildInfo}>
          <Text style={styles.actionText}>해결방법</Text>
        </TouchableOpacity>
      </View>
      {onDismiss && (
        <TouchableOpacity style={styles.dismissButton} onPress={onDismiss}>
          <Ionicons name="close" size={16} color="#666" />
        </TouchableOpacity>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#FFF3E0',
    borderLeftWidth: 4,
    borderLeftColor: '#FF9800',
    margin: 16,
    borderRadius: 8,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 1,
    },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  content: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 12,
  },
  iconContainer: {
    marginRight: 12,
  },
  textContainer: {
    flex: 1,
  },
  title: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#E65100',
    marginBottom: 2,
  },
  message: {
    fontSize: 12,
    color: '#BF360C',
    lineHeight: 16,
  },
  actionButton: {
    backgroundColor: '#FF9800',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 4,
    marginLeft: 8,
  },
  actionText: {
    color: '#fff',
    fontSize: 12,
    fontWeight: '600',
  },
  dismissButton: {
    position: 'absolute',
    top: 8,
    right: 8,
    padding: 4,
  },
});

export default ExpoGoLimitationBanner;

