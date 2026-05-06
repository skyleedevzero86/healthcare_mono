import { API_RESPONSE_CODES } from '../constants/api';

export function getUserFriendlyMessage(message: string): string {
  const lowerMessage = message.toLowerCase();

  if (lowerMessage.includes('network') || lowerMessage.includes('timeout')) {
    return '네트워크 연결을 확인해주세요.';
  }

  if (lowerMessage.includes('unauthorized') || lowerMessage.includes('401')) {
    return '인증이 만료되었습니다. 다시 로그인해주세요.';
  }

  if (lowerMessage.includes('forbidden') || lowerMessage.includes('403')) {
    return '접근 권한이 없습니다.';
  }

  if (lowerMessage.includes('not found') || lowerMessage.includes('404')) {
    return '요청한 정보를 찾을 수 없습니다.';
  }

  if (lowerMessage.includes('server error') || lowerMessage.includes('500')) {
    return '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
  }

  if (lowerMessage.includes('duplicate') || lowerMessage.includes('중복')) {
    return '이미 사용 중인 정보입니다.';
  }

  if (lowerMessage.includes('validation') || lowerMessage.includes('유효성')) {
    return '입력한 정보를 확인해주세요.';
  }

  if (lowerMessage.includes('password') || lowerMessage.includes('비밀번호')) {
    return '비밀번호를 확인해주세요.';
  }

  return message || '오류가 발생했습니다. 다시 시도해주세요.';
}

export function getApiErrorMessage(resultCode: string): string {
  switch (resultCode) {
    case API_RESPONSE_CODES.PARAM_VALID_ERR:
      return '입력한 정보를 확인해주세요.';
    case API_RESPONSE_CODES.AUTH_ERR:
      return '인증에 실패했습니다.';
    case API_RESPONSE_CODES.EXP_JWT_TOKEN_ERR:
      return '로그인이 만료되었습니다. 다시 로그인해주세요.';
    case API_RESPONSE_CODES.INVALID_JWT_TOKEN_ERR:
      return '유효하지 않은 인증 정보입니다.';
    case API_RESPONSE_CODES.DUPLICATE_KEY_ERR:
    case API_RESPONSE_CODES.DUPLICATE_CODE:
      return '이미 사용 중인 정보입니다.';
    case API_RESPONSE_CODES.RESULT_IS_EMPTY:
      return '조회된 결과가 없습니다.';
    case API_RESPONSE_CODES.UPDATE_FAIL:
      return '수정에 실패했습니다.';
    case API_RESPONSE_CODES.INSERT_FAIL:
      return '저장에 실패했습니다.';
    case API_RESPONSE_CODES.UNKNOWN_ERR:
      return '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
    default:
      return '오류가 발생했습니다.';
  }
}
