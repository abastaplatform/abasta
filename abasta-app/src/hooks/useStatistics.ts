import { useEffect, useState } from 'react';
import { statisticsService } from '../services/statisticsService';
import type {
  StatisticsData,
  StatisticsFilters,
} from '../types/statistics.types';

export const useStatistics = (filters: StatisticsFilters) => {
  const [data, setData] = useState<StatisticsData | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    const loadStatistics = async () => {
      setIsLoading(true);
      setError('');

      try {
        const response = await statisticsService.getGlobalStatistics(
          filters.dateRange
        );

        if (response.success && response.data) {
          setData(response.data);
        } else {
          setError(
            response.message || 'Error al carregar les estadístiques globals'
          );
        }
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : 'Error al carregar les estadístiques globals';
        setError(message);
        console.error('Load statistics error:', err);
      } finally {
        setIsLoading(false);
      }
    };

    loadStatistics();
  }, [filters]);

  return { data, isLoading, error, setError };
};
