import { useState } from 'react';
import type { DashboardData } from '../types/dashboard.types';
import { dashboardService } from '../services/dasboardService';

export const useDashboard = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchDashboardData = async (): Promise<DashboardData | null> => {
    setIsLoading(true);
    setError(null);

    try {
      const data = await dashboardService.getDashboardData();
      return data;
    } catch (err: unknown) {
      let errorMessage = 'Error al carregar les dades del dashboard';

      if (err instanceof Error) {
        errorMessage = err.message;
      } else if (typeof err === 'object' && err !== null) {
        const errObj = err as { response?: { data?: { message?: string } } };
        errorMessage = errObj.response?.data?.message ?? errorMessage;
      }

      setError(errorMessage);
      return null;
    } finally {
      setIsLoading(false);
    }
  };

  return {
    isLoading,
    error,
    fetchDashboardData,
  };
};
