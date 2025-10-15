import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { CommunityPost } from '../../types';

interface CommunityState {
  posts: CommunityPost[];
  loading: boolean;
  error: string | null;
}

const initialState: CommunityState = {
  posts: [],
  loading: false,
  error: null,
};

export const fetchPosts = createAsyncThunk(
  'community/fetchPosts',
  async (_, { rejectWithValue }) => {
    try {
      return [];
    } catch (error: any) {
      return rejectWithValue(error.message);
    }
  }
);

export const createPost = createAsyncThunk(
  'community/createPost',
  async (post: Omit<CommunityPost, 'id'>, { rejectWithValue }) => {
    try {
      const newPost: CommunityPost = {
        ...post,
        id: Date.now(),
      };
      return newPost;
    } catch (error: any) {
      return rejectWithValue(error.message);
    }
  }
);

const communitySlice = createSlice({
  name: 'community',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    addPost: (state, action: PayloadAction<CommunityPost>) => {
      state.posts.unshift(action.payload);
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchPosts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchPosts.fulfilled, (state, action) => {
        state.loading = false;
        state.posts = action.payload;
        state.error = null;
      })
      .addCase(fetchPosts.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(createPost.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createPost.fulfilled, (state, action) => {
        state.loading = false;
        state.posts.unshift(action.payload);
        state.error = null;
      })
      .addCase(createPost.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { clearError, addPost } = communitySlice.actions;
export default communitySlice.reducer;

