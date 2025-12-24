import React from 'react';
import { render } from '@testing-library/react-native';
import HealthCard from '../HealthCard';
import { HealthData } from '../../types';

describe('HealthCard', () => {
  const mockHealthData: HealthData = {
    heartrate: 72,
    temperature: 36.5,
    spo2: 98,
    step: 5000,
    bloodpressMax: 120,
    bloodpressMin: 80,
    sleep: 8,
  };

  it('should render health card with default title', () => {
    const { getByText } = render(<HealthCard data={mockHealthData} />);

    expect(getByText('건강 데이터')).toBeTruthy();
  });

  it('should render health card with custom title', () => {
    const { getByText } = render(<HealthCard data={mockHealthData} title="오늘의 건강" />);

    expect(getByText('오늘의 건강')).toBeTruthy();
  });

  it('should display all health metrics', () => {
    const { getByText } = render(<HealthCard data={mockHealthData} />);

    expect(getByText('심박수')).toBeTruthy();
    expect(getByText('72 bpm')).toBeTruthy();
    expect(getByText('체온')).toBeTruthy();
    expect(getByText('36.5°C')).toBeTruthy();
    expect(getByText('산소포화도')).toBeTruthy();
    expect(getByText('98%')).toBeTruthy();
    expect(getByText('걸음수')).toBeTruthy();
    expect(getByText('5,000')).toBeTruthy();
    expect(getByText('혈압')).toBeTruthy();
    expect(getByText('120/80')).toBeTruthy();
    expect(getByText('수면')).toBeTruthy();
    expect(getByText('8시간')).toBeTruthy();
  });

  it('should show normal status for normal heart rate', () => {
    const normalData: HealthData = { ...mockHealthData, heartrate: 75 };
    const { getByText } = render(<HealthCard data={normalData} />);

    expect(getByText('정상')).toBeTruthy();
  });

  it('should show high status for high heart rate', () => {
    const highData: HealthData = { ...mockHealthData, heartrate: 110 };
    const { getByText } = render(<HealthCard data={highData} />);

    expect(getByText('높음')).toBeTruthy();
  });

  it('should show low status for low heart rate', () => {
    const lowData: HealthData = { ...mockHealthData, heartrate: 55 };
    const { getByText } = render(<HealthCard data={lowData} />);

    expect(getByText('낮음')).toBeTruthy();
  });

  it('should show normal status for normal temperature', () => {
    const normalData: HealthData = { ...mockHealthData, temperature: 36.5 };
    const { getByText } = render(<HealthCard data={normalData} />);

    expect(getByText('정상')).toBeTruthy();
  });

  it('should show low status for low SpO2', () => {
    const lowData: HealthData = { ...mockHealthData, spo2: 92 };
    const { getByText } = render(<HealthCard data={lowData} />);

    expect(getByText('낮음')).toBeTruthy();
  });
});

