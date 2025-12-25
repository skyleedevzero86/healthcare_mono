import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../store';
import { fetchHealthData, fetchHealthScoreList } from '../store/slices/healthSlice';
import { checkPermissions } from '../store/slices/permissionSlice';

interface UseDashboardResult {
  user: any;
  healthData: any[];
  healthScore: any;
  loading: boolean;
  latestHealthData: any;
  refresh: () => void;
}

export const useDashboard = (): UseDashboardResult => {
  const dispatch = useDispatch<AppDispatch>();
  const { user } = useSelector((state: RootState) => state.auth);
  const { healthData, healthScore, loading } = useSelector((state: RootState) => state.health);

  useEffect(() => {
    if (user?.userId) {
      dispatch(fetchHealthData(user.userId));
      dispatch(fetchHealthScoreList(user.userId));
      dispatch(checkPermissions());
    }
  }, [dispatch, user]);

  const refresh = () => {
    if (user?.userId) {
      dispatch(fetchHealthData(user.userId));
      dispatch(fetchHealthScoreList(user.userId));
      dispatch(checkPermissions());
    }
  };

  const latestHealthData = healthData.length > 0 ? healthData[0] : null;

  return {
    user,
    healthData,
    healthScore,
    loading,
    latestHealthData,
    refresh,
  };
};

