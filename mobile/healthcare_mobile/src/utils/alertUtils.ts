import { Alert } from 'react-native';
import { AppError } from './errorNormalizer';
import { getUserFriendlyMessage } from './errorMessageMapper';

export function showErrorAlert(error: AppError): void {
  const friendlyMessage = getUserFriendlyMessage(error.message);
  Alert.alert('오류', friendlyMessage, [{ text: '확인' }]);
}

export function showSuccessAlert(message: string): void {
  Alert.alert('성공', message, [{ text: '확인' }]);
}

export function showConfirmAlert(
  title: string,
  message: string,
  onConfirm: () => void,
  onCancel?: () => void
): void {
  Alert.alert(title, message, [
    { text: '취소', onPress: onCancel, style: 'cancel' },
    { text: '확인', onPress: onConfirm },
  ]);
}
