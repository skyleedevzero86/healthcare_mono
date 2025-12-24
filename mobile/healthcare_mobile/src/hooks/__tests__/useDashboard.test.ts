import { renderHook, waitFor } from '@testing-library/react-native';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import { useDashboard } from '../useDashboard';
import { fetchHealthData, fetchHealthScoreList } from '../../store/slices/healthSlice';
import { checkPermissions } from '../../store/slices/permissionSlice';
import healthReducer from '../../store/slices/healthSlice';
import permissionReducer from '../../store/slices/permissionSlice';
import authReducer from '../../store/slices/authSlice';

jest.mock('../../store/slices/healthSlice');
jest.mock('../../store/slices/permissionSlice');

const createMockStore = (initialState = {}) => {
  return configureStore({
    reducer: {
      health: healthReducer,
      permission: permissionReducer,
      auth: authReducer,
    },
    preloadedState: {
      auth: {
        user: { userId: 'test', userNm: 'Test User' },
        token: null,
        isAuthenticated: true,
        loading: false,
        error: null,
      },
      health: {
        healthData: [],
        healthScore: null,
        loading: false,
        error: null,
      },
      permission: {
        permissions: {
          location: 'undetermined',
          camera: 'undetermined',
          microphone: 'undetermined',
          healthData: 'undetermined',
          notifications: 'undetermined',
        },
        location: null,
        loading: false,
        error: null,
      },
      ...initialState,
    },
  });
};

describe('useDashboard', () => {
  const mockDispatch = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    (fetchHealthData as jest.Mock).mockReturnValue({ type: 'health/fetchHealthData/pending' });
    (fetchHealthScoreList as jest.Mock).mockReturnValue({
      type: 'health/fetchHealthScoreList/pending',
    });
    (checkPermissions as jest.Mock).mockReturnValue({ type: 'permission/checkPermissions/pending' });
  });

  it('should fetch data on mount', async () => {
    const store = createMockStore();
    store.dispatch = mockDispatch;

    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <Provider store={store}>{children}</Provider>
    );

    renderHook(() => useDashboard(), { wrapper });

    await waitFor(() => {
      expect(mockDispatch).toHaveBeenCalled();
    });
  });

  it('should return latest health data', () => {
    const store = createMockStore({
      health: {
        healthData: [
          {
            userId: 'test',
            time: '2024-01-01T00:00:00Z',
            heartrate: 70,
            temperature: 36.5,
            spo2: 98,
            step: 5000,
            stress: 50,
            bloodpressMin: 80,
            bloodpressMax: 120,
            repiratory: 16,
            sleep: 7,
          },
        ],
        healthScore: null,
        loading: false,
        error: null,
      },
    });

    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <Provider store={store}>{children}</Provider>
    );

    const { result } = renderHook(() => useDashboard(), { wrapper });

    expect(result.current.latestHealthData).not.toBeNull();
    expect(result.current.latestHealthData?.heartrate).toBe(70);
  });

  it('should return null when no health data', () => {
    const store = createMockStore();

    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <Provider store={store}>{children}</Provider>
    );

    const { result } = renderHook(() => useDashboard(), { wrapper });

    expect(result.current.latestHealthData).toBeNull();
  });
});

