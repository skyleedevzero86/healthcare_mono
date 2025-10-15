import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  TextInput,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../../store';
import { logout } from '../../store/slices/authSlice';
import { fetchUserInfo, updateUserInfo, updatePassword } from '../../store/slices/userSlice';

const ProfileScreen: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { user: authUser } = useSelector((state: RootState) => state.auth);
  const { user, loading } = useSelector((state: RootState) => state.user);

  const [showEditForm, setShowEditForm] = useState(false);
  const [showPasswordForm, setShowPasswordForm] = useState(false);
  const [editData, setEditData] = useState({
    userNm: '',
    email: '',
    telNumEnc: '',
    birthEnc: '',
    height: '',
    weight: '',
    bloodType: '',
  });
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  useEffect(() => {
    if (authUser?.userId) {
      dispatch(fetchUserInfo(authUser.userId));
    }
  }, [dispatch, authUser]);

  useEffect(() => {
    if (user) {
      setEditData({
        userNm: user.userNm || '',
        email: user.email || '',
        telNumEnc: user.telNumEnc || '',
        birthEnc: user.birthEnc || '',
        height: user.height?.toString() || '',
        weight: user.weight?.toString() || '',
        bloodType: user.bloodType || '',
      });
    }
  }, [user]);

  const handleLogout = () => {
    Alert.alert(
      '로그아웃',
      '정말 로그아웃하시겠습니까?',
      [
        { text: '취소', style: 'cancel' },
        { text: '로그아웃', onPress: () => dispatch(logout()) },
      ]
    );
  };

  const handleEditSubmit = () => {
    if (!user?.userId) return;

    const updateData = {
      ...editData,
      height: editData.height ? parseInt(editData.height) : undefined,
      weight: editData.weight ? parseInt(editData.weight) : undefined,
    };

    dispatch(updateUserInfo(updateData));
    setShowEditForm(false);
    Alert.alert('성공', '정보가 업데이트되었습니다.');
  };

  const handlePasswordSubmit = () => {
    if (!user?.userId) return;

    if (passwordData.newPassword !== passwordData.confirmPassword) {
      Alert.alert('오류', '새 비밀번호가 일치하지 않습니다.');
      return;
    }

    if (passwordData.newPassword.length < 6) {
      Alert.alert('오류', '비밀번호는 6자 이상이어야 합니다.');
      return;
    }

    dispatch(updatePassword({
      userId: user.userId,
      newPassword: passwordData.newPassword,
    }));
    setShowPasswordForm(false);
    setPasswordData({
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    });
    Alert.alert('성공', '비밀번호가 변경되었습니다.');
  };

  const formatDate = (dateString: string) => {
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString('ko-KR');
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.title}>프로필</Text>
        </View>

        <View style={styles.profileCard}>
          <View style={styles.profileInfo}>
            <View style={styles.avatar}>
              <Text style={styles.avatarText}>
                {user?.userNm?.charAt(0) || 'U'}
              </Text>
            </View>
            <View style={styles.userInfo}>
              <Text style={styles.userName}>{user?.userNm || '사용자'}</Text>
              <Text style={styles.userEmail}>{user?.email || ''}</Text>
              <Text style={styles.userRole}>{user?.userRoleFk || 'USER'}</Text>
            </View>
          </View>
        </View>

        <View style={styles.menuSection}>
          <Text style={styles.sectionTitle}>계정 정보</Text>
          
          <View style={styles.menuItem}>
            <Text style={styles.menuLabel}>아이디</Text>
            <Text style={styles.menuValue}>{user?.userId || ''}</Text>
          </View>
          
          <View style={styles.menuItem}>
            <Text style={styles.menuLabel}>이름</Text>
            <Text style={styles.menuValue}>{user?.userNm || ''}</Text>
          </View>
          
          <View style={styles.menuItem}>
            <Text style={styles.menuLabel}>이메일</Text>
            <Text style={styles.menuValue}>{user?.email || ''}</Text>
          </View>
          
          <View style={styles.menuItem}>
            <Text style={styles.menuLabel}>전화번호</Text>
            <Text style={styles.menuValue}>{user?.telNumEnc || ''}</Text>
          </View>
          
          <View style={styles.menuItem}>
            <Text style={styles.menuLabel}>생년월일</Text>
            <Text style={styles.menuValue}>{user?.birthEnc || ''}</Text>
          </View>
          
          <View style={styles.menuItem}>
            <Text style={styles.menuLabel}>성별</Text>
            <Text style={styles.menuValue}>{user?.gender || ''}</Text>
          </View>
          
          <View style={styles.menuItem}>
            <Text style={styles.menuLabel}>혈액형</Text>
            <Text style={styles.menuValue}>{user?.bloodType || ''}</Text>
          </View>
          
          <View style={styles.menuItem}>
            <Text style={styles.menuLabel}>키</Text>
            <Text style={styles.menuValue}>{user?.height ? `${user.height}cm` : ''}</Text>
          </View>
          
          <View style={styles.menuItem}>
            <Text style={styles.menuLabel}>몸무게</Text>
            <Text style={styles.menuValue}>{user?.weight ? `${user.weight}kg` : ''}</Text>
          </View>
        </View>

        <View style={styles.actionSection}>
          <TouchableOpacity
            style={styles.actionButton}
            onPress={() => setShowEditForm(!showEditForm)}
          >
            <Text style={styles.actionButtonText}>정보 수정</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={styles.actionButton}
            onPress={() => setShowPasswordForm(!showPasswordForm)}
          >
            <Text style={styles.actionButtonText}>비밀번호 변경</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={[styles.actionButton, styles.logoutButton]}
            onPress={handleLogout}
          >
            <Text style={[styles.actionButtonText, styles.logoutButtonText]}>로그아웃</Text>
          </TouchableOpacity>
        </View>

        {showEditForm && (
          <View style={styles.editForm}>
            <Text style={styles.formTitle}>정보 수정</Text>
            <View style={styles.inputGroup}>
              <Text style={styles.inputLabel}>이름</Text>
              <TextInput
                style={styles.input}
                value={editData.userNm}
                onChangeText={(text) => setEditData({ ...editData, userNm: text })}
              />
            </View>
            <View style={styles.inputGroup}>
              <Text style={styles.inputLabel}>이메일</Text>
              <TextInput
                style={styles.input}
                value={editData.email}
                onChangeText={(text) => setEditData({ ...editData, email: text })}
                keyboardType="email-address"
              />
            </View>
            <View style={styles.inputGroup}>
              <Text style={styles.inputLabel}>전화번호</Text>
              <TextInput
                style={styles.input}
                value={editData.telNumEnc}
                onChangeText={(text) => setEditData({ ...editData, telNumEnc: text })}
                keyboardType="phone-pad"
              />
            </View>
            <View style={styles.inputRow}>
              <View style={styles.inputGroup}>
                <Text style={styles.inputLabel}>키 (cm)</Text>
                <TextInput
                  style={styles.input}
                  value={editData.height}
                  onChangeText={(text) => setEditData({ ...editData, height: text })}
                  keyboardType="numeric"
                />
              </View>
              <View style={styles.inputGroup}>
                <Text style={styles.inputLabel}>몸무게 (kg)</Text>
                <TextInput
                  style={styles.input}
                  value={editData.weight}
                  onChangeText={(text) => setEditData({ ...editData, weight: text })}
                  keyboardType="numeric"
                />
              </View>
            </View>
            <View style={styles.buttonRow}>
              <TouchableOpacity
                style={[styles.formButton, styles.cancelButton]}
                onPress={() => setShowEditForm(false)}
              >
                <Text style={styles.cancelButtonText}>취소</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.formButton, styles.saveButton]}
                onPress={handleEditSubmit}
              >
                <Text style={styles.saveButtonText}>저장</Text>
              </TouchableOpacity>
            </View>
          </View>
        )}

        {showPasswordForm && (
          <View style={styles.editForm}>
            <Text style={styles.formTitle}>비밀번호 변경</Text>
            <View style={styles.inputGroup}>
              <Text style={styles.inputLabel}>현재 비밀번호</Text>
              <TextInput
                style={styles.input}
                value={passwordData.currentPassword}
                onChangeText={(text) => setPasswordData({ ...passwordData, currentPassword: text })}
                secureTextEntry
              />
            </View>
            <View style={styles.inputGroup}>
              <Text style={styles.inputLabel}>새 비밀번호</Text>
              <TextInput
                style={styles.input}
                value={passwordData.newPassword}
                onChangeText={(text) => setPasswordData({ ...passwordData, newPassword: text })}
                secureTextEntry
              />
            </View>
            <View style={styles.inputGroup}>
              <Text style={styles.inputLabel}>새 비밀번호 확인</Text>
              <TextInput
                style={styles.input}
                value={passwordData.confirmPassword}
                onChangeText={(text) => setPasswordData({ ...passwordData, confirmPassword: text })}
                secureTextEntry
              />
            </View>
            <View style={styles.buttonRow}>
              <TouchableOpacity
                style={[styles.formButton, styles.cancelButton]}
                onPress={() => setShowPasswordForm(false)}
              >
                <Text style={styles.cancelButtonText}>취소</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.formButton, styles.saveButton]}
                onPress={handlePasswordSubmit}
              >
                <Text style={styles.saveButtonText}>변경</Text>
              </TouchableOpacity>
            </View>
          </View>
        )}
      </ScrollView>
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
    padding: 20,
  },
  header: {
    marginBottom: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
  },
  profileCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  profileInfo: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  avatar: {
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: '#2196F3',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 15,
  },
  avatarText: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#fff',
  },
  userInfo: {
    flex: 1,
  },
  userName: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 4,
  },
  userEmail: {
    fontSize: 14,
    color: '#666',
    marginBottom: 2,
  },
  userRole: {
    fontSize: 12,
    color: '#999',
  },
  menuSection: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  menuItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  menuLabel: {
    fontSize: 16,
    color: '#333',
  },
  menuValue: {
    fontSize: 16,
    color: '#666',
  },
  actionSection: {
    marginBottom: 20,
  },
  actionButton: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 10,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  actionButtonText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#2196F3',
  },
  logoutButton: {
    backgroundColor: '#F44336',
  },
  logoutButtonText: {
    color: '#fff',
  },
  editForm: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  formTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  inputGroup: {
    marginBottom: 15,
  },
  inputLabel: {
    fontSize: 14,
    color: '#666',
    marginBottom: 5,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    backgroundColor: '#f9f9f9',
  },
  inputRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  inputGroup: {
    flex: 1,
    marginHorizontal: 5,
    marginBottom: 15,
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 10,
  },
  formButton: {
    flex: 1,
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
    marginHorizontal: 5,
  },
  cancelButton: {
    backgroundColor: '#f0f0f0',
  },
  saveButton: {
    backgroundColor: '#2196F3',
  },
  cancelButtonText: {
    fontSize: 16,
    color: '#666',
  },
  saveButtonText: {
    fontSize: 16,
    color: '#fff',
    fontWeight: '600',
  },
});

export default ProfileScreen;
