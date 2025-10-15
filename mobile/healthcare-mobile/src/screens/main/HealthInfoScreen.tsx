import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  RefreshControl,
  TextInput,
  Alert,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../../store';
import { fetchHealthData, insertHealthData, fetchHealthChart } from '../../store/slices/healthSlice';
import { HealthData } from '../../types';

const HealthInfoScreen: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { user } = useSelector((state: RootState) => state.auth);
  const { healthData, loading } = useSelector((state: RootState) => state.health);

  const [showInputForm, setShowInputForm] = useState(false);
  const [inputData, setInputData] = useState({
    heartrate: '',
    temperature: '',
    spo2: '',
    step: '',
    stress: '',
    bloodpressMin: '',
    bloodpressMax: '',
    repiratory: '',
    sleep: '',
  });

  useEffect(() => {
    if (user?.userId) {
      dispatch(fetchHealthData(user.userId));
    }
  }, [dispatch, user]);

  const onRefresh = () => {
    if (user?.userId) {
      dispatch(fetchHealthData(user.userId));
    }
  };

  const handleInputSubmit = () => {
    if (!user?.userId) return;

    const newHealthData: HealthData = {
      userId: user.userId,
      time: new Date().toISOString(),
      heartrate: parseInt(inputData.heartrate) || 0,
      temperature: parseFloat(inputData.temperature) || 0,
      spo2: parseInt(inputData.spo2) || 0,
      step: parseInt(inputData.step) || 0,
      stress: parseInt(inputData.stress) || 0,
      bloodpressMin: parseInt(inputData.bloodpressMin) || 0,
      bloodpressMax: parseInt(inputData.bloodpressMax) || 0,
      repiratory: parseInt(inputData.repiratory) || 0,
      sleep: parseInt(inputData.sleep) || 0,
    };

    dispatch(insertHealthData(newHealthData));
    setShowInputForm(false);
    setInputData({
      heartrate: '',
      temperature: '',
      spo2: '',
      step: '',
      stress: '',
      bloodpressMin: '',
      bloodpressMax: '',
      repiratory: '',
      sleep: '',
    });
    Alert.alert('성공', '건강 데이터가 저장되었습니다.');
  };

  const getHealthStatus = (value: number, type: string) => {
    switch (type) {
      case 'heartrate':
        if (value < 60) return { status: '낮음', color: '#4CAF50' };
        if (value > 100) return { status: '높음', color: '#F44336' };
        return { status: '정상', color: '#2196F3' };
      case 'temperature':
        if (value < 36.1) return { status: '낮음', color: '#4CAF50' };
        if (value > 37.2) return { status: '높음', color: '#F44336' };
        return { status: '정상', color: '#2196F3' };
      case 'spo2':
        if (value < 95) return { status: '낮음', color: '#F44336' };
        return { status: '정상', color: '#2196F3' };
      default:
        return { status: '정상', color: '#2196F3' };
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView
        style={styles.container}
        refreshControl={
          <RefreshControl refreshing={loading} onRefresh={onRefresh} />
        }
      >
        <View style={styles.header}>
          <Text style={styles.title}>건강 정보</Text>
          <TouchableOpacity
            style={styles.addButton}
            onPress={() => setShowInputForm(!showInputForm)}
          >
            <Text style={styles.addButtonText}>
              {showInputForm ? '취소' : '데이터 입력'}
            </Text>
          </TouchableOpacity>
        </View>

        {showInputForm && (
          <View style={styles.inputForm}>
            <Text style={styles.formTitle}>건강 데이터 입력</Text>
            <View style={styles.inputRow}>
              <View style={styles.inputGroup}>
                <Text style={styles.inputLabel}>심박수 (bpm)</Text>
                <TextInput
                  style={styles.input}
                  value={inputData.heartrate}
                  onChangeText={(text) => setInputData({ ...inputData, heartrate: text })}
                  placeholder="70"
                  keyboardType="numeric"
                />
              </View>
              <View style={styles.inputGroup}>
                <Text style={styles.inputLabel}>체온 (°C)</Text>
                <TextInput
                  style={styles.input}
                  value={inputData.temperature}
                  onChangeText={(text) => setInputData({ ...inputData, temperature: text })}
                  placeholder="36.5"
                  keyboardType="numeric"
                />
              </View>
            </View>
            <View style={styles.inputRow}>
              <View style={styles.inputGroup}>
                <Text style={styles.inputLabel}>산소포화도 (%)</Text>
                <TextInput
                  style={styles.input}
                  value={inputData.spo2}
                  onChangeText={(text) => setInputData({ ...inputData, spo2: text })}
                  placeholder="98"
                  keyboardType="numeric"
                />
              </View>
              <View style={styles.inputGroup}>
                <Text style={styles.inputLabel}>걸음수</Text>
                <TextInput
                  style={styles.input}
                  value={inputData.step}
                  onChangeText={(text) => setInputData({ ...inputData, step: text })}
                  placeholder="8000"
                  keyboardType="numeric"
                />
              </View>
            </View>
            <View style={styles.inputRow}>
              <View style={styles.inputGroup}>
                <Text style={styles.inputLabel}>수축기 혈압</Text>
                <TextInput
                  style={styles.input}
                  value={inputData.bloodpressMax}
                  onChangeText={(text) => setInputData({ ...inputData, bloodpressMax: text })}
                  placeholder="120"
                  keyboardType="numeric"
                />
              </View>
              <View style={styles.inputGroup}>
                <Text style={styles.inputLabel}>이완기 혈압</Text>
                <TextInput
                  style={styles.input}
                  value={inputData.bloodpressMin}
                  onChangeText={(text) => setInputData({ ...inputData, bloodpressMin: text })}
                  placeholder="80"
                  keyboardType="numeric"
                />
              </View>
            </View>
            <TouchableOpacity style={styles.submitButton} onPress={handleInputSubmit}>
              <Text style={styles.submitButtonText}>저장</Text>
            </TouchableOpacity>
          </View>
        )}

        <View style={styles.dataList}>
          <Text style={styles.sectionTitle}>최근 건강 데이터</Text>
          {healthData.length === 0 ? (
            <View style={styles.emptyState}>
              <Text style={styles.emptyText}>건강 데이터가 없습니다.</Text>
              <Text style={styles.emptySubtext}>데이터를 입력해보세요!</Text>
            </View>
          ) : (
            healthData.slice(0, 10).map((data, index) => (
              <View key={index} style={styles.dataCard}>
                <View style={styles.dataHeader}>
                  <Text style={styles.dataTime}>
                    {new Date(data.time).toLocaleString()}
                  </Text>
                </View>
                <View style={styles.dataGrid}>
                  <View style={styles.dataItem}>
                    <Text style={styles.dataLabel}>심박수</Text>
                    <Text style={[styles.dataValue, { color: getHealthStatus(data.heartrate, 'heartrate').color }]}>
                      {data.heartrate} bpm
                    </Text>
                    <Text style={[styles.dataStatus, { color: getHealthStatus(data.heartrate, 'heartrate').color }]}>
                      {getHealthStatus(data.heartrate, 'heartrate').status}
                    </Text>
                  </View>
                  <View style={styles.dataItem}>
                    <Text style={styles.dataLabel}>체온</Text>
                    <Text style={[styles.dataValue, { color: getHealthStatus(data.temperature, 'temperature').color }]}>
                      {data.temperature}°C
                    </Text>
                    <Text style={[styles.dataStatus, { color: getHealthStatus(data.temperature, 'temperature').color }]}>
                      {getHealthStatus(data.temperature, 'temperature').status}
                    </Text>
                  </View>
                  <View style={styles.dataItem}>
                    <Text style={styles.dataLabel}>산소포화도</Text>
                    <Text style={[styles.dataValue, { color: getHealthStatus(data.spo2, 'spo2').color }]}>
                      {data.spo2}%
                    </Text>
                    <Text style={[styles.dataStatus, { color: getHealthStatus(data.spo2, 'spo2').color }]}>
                      {getHealthStatus(data.spo2, 'spo2').status}
                    </Text>
                  </View>
                  <View style={styles.dataItem}>
                    <Text style={styles.dataLabel}>걸음수</Text>
                    <Text style={styles.dataValue}>{data.step.toLocaleString()}</Text>
                  </View>
                </View>
              </View>
            ))
          )}
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
    padding: 20,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
  },
  addButton: {
    backgroundColor: '#2196F3',
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 8,
  },
  addButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
  },
  inputForm: {
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
  inputRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 15,
  },
  inputGroup: {
    flex: 1,
    marginHorizontal: 5,
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
  submitButton: {
    backgroundColor: '#2196F3',
    borderRadius: 8,
    padding: 15,
    alignItems: 'center',
    marginTop: 10,
  },
  submitButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  dataList: {
    marginBottom: 20,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  emptyState: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 40,
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
  emptyText: {
    fontSize: 16,
    color: '#666',
    marginBottom: 5,
  },
  emptySubtext: {
    fontSize: 14,
    color: '#999',
  },
  dataCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 15,
    marginBottom: 15,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  dataHeader: {
    marginBottom: 10,
  },
  dataTime: {
    fontSize: 14,
    color: '#666',
  },
  dataGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  dataItem: {
    width: '48%',
    marginBottom: 10,
  },
  dataLabel: {
    fontSize: 12,
    color: '#666',
    marginBottom: 2,
  },
  dataValue: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 2,
  },
  dataStatus: {
    fontSize: 12,
    fontWeight: '600',
  },
});

export default HealthInfoScreen;
