import React from 'react';
import { render } from '@testing-library/react-native';
import { HealthDataCard } from '../HealthDataCard';
import { HealthData } from '../../types/health';

const mockHealthData: HealthData = {
  userId: 'test',
  time: '2024-01-01T00:00:00Z',
  heartrate: 75,
  temperature: 36.5,
  spo2: 98,
  step: 5000,
  stress: 50,
  bloodpressMin: 80,
  bloodpressMax: 120,
  repiratory: 16,
  sleep: 7,
};

describe('HealthDataCard', () => {
  it('should render health data correctly', () => {
    const { getByText } = render(<HealthDataCard data={mockHealthData} />);

    expect(getByText('실시간 건강 데이터')).toBeTruthy();
    expect(getByText('75 bpm')).toBeTruthy();
    expect(getByText('36.5°C')).toBeTruthy();
    expect(getByText('98%')).toBeTruthy();
  });

  it('should call onChartPress when chart button is pressed', () => {
    const onChartPress = jest.fn();
    const { getByText } = render(<HealthDataCard data={mockHealthData} onChartPress={onChartPress} />);

    const chartButton = getByText('차트');
    chartButton.parent?.props.onPress();

    expect(onChartPress).toHaveBeenCalled();
  });
});

