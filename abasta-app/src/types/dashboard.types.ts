export interface DashboardData {
  totalComandes: number;
  despesaComandes: number;
  comandesPendents: number;
}

export interface DashboardResponse {
  success: boolean;
  message: string;
  data: DashboardData;
  timestamp: string;
}
