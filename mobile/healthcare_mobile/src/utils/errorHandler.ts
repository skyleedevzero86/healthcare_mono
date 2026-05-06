import { normalizeError, AppError } from './errorNormalizer';
import { getUserFriendlyMessage } from './errorMessageMapper';
import { showErrorAlert } from './alertUtils';

export function handleError(error: unknown, showAlert: boolean = true): AppError {
  const appError = normalizeError(error);
  
  const friendlyMessage = getUserFriendlyMessage(appError.message);
  const normalizedError: AppError = {
    ...appError,
    message: friendlyMessage,
  };

  if (showAlert) {
    showErrorAlert(normalizedError);
  }

  return normalizedError;
}

export { normalizeError } from './errorNormalizer';

export { getApiErrorMessage } from './errorMessageMapper';

export { showErrorAlert, showSuccessAlert, showConfirmAlert } from './alertUtils';

export type { AppError } from './errorNormalizer';
