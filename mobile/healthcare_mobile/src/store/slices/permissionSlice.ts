import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { PermissionStatus, LocationData, SensorData } from '../../types';
import { permissionService } from '../../services/permissionService';

interface PermissionState {
  permissions: PermissionStatus;
  location: LocationData | null;
  sensorData: SensorData | null;
  loading: boolean;
  error: string | null;
}

const initialState: PermissionState = {
  permissions: {
    location: 'undetermined',
    camera: 'undetermined',
    microphone: 'undetermined',
    healthData: 'undetermined',
    notifications: 'undetermined',
  },
  location: null,
  sensorData: null,
  loading: false,
  error: null,
};

export const checkPermissions = createAsyncThunk(
  'permission/checkPermissions',
  async (_, { rejectWithValue }) => {
    try {
      const permissions = await permissionService.checkAllPermissions();
      return permissions;
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : 'Unknown error');
    }
  }
);

export const requestAllPermissions = createAsyncThunk(
  'permission/requestAllPermissions',
  async (_, { rejectWithValue }) => {
    try {
      const permissions = await permissionService.requestAllPermissions();
      return permissions;
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : 'Unknown error');
    }
  }
);

export const requestLocationPermission = createAsyncThunk(
  'permission/requestLocationPermission',
  async (_, { rejectWithValue }) => {
    try {
      const granted = await permissionService.requestLocationPermission();
      return granted;
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : 'Unknown error');
    }
  }
);

export const requestCameraPermission = createAsyncThunk(
  'permission/requestCameraPermission',
  async (_, { rejectWithValue }) => {
    try {
      const granted = await permissionService.requestCameraPermission();
      return granted;
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : 'Unknown error');
    }
  }
);

export const requestMicrophonePermission = createAsyncThunk(
  'permission/requestMicrophonePermission',
  async (_, { rejectWithValue }) => {
    try {
      const granted = await permissionService.requestMicrophonePermission();
      return granted;
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : 'Unknown error');
    }
  }
);

export const requestNotificationPermission = createAsyncThunk(
  'permission/requestNotificationPermission',
  async (_, { rejectWithValue }) => {
    try {
      const granted = await permissionService.requestNotificationPermission();
      return granted;
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : 'Unknown error');
    }
  }
);

export const getCurrentLocation = createAsyncThunk(
  'permission/getCurrentLocation',
  async (_, { rejectWithValue }) => {
    try {
      const location = await permissionService.getCurrentLocation();
      return location;
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : 'Unknown error');
    }
  }
);

export const takePicture = createAsyncThunk(
  'permission/takePicture',
  async (_, { rejectWithValue }) => {
    try {
      const imageUri = await permissionService.takePicture();
      return imageUri;
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : 'Unknown error');
    }
  }
);

export const selectImageFromLibrary = createAsyncThunk(
  'permission/selectImageFromLibrary',
  async (_, { rejectWithValue }) => {
    try {
      const imageUri = await permissionService.selectImageFromLibrary();
      return imageUri;
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : 'Unknown error');
    }
  }
);

export const sendNotification = createAsyncThunk(
  'permission/sendNotification',
  async ({ title, body }: { title: string; body: string }, { rejectWithValue }) => {
    try {
      await permissionService.sendNotification(title, body);
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : 'Unknown error');
    }
  }
);

const permissionSlice = createSlice({
  name: 'permission',
  initialState,
  reducers: {
    setLocation: (state, action: PayloadAction<LocationData>) => {
      state.location = action.payload;
    },
    setSensorData: (state, action: PayloadAction<SensorData>) => {
      state.sensorData = action.payload;
    },
    clearError: (state) => {
      state.error = null;
    },
    resetPermissions: (state) => {
      state.permissions = initialState.permissions;
      state.location = null;
      state.sensorData = null;
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(checkPermissions.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(checkPermissions.fulfilled, (state, action) => {
        state.loading = false;
        state.permissions = action.payload;
      })
      .addCase(checkPermissions.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(requestAllPermissions.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(requestAllPermissions.fulfilled, (state, action) => {
        state.loading = false;
        state.permissions = action.payload;
      })
      .addCase(requestAllPermissions.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(requestLocationPermission.fulfilled, (state, action) => {
        state.permissions.location = action.payload ? 'granted' : 'denied';
      })
      .addCase(requestCameraPermission.fulfilled, (state, action) => {
        state.permissions.camera = action.payload ? 'granted' : 'denied';
      })
      .addCase(requestMicrophonePermission.fulfilled, (state, action) => {
        state.permissions.microphone = action.payload ? 'granted' : 'denied';
      })
      .addCase(requestNotificationPermission.fulfilled, (state, action) => {
        state.permissions.notifications = action.payload ? 'granted' : 'denied';
      })
      .addCase(getCurrentLocation.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getCurrentLocation.fulfilled, (state, action) => {
        state.loading = false;
        state.location = action.payload;
      })
      .addCase(getCurrentLocation.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(takePicture.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(takePicture.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(takePicture.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(selectImageFromLibrary.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(selectImageFromLibrary.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(selectImageFromLibrary.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(sendNotification.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(sendNotification.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(sendNotification.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { setLocation, setSensorData, clearError, resetPermissions } = permissionSlice.actions;
export default permissionSlice.reducer;
