export type QuickRange = 'LAST_WEEK' | 'LAST_MONTH' | 'LAST_YEAR' | 'CUSTOM';

export interface DateRange {
  from: string; 
  to: string; 
}

export interface StatisticsFilters {
  dateRange: DateRange;
  quickRange: QuickRange;
}

export interface BackendDespesaPerProveidor {
  proveidor: string;
  numComandes: number;
  despesaTotal: number;  
  percentatge: number;   
}

export interface BackendProducteTop {
  nomProducte: string;
  quantitatTotal: number; 
  despesaTotal: number;  
}

export interface BackendReportGlobalResponse {
  dataInicial: string; 
  dataFinal: string;
  totalComandes: number;
  despesaTotal: number;
  comandaMitjana: number;
  despesaProveidors: BackendDespesaPerProveidor[];
  topProductes: BackendProducteTop[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}


export interface SummaryStats {
  totalOrders: number;
  totalSpend: number;
  averagePerOrder: number;
}

export interface ProviderSpend {
  providerId: string;  
  providerName: string;
  ordersCount: number;
  amount: number;
  percentage: number;   
}

export interface ProductDemand {
  productId: string;   
  productName: string;
  quantity: number;
  totalAmount: number;
}

export interface StatisticsData {
  period: {
    start: string; 
    end: string; 
  };
  summary: SummaryStats;
  providerSpend: ProviderSpend[];
  topProducts: ProductDemand[];
}
