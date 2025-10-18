import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createStackNavigator } from '@react-navigation/stack';
import { Ionicons } from '@expo/vector-icons';
import { RootStackParamList, MainTabParamList } from '../types';
import DashboardScreen from '../screens/main/DashboardScreen';
import HealthInfoScreen from '../screens/main/HealthInfoScreen';
import CommunityScreen from '../screens/main/CommunityScreen';
import ProfileScreen from '../screens/main/ProfileScreen';
import PermissionScreen from '../screens/main/PermissionScreen';

const Tab = createBottomTabNavigator<MainTabParamList>();
const Stack = createStackNavigator<RootStackParamList>();

const MainTabNavigator: React.FC = () => {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ focused, color, size }) => {
          let iconName: keyof typeof Ionicons.glyphMap;

          if (route.name === 'Dashboard') {
            iconName = focused ? 'home' : 'home-outline';
          } else if (route.name === 'HealthInfo') {
            iconName = focused ? 'heart' : 'heart-outline';
          } else if (route.name === 'Community') {
            iconName = focused ? 'people' : 'people-outline';
          } else if (route.name === 'Profile') {
            iconName = focused ? 'person' : 'person-outline';
          } else if (route.name === 'Permission') {
            iconName = focused ? 'shield-checkmark' : 'shield-checkmark-outline';
          } else {
            iconName = 'help-outline';
          }

          return <Ionicons name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: '#2196F3',
        tabBarInactiveTintColor: 'gray',
        headerShown: false,
      })}
    >
      <Tab.Screen 
        name="Dashboard" 
        component={DashboardScreen}
        options={{ tabBarLabel: '대시보드' }}
      />
      <Tab.Screen 
        name="HealthInfo" 
        component={HealthInfoScreen}
        options={{ tabBarLabel: '건강정보' }}
      />
      <Tab.Screen 
        name="Community" 
        component={CommunityScreen}
        options={{ tabBarLabel: '커뮤니티' }}
      />
      <Tab.Screen 
        name="Profile" 
        component={ProfileScreen}
        options={{ tabBarLabel: '프로필' }}
      />
      <Tab.Screen 
        name="Permission" 
        component={PermissionScreen}
        options={{ tabBarLabel: '권한' }}
      />
    </Tab.Navigator>
  );
};

const MainNavigator: React.FC = () => {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="Main" component={MainTabNavigator} />
    </Stack.Navigator>
  );
};

export default MainNavigator;
