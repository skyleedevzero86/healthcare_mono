import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  RefreshControl,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../../store';
import { fetchHealthData, fetchHealthScoreList } from '../../store/slices/healthSlice';
import { fetchUserInfo } from '../../store/slices/userSlice';
import { checkPermissions, getCurrentLocation, sendNotification } from '../../store/slices/permissionSlice';
import PermissionRequest from '../../components/PermissionRequest';
import ExpoGoLimitationBanner from '../../components/ExpoGoLimitationBanner';

const DashboardScreen: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { user } = useSelector((state: RootState) => state.auth);
  const { healthData, healthScore, loading } = useSelector((state: RootState) => state.health);
  const { permissions } = useSelector((state: RootState) => state.permission);
  const [showPermissionRequest, setShowPermissionRequest] = useState(false);
  const [showLimitationBanner, setShowLimitationBanner] = useState(true);

  useEffect(() => {
    if (user?.userId) {
      dispatch(fetchHealthData(user.userId));
      dispatch(fetchHealthScoreList(user.userId));
      dispatch(checkPermissions());
    }
  }, [dispatch, user]);

  const onRefresh = () => {
    if (user?.userId) {
      dispatch(fetchHealthData(user.userId));
      dispatch(fetchHealthScoreList(user.userId));
      dispatch(checkPermissions());
    }
  };

  const handleLocationRequest = async () => {
    try {
      await dispatch(getCurrentLocation()).unwrap();
    } catch (error) {
      console.error('Location request failed:', error);
    }
  };

  const handleNotificationRequest = async () => {
    try {
      await dispatch(sendNotification({
        title: '헬스케어 알림',
        body: '건강 상태를 확인해보세요!',
      })).unwrap();
    } catch (error) {
      console.error('Notification request failed:', error);
    }
  };

  const getLatestHealthData = () => {
    return healthData.length > 0 ? healthData[0] : null;
  };

  const latestData = getLatestHealthData();

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView
        style={styles.container}
        refreshControl={
          <RefreshControl refreshing={loading} onRefresh={onRefresh} />
        }
      >
        <View style={styles.header}>
          <Text style={styles.greeting}>
            안녕하세요, {user?.userNm || '사용자'}님!
          </Text>
          <Text style={styles.subtitle}>오늘의 건강 상태를 확인해보세요</Text>
        </View>

        {showLimitationBanner && (
          <ExpoGoLimitationBanner onDismiss={() => setShowLimitationBanner(false)} />
        )}

        {latestData && (
          <View style={styles.healthCard}>
            <Text style={styles.cardTitle}>실시간 건강 데이터</Text>
            <View style={styles.healthGrid}>
              <View style={styles.healthItem}>
                <Text style={styles.healthLabel}>심박수</Text>
                <Text style={styles.healthValue}>{latestData.heartrate} bpm</Text>
              </View>
              <View style={styles.healthItem}>
                <Text style={styles.healthLabel}>체온</Text>
                <Text style={styles.healthValue}>{latestData.temperature}°C</Text>
              </View>
              <View style={styles.healthItem}>
                <Text style={styles.healthLabel}>혈압</Text>
                <Text style={styles.healthValue}>
                  {latestData.bloodpressMax}/{latestData.bloodpressMin}
                </Text>
              </View>
              <View style={styles.healthItem}>
                <Text style={styles.healthLabel}>산소포화도</Text>
                <Text style={styles.healthValue}>{latestData.spo2}%</Text>
              </View>
            </View>
          </View>
        )}

        {healthScore && (
          <View style={styles.scoreCard}>
            <Text style={styles.cardTitle}>건강 점수</Text>
            <View style={styles.scoreContainer}>
              <Text style={styles.mainScore}>{healthScore.healthScore}</Text>
              <Text style={styles.scoreLabel}>점</Text>
            </View>
            <View style={styles.scoreBreakdown}>
              <View style={styles.scoreItem}>
                <Text style={styles.scoreItemLabel}>수면</Text>
                <Text style={styles.scoreItemValue}>{healthScore.userSleepScore}</Text>
              </View>
              <View style={styles.scoreItem}>
                <Text style={styles.scoreItemLabel}>운동</Text>
                <Text style={styles.scoreItemValue}>{healthScore.userExerciseScore}</Text>
              </View>
              <View style={styles.scoreItem}>
                <Text style={styles.scoreItemLabel}>스트레스</Text>
                <Text style={styles.scoreItemValue}>{healthScore.userStressScore}</Text>
              </View>
            </View>
          </View>
        )}

        <View style={styles.quickActions}>
          <Text style={styles.cardTitle}>빠른 액션</Text>
          <View style={styles.actionGrid}>
            <TouchableOpacity style={styles.actionButton}>
              <Text style={styles.actionButtonText}>건강 데이터 입력</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.actionButton}>
              <Text style={styles.actionButtonText}>차트 보기</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.actionButton}>
              <Text style={styles.actionButtonText}>커뮤니티</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.actionButton}>
              <Text style={styles.actionButtonText}>설정</Text>
            </TouchableOpacity>
          </View>
        </View>

        <View style={styles.permissionSection}>
          <Text style={styles.cardTitle}>권한 관리</Text>
          <View style={styles.permissionGrid}>
            <TouchableOpacity 
              style={[styles.permissionButton, permissions.location === 'granted' && styles.grantedButton]}
              onPress={handleLocationRequest}
            >
              <Text style={[styles.permissionButtonText, permissions.location === 'granted' && styles.grantedText]}>
                위치 정보
              </Text>
            </TouchableOpacity>
            <TouchableOpacity 
              style={[styles.permissionButton, permissions.notifications === 'granted' && styles.grantedButton]}
              onPress={handleNotificationRequest}
            >
              <Text style={[styles.permissionButtonText, permissions.notifications === 'granted' && styles.grantedText]}>
                알림
              </Text>
            </TouchableOpacity>
            <TouchableOpacity 
              style={styles.permissionButton}
              onPress={() => setShowPermissionRequest(true)}
            >
              <Text style={styles.permissionButtonText}>권한 설정</Text>
            </TouchableOpacity>
          </View>
        </View>
      </ScrollView>

      <PermissionRequest
        visible={showPermissionRequest}
        onClose={() => setShowPermissionRequest(false)}
        onComplete={() => setShowPermissionRequest(false)}
      />
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
  greeting: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
  },
  healthCard: {
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
  scoreCard: {
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
  cardTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  healthGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  healthItem: {
    width: '48%',
    backgroundColor: '#f8f9fa',
    borderRadius: 8,
    padding: 15,
    marginBottom: 10,
    alignItems: 'center',
  },
  healthLabel: {
    fontSize: 14,
    color: '#666',
    marginBottom: 5,
  },
  healthValue: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#2196F3',
  },
  scoreContainer: {
    alignItems: 'center',
    marginBottom: 20,
  },
  mainScore: {
    fontSize: 48,
    fontWeight: 'bold',
    color: '#2196F3',
  },
  scoreLabel: {
    fontSize: 18,
    color: '#666',
  },
  scoreBreakdown: {
    flexDirection: 'row',
    justifyContent: 'space-around',
  },
  scoreItem: {
    alignItems: 'center',
  },
  scoreItemLabel: {
    fontSize: 14,
    color: '#666',
    marginBottom: 5,
  },
  scoreItemValue: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
  },
  quickActions: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  actionGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  actionButton: {
    width: '48%',
    backgroundColor: '#2196F3',
    borderRadius: 8,
    padding: 15,
    marginBottom: 10,
    alignItems: 'center',
  },
  actionButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
  },
  permissionSection: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  permissionGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  permissionButton: {
    width: '30%',
    backgroundColor: '#f0f0f0',
    borderRadius: 8,
    padding: 12,
    marginBottom: 10,
    alignItems: 'center',
  },
  grantedButton: {
    backgroundColor: '#4CAF50',
  },
  permissionButtonText: {
    color: '#666',
    fontSize: 12,
    fontWeight: '600',
  },
  grantedText: {
    color: '#fff',
  },
});

export default DashboardScreen;
