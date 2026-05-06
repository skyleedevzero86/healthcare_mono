export interface AppError {
  code: string;
  message: string;
  statusCode?: number;
  originalError?: Error;
}

interface ErrorLike {
  code?: string;
  message?: string;
  statusCode?: number;
}

export function normalizeError(error: unknown): AppError {
  if (error instanceof Error) {
    return {
      code: 'UNKNOWN_ERROR',
      message: error.message,
      originalError: error,
    };
  }

  if (typeof error === 'string') {
    return {
      code: 'UNKNOWN_ERROR',
      message: error,
    };
  }

  if (error && typeof error === 'object' && 'message' in error) {
    const errorLike = error as ErrorLike;
    return {
      code: errorLike.code || 'UNKNOWN_ERROR',
      message: errorLike.message || '알 수 없는 오류가 발생했습니다.',
      statusCode: errorLike.statusCode,
      originalError: error instanceof Error ? error : undefined,
    };
  }

  return {
    code: 'UNKNOWN_ERROR',
    message: '알 수 없는 오류가 발생했습니다.',
  };
}
