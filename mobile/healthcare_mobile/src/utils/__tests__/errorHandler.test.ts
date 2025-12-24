import { ErrorHandler } from '../errorHandler';
import { API_RESPONSE_CODES } from '../../constants/api';

describe('ErrorHandler', () => {
  describe('normalizeError', () => {
    it('should normalize Error object', () => {
      const error = new Error('Test error');
      const result = ErrorHandler.normalizeError(error);

      expect(result.code).toBe('UNKNOWN_ERROR');
      expect(result.message).toBe('Test error');
      expect(result.originalError).toBe(error);
    });

    it('should normalize string error', () => {
      const result = ErrorHandler.normalizeError('String error');

      expect(result.code).toBe('UNKNOWN_ERROR');
      expect(result.message).toBe('String error');
    });

    it('should normalize object error', () => {
      const error = { code: 'CUSTOM_ERROR', message: 'Custom error', statusCode: 400 };
      const result = ErrorHandler.normalizeError(error);

      expect(result.code).toBe('CUSTOM_ERROR');
      expect(result.message).toBe('Custom error');
      expect(result.statusCode).toBe(400);
    });
  });

  describe('getUserFriendlyMessage', () => {
    it('should return friendly message for network error', () => {
      const message = ErrorHandler.getUserFriendlyMessage('Network Error');
      expect(message).toBe('네트워크 연결을 확인해주세요.');
    });

    it('should return friendly message for unauthorized error', () => {
      const message = ErrorHandler.getUserFriendlyMessage('Unauthorized 401');
      expect(message).toBe('인증이 만료되었습니다. 다시 로그인해주세요.');
    });

    it('should return friendly message for server error', () => {
      const message = ErrorHandler.getUserFriendlyMessage('Server Error 500');
      expect(message).toBe('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    });

    it('should return original message if no match', () => {
      const message = ErrorHandler.getUserFriendlyMessage('Custom error message');
      expect(message).toBe('Custom error message');
    });
  });

  describe('getApiErrorMessage', () => {
    it('should return message for PARAM_VALID_ERR', () => {
      const message = ErrorHandler.getApiErrorMessage(API_RESPONSE_CODES.PARAM_VALID_ERR);
      expect(message).toBe('입력한 정보를 확인해주세요.');
    });

    it('should return message for AUTH_ERR', () => {
      const message = ErrorHandler.getApiErrorMessage(API_RESPONSE_CODES.AUTH_ERR);
      expect(message).toBe('인증에 실패했습니다.');
    });

    it('should return default message for unknown code', () => {
      const message = ErrorHandler.getApiErrorMessage('UNKNOWN_CODE');
      expect(message).toBe('오류가 발생했습니다.');
    });
  });
});

