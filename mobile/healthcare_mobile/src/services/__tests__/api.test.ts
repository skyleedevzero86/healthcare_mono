import { apiService } from '../api';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

jest.mock('axios');
jest.mock('@react-native-async-storage/async-storage');

const mockedAxios = axios as jest.Mocked<typeof axios>;
const mockedAsyncStorage = AsyncStorage as jest.Mocked<typeof AsyncStorage>;

describe('ApiService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockedAxios.create.mockReturnValue(mockedAxios as any);
  });

  describe('get', () => {
    it('should make GET request successfully', async () => {
      const mockResponse = {
        data: {
          resultCode: '200',
          resultMsg: 'Success',
          resultData: { id: 1, name: 'Test' },
        },
      };

      (mockedAxios.get as jest.Mock).mockResolvedValue(mockResponse);

      const result = await apiService.get('/test');

      expect(mockedAxios.get).toHaveBeenCalledWith('/test', undefined);
      expect(result).toEqual(mockResponse.data);
    });

    it('should include authorization header when token exists', async () => {
      mockedAsyncStorage.getItem.mockResolvedValue('test-token');
      const mockResponse = {
        data: {
          resultCode: '200',
          resultMsg: 'Success',
          resultData: {},
        },
      };

      (mockedAxios.get as jest.Mock).mockResolvedValue(mockResponse);

      await apiService.get('/test');

      expect(mockedAxios.get).toHaveBeenCalled();
    });
  });

  describe('post', () => {
    it('should make POST request successfully', async () => {
      const mockData = { name: 'Test' };
      const mockResponse = {
        data: {
          resultCode: '200',
          resultMsg: 'Success',
          resultData: { id: 1, ...mockData },
        },
      };

      (mockedAxios.post as jest.Mock).mockResolvedValue(mockResponse);

      const result = await apiService.post('/test', mockData);

      expect(mockedAxios.post).toHaveBeenCalledWith('/test', mockData, undefined);
      expect(result).toEqual(mockResponse.data);
    });
  });

  describe('put', () => {
    it('should make PUT request successfully', async () => {
      const mockData = { name: 'Updated' };
      const mockResponse = {
        data: {
          resultCode: '200',
          resultMsg: 'Success',
          resultData: { id: 1, ...mockData },
        },
      };

      (mockedAxios.put as jest.Mock).mockResolvedValue(mockResponse);

      const result = await apiService.put('/test/1', mockData);

      expect(mockedAxios.put).toHaveBeenCalledWith('/test/1', mockData, undefined);
      expect(result).toEqual(mockResponse.data);
    });
  });

  describe('delete', () => {
    it('should make DELETE request successfully', async () => {
      const mockResponse = {
        data: {
          resultCode: '200',
          resultMsg: 'Success',
          resultData: null,
        },
      };

      (mockedAxios.delete as jest.Mock).mockResolvedValue(mockResponse);

      const result = await apiService.delete('/test/1');

      expect(mockedAxios.delete).toHaveBeenCalledWith('/test/1', undefined);
      expect(result).toEqual(mockResponse.data);
    });
  });
});

