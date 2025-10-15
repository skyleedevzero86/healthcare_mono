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
import { fetchPosts, createPost, fetchPost } from '../../store/slices/communitySlice';
import { CommunityPost } from '../../types';

const CommunityScreen: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { user } = useSelector((state: RootState) => state.auth);
  const { posts, loading } = useSelector((state: RootState) => state.community);

  const [showWriteForm, setShowWriteForm] = useState(false);
  const [writeData, setWriteData] = useState({
    content: '',
    heartrate: '',
    temperature: '',
    bloodpress: '',
    smoking: '',
    drinking: '',
    exercise: '',
    age: '',
  });

  useEffect(() => {
    dispatch(fetchPosts());
  }, [dispatch]);

  const onRefresh = () => {
    dispatch(fetchPosts());
  };

  const handleWriteSubmit = () => {
    if (!user?.userId || !writeData.content.trim()) {
      Alert.alert('오류', '내용을 입력해주세요.');
      return;
    }

    const newPost: Omit<CommunityPost, 'commuSeq' | 'regDate'> = {
      content: writeData.content,
      heartrate: parseInt(writeData.heartrate) || 0,
      temperature: parseFloat(writeData.temperature) || 0,
      bloodpress: parseInt(writeData.bloodpress) || 0,
      smoking: parseInt(writeData.smoking) || 0,
      drinking: parseInt(writeData.drinking) || 0,
      exercise: parseInt(writeData.exercise) || 0,
      age: parseInt(writeData.age) || 0,
      userId: user.userId,
      userNm: user.userNm,
      bodyAge: 0,
    };

    dispatch(createPost(newPost));
    setShowWriteForm(false);
    setWriteData({
      content: '',
      heartrate: '',
      temperature: '',
      bloodpress: '',
      smoking: '',
      drinking: '',
      exercise: '',
      age: '',
    });
    Alert.alert('성공', '게시글이 작성되었습니다.');
  };

  const handlePostPress = (commuSeq: number) => {
    dispatch(fetchPost(commuSeq));
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
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
          <Text style={styles.title}>커뮤니티</Text>
          <TouchableOpacity
            style={styles.writeButton}
            onPress={() => setShowWriteForm(!showWriteForm)}
          >
            <Text style={styles.writeButtonText}>
              {showWriteForm ? '취소' : '글쓰기'}
            </Text>
          </TouchableOpacity>
        </View>

        {showWriteForm && (
          <View style={styles.writeForm}>
            <Text style={styles.formTitle}>새 게시글 작성</Text>
            <TextInput
              style={styles.contentInput}
              value={writeData.content}
              onChangeText={(text) => setWriteData({ ...writeData, content: text })}
              placeholder="건강 정보나 경험을 공유해보세요..."
              multiline
              numberOfLines={4}
            />
            
            <Text style={styles.healthDataTitle}>건강 데이터 (선택사항)</Text>
            <View style={styles.healthDataRow}>
              <View style={styles.healthDataItem}>
                <Text style={styles.healthDataLabel}>심박수</Text>
                <TextInput
                  style={styles.healthDataInput}
                  value={writeData.heartrate}
                  onChangeText={(text) => setWriteData({ ...writeData, heartrate: text })}
                  placeholder="70"
                  keyboardType="numeric"
                />
              </View>
              <View style={styles.healthDataItem}>
                <Text style={styles.healthDataLabel}>체온</Text>
                <TextInput
                  style={styles.healthDataInput}
                  value={writeData.temperature}
                  onChangeText={(text) => setWriteData({ ...writeData, temperature: text })}
                  placeholder="36.5"
                  keyboardType="numeric"
                />
              </View>
            </View>
            <View style={styles.healthDataRow}>
              <View style={styles.healthDataItem}>
                <Text style={styles.healthDataLabel}>혈압</Text>
                <TextInput
                  style={styles.healthDataInput}
                  value={writeData.bloodpress}
                  onChangeText={(text) => setWriteData({ ...writeData, bloodpress: text })}
                  placeholder="120"
                  keyboardType="numeric"
                />
              </View>
              <View style={styles.healthDataItem}>
                <Text style={styles.healthDataLabel}>나이</Text>
                <TextInput
                  style={styles.healthDataInput}
                  value={writeData.age}
                  onChangeText={(text) => setWriteData({ ...writeData, age: text })}
                  placeholder="30"
                  keyboardType="numeric"
                />
              </View>
            </View>
            
            <TouchableOpacity style={styles.submitButton} onPress={handleWriteSubmit}>
              <Text style={styles.submitButtonText}>게시하기</Text>
            </TouchableOpacity>
          </View>
        )}

        <View style={styles.postsList}>
          <Text style={styles.sectionTitle}>최근 게시글</Text>
          {posts.length === 0 ? (
            <View style={styles.emptyState}>
              <Text style={styles.emptyText}>게시글이 없습니다.</Text>
              <Text style={styles.emptySubtext}>첫 번째 게시글을 작성해보세요!</Text>
            </View>
          ) : (
            posts.map((post) => (
              <TouchableOpacity
                key={post.commuSeq}
                style={styles.postCard}
                onPress={() => handlePostPress(post.commuSeq)}
              >
                <View style={styles.postHeader}>
                  <Text style={styles.postAuthor}>{post.userNm}</Text>
                  <Text style={styles.postDate}>{formatDate(post.regDate)}</Text>
                </View>
                <Text style={styles.postContent} numberOfLines={3}>
                  {post.content}
                </Text>
                {(post.heartrate > 0 || post.temperature > 0 || post.bloodpress > 0) && (
                  <View style={styles.healthDataPreview}>
                    {post.heartrate > 0 && (
                      <Text style={styles.healthDataText}>심박수: {post.heartrate} bpm</Text>
                    )}
                    {post.temperature > 0 && (
                      <Text style={styles.healthDataText}>체온: {post.temperature}°C</Text>
                    )}
                    {post.bloodpress > 0 && (
                      <Text style={styles.healthDataText}>혈압: {post.bloodpress}</Text>
                    )}
                  </View>
                )}
              </TouchableOpacity>
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
  writeButton: {
    backgroundColor: '#2196F3',
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 8,
  },
  writeButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
  },
  writeForm: {
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
  contentInput: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    backgroundColor: '#f9f9f9',
    marginBottom: 15,
    textAlignVertical: 'top',
  },
  healthDataTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 10,
  },
  healthDataRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  healthDataItem: {
    flex: 1,
    marginHorizontal: 5,
  },
  healthDataLabel: {
    fontSize: 12,
    color: '#666',
    marginBottom: 5,
  },
  healthDataInput: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 6,
    padding: 8,
    fontSize: 14,
    backgroundColor: '#f9f9f9',
  },
  submitButton: {
    backgroundColor: '#2196F3',
    borderRadius: 8,
    padding: 15,
    alignItems: 'center',
    marginTop: 15,
  },
  submitButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  postsList: {
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
  postCard: {
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
  postHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 10,
  },
  postAuthor: {
    fontSize: 14,
    fontWeight: '600',
    color: '#2196F3',
  },
  postDate: {
    fontSize: 12,
    color: '#666',
  },
  postContent: {
    fontSize: 16,
    color: '#333',
    lineHeight: 22,
    marginBottom: 10,
  },
  healthDataPreview: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  healthDataText: {
    fontSize: 12,
    color: '#666',
    backgroundColor: '#f0f0f0',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 4,
    marginRight: 8,
    marginBottom: 4,
  },
});

export default CommunityScreen;
