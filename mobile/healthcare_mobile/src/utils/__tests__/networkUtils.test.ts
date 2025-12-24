import { checkNetworkConnection, isNetworkAvailable, getNetworkErrorMessage, NetworkState } from '../networkUtils';
import NetInfo from '@react-native-community/netinfo';

jest.mock('@react-native-community/netinfo');

describe('networkUtils', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('checkNetworkConnection', () => {
    it('should return network state when connected', async () => {
      (NetInfo.fetch as jest.Mock).mockResolvedValue({
        isConnected: true,
        type: 'wifi',
        isInternetReachable: true,
      });

      const result = await checkNetworkConnection();

      expect(result).toEqual({
        isConnected: true,
        type: 'wifi',
        isInternetReachable: true,
      });
    });

    it('should return disconnected state when network is unavailable', async () => {
      (NetInfo.fetch as jest.Mock).mockResolvedValue({
        isConnected: false,
        type: null,
        isInternetReachable: false,
      });

      const result = await checkNetworkConnection();

      expect(result).toEqual({
        isConnected: false,
        type: null,
        isInternetReachable: false,
      });
    });

    it('should handle errors gracefully', async () => {
      (NetInfo.fetch as jest.Mock).mockRejectedValue(new Error('Network error'));

      const result = await checkNetworkConnection();

      expect(result).toEqual({
        isConnected: false,
        type: null,
        isInternetReachable: false,
      });
    });
  });

  describe('isNetworkAvailable', () => {
    it('should return true when network is connected and reachable', async () => {
      (NetInfo.fetch as jest.Mock).mockResolvedValue({
        isConnected: true,
        isInternetReachable: true,
      });

      const result = await isNetworkAvailable();

      expect(result).toBe(true);
    });

    it('should return false when network is not connected', async () => {
      (NetInfo.fetch as jest.Mock).mockResolvedValue({
        isConnected: false,
        isInternetReachable: false,
      });

      const result = await isNetworkAvailable();

      expect(result).toBe(false);
    });

    it('should return false when network is connected but not reachable', async () => {
      (NetInfo.fetch as jest.Mock).mockResolvedValue({
        isConnected: true,
        isInternetReachable: false,
      });

      const result = await isNetworkAvailable();

      expect(result).toBe(false);
    });
  });

  describe('getNetworkErrorMessage', () => {
    it('should return message for no connection', () => {
      const state: NetworkState = {
        isConnected: false,
        type: null,
        isInternetReachable: false,
      };

      const message = getNetworkErrorMessage(state);

      expect(message).toContain('No internet connection');
    });

    it('should return message for unreachable internet', () => {
      const state: NetworkState = {
        isConnected: true,
        type: 'wifi',
        isInternetReachable: false,
      };

      const message = getNetworkErrorMessage(state);

      expect(message).toContain('Internet is not reachable');
    });

    it('should return success message when network is available', () => {
      const state: NetworkState = {
        isConnected: true,
        type: 'wifi',
        isInternetReachable: true,
      };

      const message = getNetworkErrorMessage(state);

      expect(message).toContain('Network connection is available');
    });
  });
});

