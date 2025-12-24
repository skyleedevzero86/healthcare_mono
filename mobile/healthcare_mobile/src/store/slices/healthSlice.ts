import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { HealthData, HealthScore } from '../../types/health';
import { healthService } from '../../services/healthService';
import { ErrorHandler } from '../../utils/errorHandler';

interface HealthState {
  healthData: HealthData[];
  healthScore: HealthScore | null;
  loading: boolean;
  error: string | null;
}

const initialState: HealthState = {
  healthData: [],
  healthScore: null,
  loading: false,
  error: null,
};

export const fetchHealthData = createAsyncThunk(
  'health/fetchHealthData',
  async (userId: string, { rejectWithValue }) => {
    try {
      const data = await healthService.fetchHealthData(userId);
      return data;
    } catch (error) {
      const appError = ErrorHandler.normalizeError(error);
      return rejectWithValue(appError.message);
    }
  }
);

export const insertHealthData = createAsyncThunk(
  'health/insertHealthData',
  async (data: HealthData, { rejectWithValue }) => {
    try {
      const result = await healthService.insertHealthData(data);
      return result;
    } catch (error) {
      const appError = ErrorHandler.normalizeError(error);
      return rejectWithValue(appError.message);
    }
  }
);

export const fetchHealthScoreList = createAsyncThunk(
  'health/fetchHealthScoreList',
  async (userId: string, { rejectWithValue }) => {
    try {
      const score = await healthService.fetchHealthScoreList(userId);
      return score;
    } catch (error) {
      const appError = ErrorHandler.normalizeError(error);
      return rejectWithValue(appError.message);
    }
  }
);

export const fetchHealthChart = createAsyncThunk(
  'health/fetchHealthChart',
  async (
    { userId, startDate, endDate }: { userId: string; startDate: string; endDate: string },
    { rejectWithValue }
  ) => {
    try {
      const data = await healthService.fetchHealthChart(userId, startDate, endDate);
      return data;
    } catch (error) {
      const appError = ErrorHandler.normalizeError(error);
      return rejectWithValue(appError.message);
    }
  }
);

const healthSlice = createSlice({
  name: 'health',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    addHealthData: (state, action: PayloadAction<HealthData>) => {
      state.healthData.unshift(action.payload);
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchHealthData.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchHealthData.fulfilled, (state, action) => {
        state.loading = false;
        state.healthData = action.payload;
        state.error = null;
      })
      .addCase(fetchHealthData.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(insertHealthData.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(insertHealthData.fulfilled, (state, action) => {
        state.loading = false;
        state.healthData.unshift(action.payload);
        state.error = null;
      })
      .addCase(insertHealthData.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(fetchHealthScoreList.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchHealthScoreList.fulfilled, (state, action) => {
        state.loading = false;
        state.healthScore = action.payload;
        state.error = null;
      })
      .addCase(fetchHealthScoreList.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(fetchHealthChart.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchHealthChart.fulfilled, (state, action) => {
        state.loading = false;
        state.error = null;
      })
      .addCase(fetchHealthChart.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { clearError, addHealthData } = healthSlice.actions;
export default healthSlice.reducer;
