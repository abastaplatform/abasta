import api from './api';
import type {
  DashboardResponse,
  DashboardData,
} from '../types/dashboard.types';

export const dashboardService = {
  getDashboardData: async (): Promise<DashboardData> => {
    const response = await api.get<DashboardResponse>('/reports/dashboard');
    return response.data;
  },
};
