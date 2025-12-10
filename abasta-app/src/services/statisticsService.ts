import api from './api';
import type {
  ApiResponse,
  BackendReportGlobalResponse,
  DateRange,
  StatisticsData,
  ProviderSpend,
  ProductDemand,
} from '../types/statistics.types';

const buildQueryFromDateRange = (dateRange: DateRange): string => {
  const params = new URLSearchParams();
  if (dateRange.from) params.append('startDate', dateRange.from);
  if (dateRange.to) params.append('endDate', dateRange.to);
  return params.toString();
};

const mapBackendToStatisticsData = (
  backend: BackendReportGlobalResponse
): StatisticsData => {
  const summary = {
    totalOrders: backend.totalComandes,
    totalSpend: backend.despesaTotal,
    averagePerOrder: backend.comandaMitjana,
  };

  const providerSpend: ProviderSpend[] =
    backend.despesaProveidors?.map((p, index) => ({
      providerId: `${index}-${p.proveidor}`,
      providerName: p.proveidor,
      ordersCount: p.numComandes,
      amount: p.despesaTotal,
      percentage: p.percentatge,
    })) ?? [];

  const topProducts: ProductDemand[] =
    backend.topProductes?.map((p, index) => ({
      productId: `${index}-${p.nomProducte}`,
      productName: p.nomProducte,
      quantity: p.quantitatTotal,
      totalAmount: p.despesaTotal,
    })) ?? [];

  return {
    period: {
      start: backend.dataInicial,
      end: backend.dataFinal,
    },
    summary,
    providerSpend,
    topProducts,
  };
};

export const statisticsService = {
  getGlobalStatistics: async (
    dateRange: DateRange
  ): Promise<ApiResponse<StatisticsData>> => {
    const query = buildQueryFromDateRange(dateRange);
    const response = await api.get<ApiResponse<BackendReportGlobalResponse>>(
      `/reports/global?${query}`
    );

    if (response.success && response.data) {
      const mapped = mapBackendToStatisticsData(response.data);
      return {
        ...response,
        data: mapped,
      } as ApiResponse<StatisticsData>;
    }

    return response as unknown as ApiResponse<StatisticsData>;
  },

  exportStatisticsPdf: async (dateRange: DateRange): Promise<Blob> => {
    const query = buildQueryFromDateRange(dateRange);
    const blob = await api.getBlob(`/reports/global/pdf?${query}`);
    return blob;
  },
};
