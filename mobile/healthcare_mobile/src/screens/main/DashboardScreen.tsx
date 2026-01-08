import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  RefreshControl,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../../store';
import { useDashboard } from '../../hooks/useDashboard';
import { useRealtimeHealth } from '../../hooks/useRealtimeHealth';
import { RealtimeStatusCard } from '../../components/RealtimeStatusCard';
import { TodayGoalsCard } from '../../components/TodayGoalsCard';
import LoadingSpinner from '../../components/LoadingSpinner';
import { healthKitService } from '../../services/healthKitService';

const DashboardScreen: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { user } = useSelector((state: RootState) => state.auth);
  const { loading } = useDashboard();
  const { data: realtimeData, refresh: refreshRealtime } = useRealtimeHealth();
  const [targetSteps] = useState(10000);

  useEffect(() => {
    if (user?.userId) {
      healthKitService.requestAuthorization();
    }
  }, [user?.userId]);

  const handleRefresh = () => {
    refreshRealtime();
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView
        style={styles.container}
        refreshControl={<RefreshControl refreshing={loading} onRefresh={handleRefresh} />}
      >
        {loading ? (
          <LoadingSpinner />
        ) : (
          <>
            <View style={styles.content}>
              <RealtimeStatusCard
                heartRate={realtimeData.heartRate}
                bloodPressure={{
                  systolic: realtimeData.bloodPressure.systolic,
                  diastolic: realtimeData.bloodPressure.diastolic,
                }}
                temperature={realtimeData.temperature}
                stress={realtimeData.stress}
              />
              
              <TodayGoalsCard
                currentSteps={realtimeData.steps}
                targetSteps={targetSteps}
                yesterdaySleep={realtimeData.sleepHours}
                currentStress={realtimeData.stress}
              />
            </View>
          </>
        )}
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#F5F5F5',
  },
  container: {
    flex: 1,
  },
  content: {
    padding: 20,
  },
});

export default DashboardScreen;
