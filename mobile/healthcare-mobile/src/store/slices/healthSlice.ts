import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { HealthData, HealthScore } from '../../types';

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
      return [];
    } catch (error: any) {
      return rejectWithValue(error.message);
    }
  }
);

export const insertHealthData = createAsyncThunk(
  'health/insertHealthData',
  async (data: HealthData, { rejectWithValue }) => {
    try {
      return data;
    } catch (error: any) {
      return rejectWithValue(error.message);
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
      });
  },
});

export const { clearError, addHealthData } = healthSlice.actions;
export default healthSlice.reducer;

