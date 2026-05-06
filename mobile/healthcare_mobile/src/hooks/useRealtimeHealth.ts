import { useState, useEffect, useCallback } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store';
import { healthKitService } from '../services/healthKitService';

interface RealtimeHealthData {
  heartRate: number;
  bloodPressure: { systolic: number; diastolic: number };
  temperature: number;
  stress: number;
  steps: number;
  sleepHours: number;
}

export const useRealtimeHealth = () => {
  const { user } = useSelector((state: RootState) => state.auth);
  const [data, setData] = useState<RealtimeHealthData>({
    heartRate: 0,
    bloodPressure: { systolic: 0, diastolic: 0 },
    temperature: 0,
    stress: 0,
    steps: 0,
    sleepHours: 0,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    if (!user?.userId) return;

    setLoading(true);
    setError(null);

    try {
      const healthData = await healthKitService.getLatestHealthData(user.userId);
      
      if (healthData) {
        setData({
          heartRate: healthData.heartRate,
          bloodPressure: {
            systolic: healthData.bloodPressureSystolic,
            diastolic: healthData.bloodPressureDiastolic,
          },
          temperature: healthData.bodyTemperature,
          stress: healthData.stress,
          steps: healthData.steps,
          sleepHours: healthData.sleepHours,
        });
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : '데이터 조회 실패');
    } finally {
      setLoading(false);
    }
  }, [user?.userId]);

  useEffect(() => {
    fetchData();
    
    const interval = setInterval(() => {
      fetchData();
    }, 30000);

    return () => clearInterval(interval);
  }, [fetchData]);

  return {
    data,
    loading,
    error,
    refresh: fetchData,
  };
};

