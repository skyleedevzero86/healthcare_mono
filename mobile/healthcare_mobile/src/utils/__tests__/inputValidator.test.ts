import { InputValidator } from '../inputValidator';

describe('InputValidator', () => {
  describe('validateEmail', () => {
    it('should validate correct email', () => {
      const result = InputValidator.validateEmail('test@example.com');
      expect(result.valid).toBe(true);
    });

    it('should reject invalid email format', () => {
      const result = InputValidator.validateEmail('invalid-email');
      expect(result.valid).toBe(false);
      expect(result.message).toContain('이메일 형식');
    });

    it('should reject empty email', () => {
      const result = InputValidator.validateEmail('');
      expect(result.valid).toBe(false);
    });

    it('should reject email longer than 100 characters', () => {
      const longEmail = 'a'.repeat(90) + '@example.com';
      const result = InputValidator.validateEmail(longEmail);
      expect(result.valid).toBe(false);
    });
  });

  describe('validatePassword', () => {
    it('should validate correct password', () => {
      const result = InputValidator.validatePassword('Test1234!');
      expect(result.valid).toBe(true);
    });

    it('should reject password shorter than 8 characters', () => {
      const result = InputValidator.validatePassword('Test1!');
      expect(result.valid).toBe(false);
    });

    it('should reject password without uppercase', () => {
      const result = InputValidator.validatePassword('test1234!');
      expect(result.valid).toBe(false);
    });

    it('should reject password without lowercase', () => {
      const result = InputValidator.validatePassword('TEST1234!');
      expect(result.valid).toBe(false);
    });

    it('should reject password without number', () => {
      const result = InputValidator.validatePassword('TestPassword!');
      expect(result.valid).toBe(false);
    });

    it('should reject password without special character', () => {
      const result = InputValidator.validatePassword('TestPassword123');
      expect(result.valid).toBe(false);
    });
  });

  describe('validateUserId', () => {
    it('should validate correct userId', () => {
      const result = InputValidator.validateUserId('testuser123');
      expect(result.valid).toBe(true);
    });

    it('should reject userId shorter than 4 characters', () => {
      const result = InputValidator.validateUserId('abc');
      expect(result.valid).toBe(false);
    });

    it('should reject userId with special characters', () => {
      const result = InputValidator.validateUserId('test@user');
      expect(result.valid).toBe(false);
    });
  });

  describe('validateHealthValue', () => {
    it('should validate heartrate in range', () => {
      const result = InputValidator.validateHealthValue(75, 'heartrate');
      expect(result.valid).toBe(true);
    });

    it('should reject heartrate out of range', () => {
      const result = InputValidator.validateHealthValue(250, 'heartrate');
      expect(result.valid).toBe(false);
    });

    it('should validate temperature in range', () => {
      const result = InputValidator.validateHealthValue(36.5, 'temperature');
      expect(result.valid).toBe(true);
    });

    it('should reject temperature out of range', () => {
      const result = InputValidator.validateHealthValue(50, 'temperature');
      expect(result.valid).toBe(false);
    });
  });

  describe('sanitizeInput', () => {
    it('should remove dangerous characters', () => {
      const result = InputValidator.sanitizeInput('<script>alert("xss")</script>');
      expect(result).not.toContain('<');
      expect(result).not.toContain('>');
    });

    it('should remove javascript protocol', () => {
      const result = InputValidator.sanitizeInput('javascript:alert("xss")');
      expect(result).not.toContain('javascript:');
    });

    it('should trim whitespace', () => {
      const result = InputValidator.sanitizeInput('  test  ');
      expect(result).toBe('test');
    });
  });
});

