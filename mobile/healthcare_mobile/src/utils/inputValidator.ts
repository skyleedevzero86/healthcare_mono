export interface ValidationResult {
  valid: boolean;
  message?: string;
}

export function validateEmail(email: string): ValidationResult {
  if (!email || email.trim().length === 0) {
    return { valid: false, message: '이메일을 입력해주세요.' };
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return { valid: false, message: '올바른 이메일 형식이 아닙니다.' };
  }

  if (email.length > 100) {
    return { valid: false, message: '이메일은 100자 이하여야 합니다.' };
  }

  return { valid: true };
}

export function validatePassword(password: string): ValidationResult {
  if (!password || password.length === 0) {
    return { valid: false, message: '비밀번호를 입력해주세요.' };
  }

  if (password.length < 8) {
    return { valid: false, message: '비밀번호는 8자 이상이어야 합니다.' };
  }

  if (password.length > 50) {
    return { valid: false, message: '비밀번호는 50자 이하여야 합니다.' };
  }

  const hasUpperCase = /[A-Z]/.test(password);
  const hasLowerCase = /[a-z]/.test(password);
  const hasNumber = /[0-9]/.test(password);
  const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);

  if (!hasUpperCase || !hasLowerCase || !hasNumber || !hasSpecialChar) {
    return {
      valid: false,
      message: '비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.',
    };
  }

  return { valid: true };
}

export function validateUserId(userId: string): ValidationResult {
  if (!userId || userId.trim().length === 0) {
    return { valid: false, message: '아이디를 입력해주세요.' };
  }

  if (userId.length < 4) {
    return { valid: false, message: '아이디는 4자 이상이어야 합니다.' };
  }

  if (userId.length > 20) {
    return { valid: false, message: '아이디는 20자 이하여야 합니다.' };
  }

  const userIdRegex = /^[a-zA-Z0-9_]+$/;
  if (!userIdRegex.test(userId)) {
    return { valid: false, message: '아이디는 영문, 숫자, 언더스코어만 사용할 수 있습니다.' };
  }

  return { valid: true };
}

export function validatePhoneNumber(phone: string): ValidationResult {
  if (!phone || phone.trim().length === 0) {
    return { valid: false, message: '전화번호를 입력해주세요.' };
  }

  const phoneRegex = /^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$/;
  if (!phoneRegex.test(phone.replace(/-/g, ''))) {
    return { valid: false, message: '올바른 전화번호 형식이 아닙니다.' };
  }

  return { valid: true };
}

export function validateHealthValue(
  value: number,
  type: 'heartrate' | 'temperature' | 'spo2' | 'step' | 'bloodpress' | 'sleep'
): ValidationResult {
  switch (type) {
    case 'heartrate':
      if (value < 30 || value > 220) {
        return { valid: false, message: '심박수는 30-220 bpm 범위여야 합니다.' };
      }
      break;
    case 'temperature':
      if (value < 30 || value > 45) {
        return { valid: false, message: '체온은 30-45°C 범위여야 합니다.' };
      }
      break;
    case 'spo2':
      if (value < 70 || value > 100) {
        return { valid: false, message: '산소포화도는 70-100% 범위여야 합니다.' };
      }
      break;
    case 'step':
      if (value < 0 || value > 100000) {
        return { valid: false, message: '걸음수는 0-100000 범위여야 합니다.' };
      }
      break;
    case 'bloodpress':
      if (value < 40 || value > 250) {
        return { valid: false, message: '혈압은 40-250 범위여야 합니다.' };
      }
      break;
    case 'sleep':
      if (value < 0 || value > 24) {
        return { valid: false, message: '수면 시간은 0-24시간 범위여야 합니다.' };
      }
      break;
  }

  return { valid: true };
}

export function sanitizeInput(input: string): string {
  if (!input) return '';
  return input
    .trim()
    .replace(/[<>]/g, '')
    .replace(/javascript:/gi, '')
    .replace(/on\w+=/gi, '');
}

export function validateRequired(value: unknown, fieldName: string): ValidationResult {
  if (value === null || value === undefined || value === '') {
    return { valid: false, message: `${fieldName}을(를) 입력해주세요.` };
  }
  return { valid: true };
}
