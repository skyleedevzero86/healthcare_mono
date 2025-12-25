import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Switch,
  TextInput,
  Alert,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import { Settings } from '../../types/settings';

const SettingsScreen: React.FC = () => {
  const [settings, setSettings] = useState<Settings>({
    notifications: {
      healthReminder: true,
      checkupNotification: true,
      exerciseReminder: true,
      mealReminder: true,
    },
    healthGoals: {
      dailySteps: 10000,
      weeklyExercise: 3,
      dailyCalories: 2000,
    },
    app: {
      darkMode: false,
      autoLogin: true,
      biometric: false,
      language: 'ko',
    },
  });

  const updateNotificationSetting = (key: keyof Settings['notifications'], value: boolean) => {
    setSettings({
      ...settings,
      notifications: {
        ...settings.notifications,
        [key]: value,
      },
    });
  };

  const updateHealthGoal = (key: keyof Settings['healthGoals'], value: number) => {
    setSettings({
      ...settings,
      healthGoals: {
        ...settings.healthGoals,
        [key]: value,
      },
    });
  };

  const updateAppSetting = (key: keyof Settings['app'], value: boolean | string) => {
    setSettings({
      ...settings,
      app: {
        ...settings.app,
        [key]: value,
      },
    });
  };

  const handleExportData = () => {
    Alert.alert('데이터 내보내기', '건강 데이터를 파일로 저장하시겠습니까?');
  };

  const handleImportData = () => {
    Alert.alert('데이터 가져오기', '저장된 데이터를 불러오시겠습니까?');
  };

  const handleClearCache = () => {
    Alert.alert('캐시 삭제', '임시 데이터를 삭제하시겠습니까?', [
      { text: '취소', style: 'cancel' },
      { text: '삭제', onPress: () => Alert.alert('완료', '캐시가 삭제되었습니다.') },
    ]);
  };

  const handleCheckUpdate = () => {
    Alert.alert('업데이트 확인', '최신 버전입니다. (v1.0.0)');
  };

  const handleShowAbout = () => {
    Alert.alert(
      'Healthcare Mono',
      '건강 관리 통합 플랫폼\n\n버전: 1.0.0\n개발: Healthcare Team\n\n건강한 삶을 위한 모든 것을 한 곳에서 관리하세요.'
    );
  };

  const handleShowPrivacyPolicy = () => {
    Alert.alert(
      '개인정보 처리방침',
      '본 앱은 사용자의 건강 데이터를 안전하게 보호합니다.\n\n- 수집하는 정보: 건강 데이터, 검진 결과, 식단 및 운동 기록\n- 사용 목적: 건강 관리 및 맞춤형 서비스 제공\n- 보관 기간: 회원 탈퇴 시까지\n- 제3자 제공: 없음'
    );
  };

  const handleShowTermsOfService = () => {
    Alert.alert(
      '이용약관',
      '1. 서비스 이용\n- 본 서비스는 건강 관리 목적으로 제공됩니다.\n- 의료 진단을 대체하지 않습니다.\n\n2. 사용자 의무\n- 정확한 건강 정보를 입력해야 합니다.\n- 타인의 정보를 무단으로 사용할 수 없습니다.\n\n3. 책임 제한\n- 본 서비스는 참고용이며, 의료 상담은 전문의에게 받으시기 바랍니다.'
    );
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.title}>설정</Text>
        </View>

        <View style={styles.section}>
          <View style={styles.sectionHeader}>
            <Ionicons name="notifications" size={20} color="#2196F3" />
            <Text style={styles.sectionTitle}>알림 설정</Text>
          </View>
          <View style={styles.settingItem}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>건강 데이터 입력 알림</Text>
              <Text style={styles.settingDescription}>매일 건강 데이터를 입력하도록 알림</Text>
            </View>
            <Switch
              value={settings.notifications.healthReminder}
              onValueChange={(value) => updateNotificationSetting('healthReminder', value)}
            />
          </View>
          <View style={styles.settingItem}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>검진 결과 알림</Text>
              <Text style={styles.settingDescription}>검진 결과 업로드 시 알림</Text>
            </View>
            <Switch
              value={settings.notifications.checkupNotification}
              onValueChange={(value) => updateNotificationSetting('checkupNotification', value)}
            />
          </View>
          <View style={styles.settingItem}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>운동 알림</Text>
              <Text style={styles.settingDescription}>운동 시간 알림</Text>
            </View>
            <Switch
              value={settings.notifications.exerciseReminder}
              onValueChange={(value) => updateNotificationSetting('exerciseReminder', value)}
            />
          </View>
          <View style={styles.settingItem}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>식단 기록 알림</Text>
              <Text style={styles.settingDescription}>식사 시간 식단 기록 알림</Text>
            </View>
            <Switch
              value={settings.notifications.mealReminder}
              onValueChange={(value) => updateNotificationSetting('mealReminder', value)}
            />
          </View>
        </View>

        <View style={styles.section}>
          <View style={styles.sectionHeader}>
            <Ionicons name="trophy" size={20} color="#2196F3" />
            <Text style={styles.sectionTitle}>건강 목표 설정</Text>
          </View>
          <View style={styles.settingItem}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>일일 걸음 수 목표</Text>
              <Text style={styles.settingDescription}>하루 목표 걸음 수</Text>
            </View>
            <TextInput
              style={styles.settingInput}
              value={settings.healthGoals.dailySteps.toString()}
              onChangeText={(text) => updateHealthGoal('dailySteps', parseInt(text) || 0)}
              keyboardType="numeric"
            />
          </View>
          <View style={styles.settingItem}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>주간 운동 목표</Text>
              <Text style={styles.settingDescription}>주당 운동 횟수</Text>
            </View>
            <TextInput
              style={styles.settingInput}
              value={settings.healthGoals.weeklyExercise.toString()}
              onChangeText={(text) => updateHealthGoal('weeklyExercise', parseInt(text) || 0)}
              keyboardType="numeric"
            />
          </View>
          <View style={styles.settingItem}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>일일 칼로리 목표</Text>
              <Text style={styles.settingDescription}>하루 목표 칼로리</Text>
            </View>
            <TextInput
              style={styles.settingInput}
              value={settings.healthGoals.dailyCalories.toString()}
              onChangeText={(text) => updateHealthGoal('dailyCalories', parseInt(text) || 0)}
              keyboardType="numeric"
            />
          </View>
        </View>

        <View style={styles.section}>
          <View style={styles.sectionHeader}>
            <Ionicons name="settings" size={20} color="#2196F3" />
            <Text style={styles.sectionTitle}>앱 설정</Text>
          </View>
          <View style={styles.settingItem}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>다크 모드</Text>
              <Text style={styles.settingDescription}>어두운 테마 사용</Text>
            </View>
            <Switch
              value={settings.app.darkMode}
              onValueChange={(value) => updateAppSetting('darkMode', value)}
            />
          </View>
          <View style={styles.settingItem}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>자동 로그인</Text>
              <Text style={styles.settingDescription}>앱 실행 시 자동 로그인</Text>
            </View>
            <Switch
              value={settings.app.autoLogin}
              onValueChange={(value) => updateAppSetting('autoLogin', value)}
            />
          </View>
          <View style={styles.settingItem}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>생체 인증</Text>
              <Text style={styles.settingDescription}>지문 또는 Face ID 사용</Text>
            </View>
            <Switch
              value={settings.app.biometric}
              onValueChange={(value) => updateAppSetting('biometric', value)}
            />
          </View>
          <TouchableOpacity style={styles.settingItem} onPress={handleExportData}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>데이터 내보내기</Text>
              <Text style={styles.settingDescription}>건강 데이터를 파일로 저장</Text>
            </View>
            <Ionicons name="chevron-forward" size={20} color="#999" />
          </TouchableOpacity>
          <TouchableOpacity style={styles.settingItem} onPress={handleImportData}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>데이터 가져오기</Text>
              <Text style={styles.settingDescription}>저장된 데이터 불러오기</Text>
            </View>
            <Ionicons name="chevron-forward" size={20} color="#999" />
          </TouchableOpacity>
          <TouchableOpacity style={styles.settingItem} onPress={handleClearCache}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>캐시 삭제</Text>
              <Text style={styles.settingDescription}>임시 데이터 삭제</Text>
            </View>
            <Ionicons name="chevron-forward" size={20} color="#999" />
          </TouchableOpacity>
        </View>

        <View style={styles.section}>
          <View style={styles.sectionHeader}>
            <Ionicons name="information-circle" size={20} color="#2196F3" />
            <Text style={styles.sectionTitle}>정보</Text>
          </View>
          <View style={styles.settingItem}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>앱 버전</Text>
              <Text style={styles.settingDescription}>현재 버전 정보</Text>
            </View>
            <Text style={styles.settingValue}>1.0.0</Text>
          </View>
          <TouchableOpacity style={styles.settingItem} onPress={handleCheckUpdate}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>업데이트 확인</Text>
              <Text style={styles.settingDescription}>최신 버전 확인</Text>
            </View>
            <Ionicons name="chevron-forward" size={20} color="#999" />
          </TouchableOpacity>
          <TouchableOpacity style={styles.settingItem} onPress={handleShowAbout}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>앱 정보</Text>
              <Text style={styles.settingDescription}>앱에 대한 정보</Text>
            </View>
            <Ionicons name="chevron-forward" size={20} color="#999" />
          </TouchableOpacity>
          <TouchableOpacity style={styles.settingItem} onPress={handleShowPrivacyPolicy}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>개인정보 처리방침</Text>
              <Text style={styles.settingDescription}>개인정보 보호 정책 확인</Text>
            </View>
            <Ionicons name="chevron-forward" size={20} color="#999" />
          </TouchableOpacity>
          <TouchableOpacity style={styles.settingItem} onPress={handleShowTermsOfService}>
            <View style={styles.settingInfo}>
              <Text style={styles.settingLabel}>이용약관</Text>
              <Text style={styles.settingDescription}>서비스 이용약관 확인</Text>
            </View>
            <Ionicons name="chevron-forward" size={20} color="#999" />
          </TouchableOpacity>
        </View>
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
  },
  header: {
    padding: 20,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
  },
  section: {
    backgroundColor: '#fff',
    marginTop: 20,
    paddingVertical: 10,
  },
  sectionHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginLeft: 10,
  },
  settingItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  settingInfo: {
    flex: 1,
  },
  settingLabel: {
    fontSize: 16,
    color: '#333',
    marginBottom: 4,
  },
  settingDescription: {
    fontSize: 12,
    color: '#999',
  },
  settingInput: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 6,
    padding: 8,
    width: 80,
    textAlign: 'right',
  },
  settingValue: {
    fontSize: 16,
    color: '#666',
  },
});

export default SettingsScreen;

