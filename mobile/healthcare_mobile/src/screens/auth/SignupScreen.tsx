import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RootStackParamList } from '../../types';
import { RootState, AppDispatch } from '../../store';
import { signup } from '../../store/slices/authSlice';

type SignupScreenNavigationProp = StackNavigationProp<RootStackParamList, 'Signup'>;

const SignupScreen: React.FC = () => {
  const navigation = useNavigation<SignupScreenNavigationProp>();
  const dispatch = useDispatch<AppDispatch>();
  const { loading, error } = useSelector((state: RootState) => state.auth);

  const [formData, setFormData] = useState({
    userId: '',
    userPwEnc: '',
    userNm: '',
    userRoleFk: 'USER',
    birthEnc: '',
    telNumEnc: '',
    email: '',
    gender: '',
    bloodType: '',
    agreementYn: 'N',
  });

  const handleSignup = async () => {
    if (!formData.userId || !formData.userPwEnc || !formData.userNm || !formData.email) {
      Alert.alert('오류', '필수 정보를 모두 입력해주세요.');
      return;
    }

    if (formData.agreementYn !== 'Y') {
      Alert.alert('오류', '개인정보 처리방침에 동의해주세요.');
      return;
    }

    try {
      await dispatch(signup(formData)).unwrap();
      Alert.alert('성공', '회원가입이 완료되었습니다.', [
        { text: '확인', onPress: () => navigation.navigate('Login') }
      ]);
    } catch (error) {
      Alert.alert('회원가입 실패', error as string);
    }
  };

  const handleBack = () => {
    navigation.goBack();
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <KeyboardAvoidingView
        style={styles.container}
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      >
        <ScrollView contentContainerStyle={styles.scrollContainer}>
          <View style={styles.header}>
            <TouchableOpacity onPress={handleBack} style={styles.backButton}>
              <Text style={styles.backButtonText}>← 뒤로</Text>
            </TouchableOpacity>
            <Text style={styles.title}>회원가입</Text>
          </View>

          <View style={styles.form}>
            <View style={styles.inputContainer}>
              <Text style={styles.label}>아이디 *</Text>
              <TextInput
                style={styles.input}
                value={formData.userId}
                onChangeText={(text) => setFormData({ ...formData, userId: text })}
                placeholder="아이디를 입력하세요"
                autoCapitalize="none"
                autoCorrect={false}
              />
            </View>

            <View style={styles.inputContainer}>
              <Text style={styles.label}>비밀번호 *</Text>
              <TextInput
                style={styles.input}
                value={formData.userPwEnc}
                onChangeText={(text) => setFormData({ ...formData, userPwEnc: text })}
                placeholder="비밀번호를 입력하세요"
                secureTextEntry
                autoCapitalize="none"
                autoCorrect={false}
              />
            </View>

            <View style={styles.inputContainer}>
              <Text style={styles.label}>이름 *</Text>
              <TextInput
                style={styles.input}
                value={formData.userNm}
                onChangeText={(text) => setFormData({ ...formData, userNm: text })}
                placeholder="이름을 입력하세요"
              />
            </View>

            <View style={styles.inputContainer}>
              <Text style={styles.label}>이메일 *</Text>
              <TextInput
                style={styles.input}
                value={formData.email}
                onChangeText={(text) => setFormData({ ...formData, email: text })}
                placeholder="이메일을 입력하세요"
                keyboardType="email-address"
                autoCapitalize="none"
                autoCorrect={false}
              />
            </View>

            <View style={styles.inputContainer}>
              <Text style={styles.label}>생년월일</Text>
              <TextInput
                style={styles.input}
                value={formData.birthEnc}
                onChangeText={(text) => setFormData({ ...formData, birthEnc: text })}
                placeholder="YYYY-MM-DD"
              />
            </View>

            <View style={styles.inputContainer}>
              <Text style={styles.label}>전화번호</Text>
              <TextInput
                style={styles.input}
                value={formData.telNumEnc}
                onChangeText={(text) => setFormData({ ...formData, telNumEnc: text })}
                placeholder="전화번호를 입력하세요"
                keyboardType="phone-pad"
              />
            </View>

            <View style={styles.inputContainer}>
              <Text style={styles.label}>성별</Text>
              <View style={styles.radioContainer}>
                <TouchableOpacity
                  style={[styles.radioButton, formData.gender === 'M' && styles.radioButtonSelected]}
                  onPress={() => setFormData({ ...formData, gender: 'M' })}
                >
                  <Text style={[styles.radioText, formData.gender === 'M' && styles.radioTextSelected]}>남성</Text>
                </TouchableOpacity>
                <TouchableOpacity
                  style={[styles.radioButton, formData.gender === 'F' && styles.radioButtonSelected]}
                  onPress={() => setFormData({ ...formData, gender: 'F' })}
                >
                  <Text style={[styles.radioText, formData.gender === 'F' && styles.radioTextSelected]}>여성</Text>
                </TouchableOpacity>
              </View>
            </View>

            <View style={styles.inputContainer}>
              <Text style={styles.label}>혈액형</Text>
              <View style={styles.radioContainer}>
                {['A', 'B', 'AB', 'O'].map((type) => (
                  <TouchableOpacity
                    key={type}
                    style={[styles.radioButton, formData.bloodType === type && styles.radioButtonSelected]}
                    onPress={() => setFormData({ ...formData, bloodType: type })}
                  >
                    <Text style={[styles.radioText, formData.bloodType === type && styles.radioTextSelected]}>{type}형</Text>
                  </TouchableOpacity>
                ))}
              </View>
            </View>

            <View style={styles.agreementContainer}>
              <TouchableOpacity
                style={styles.agreementButton}
                onPress={() => setFormData({ ...formData, agreementYn: formData.agreementYn === 'Y' ? 'N' : 'Y' })}
              >
                <Text style={styles.agreementText}>
                  {formData.agreementYn === 'Y' ? '✓' : '○'} 개인정보 처리방침에 동의합니다 *
                </Text>
              </TouchableOpacity>
            </View>

            <TouchableOpacity
              style={[styles.signupButton, loading && styles.disabledButton]}
              onPress={handleSignup}
              disabled={loading}
            >
              <Text style={styles.signupButtonText}>
                {loading ? '가입 중...' : '회원가입'}
              </Text>
            </TouchableOpacity>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  scrollContainer: {
    flexGrow: 1,
    padding: 20,
  },
  header: {
    marginBottom: 20,
  },
  backButton: {
    marginBottom: 10,
  },
  backButtonText: {
    fontSize: 16,
    color: '#2196F3',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
  },
  form: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 24,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  inputContainer: {
    marginBottom: 20,
  },
  label: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 8,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    backgroundColor: '#f9f9f9',
  },
  radioContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  radioButton: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 12,
    marginRight: 10,
    marginBottom: 10,
    backgroundColor: '#f9f9f9',
  },
  radioButtonSelected: {
    backgroundColor: '#2196F3',
    borderColor: '#2196F3',
  },
  radioText: {
    fontSize: 14,
    color: '#333',
  },
  radioTextSelected: {
    color: '#fff',
  },
  agreementContainer: {
    marginBottom: 20,
  },
  agreementButton: {
    padding: 10,
  },
  agreementText: {
    fontSize: 14,
    color: '#666',
  },
  signupButton: {
    backgroundColor: '#2196F3',
    borderRadius: 8,
    padding: 16,
    alignItems: 'center',
  },
  disabledButton: {
    backgroundColor: '#ccc',
  },
  signupButtonText: {
    color: '#fff',
    fontSize: 18,
    fontWeight: '600',
  },
});

export default SignupScreen;
