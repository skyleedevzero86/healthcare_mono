import { useState, useEffect } from 'react';
import { HealthData } from '../types/health';
import { analyzeHealthData, HealthAnalysis } from '../services/healthAnalysisService';

interface UseHealthAnalysisResult {
  analysis: HealthAnalysis | null;
  loading: boolean;
  refresh: () => void;
}

export const useHealthAnalysis = (healthData: HealthData | null): UseHealthAnalysisResult => {
  const [analysis, setAnalysis] = useState<HealthAnalysis | null>(null);
  const [loading, setLoading] = useState(false);

  const performAnalysis = () => {
    if (!healthData) {
      setAnalysis(null);
      return;
    }

    setLoading(true);
    setTimeout(() => {
      const result = analyzeHealthData(healthData);
      setAnalysis(result);
      setLoading(false);
    }, 1500);
  };

  useEffect(() => {
    performAnalysis();
  }, [healthData]);

  return {
    analysis,
    loading,
    refresh: performAnalysis,
  };
};

