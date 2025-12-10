import type { FC } from 'react';
import PageHeader from '../common/PageHeader/PageHeader';
import Alert from '../common/Alert/Alert';

import { useStatisticsFilters } from '../../hooks/useStatisticsFilters';
import { useStatistics } from '../../hooks/useStatistics';

import TimeFilter from './TimeFilter/TimeFilter';
import SummaryCards from './SummaryCards/SummaryCards';
import ProviderSpendTable from './ProviderSpendTable/ProviderSpendTable';
import TopProductsTable from './TopProductsTable/TopProductsTable';

import './StatisticsPage.scss';

const StatisticsPage: FC = () => {
  const {
    filters,
    appliedFilters,
    handleDateChange,
    handleQuickRangeChange,
    applyFilters,
  } = useStatisticsFilters();

  const { data, isLoading, error, setError } = useStatistics(appliedFilters);

  const breadcrumbItems = [{ label: 'Estadístiques', active: true }];

  return (
    <div className="statistics-page-container form-container">
      <div className="container-fluid py-4">
        <div className="statistics-page mx-auto">
          <PageHeader
            title="Estadístiques"
            breadcrumbItems={breadcrumbItems}
          />

          {error && (
            <Alert
              variant="danger"
              message={error}
              onClose={() => setError('')}
            />
          )}

          <div className="mb-4">
            <TimeFilter
              dateRange={filters.dateRange}
              quickRange={filters.quickRange}
              onDateChange={handleDateChange}
              onQuickRangeChange={handleQuickRangeChange}
              onApply={applyFilters}
            />
          </div>

          {isLoading && (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Carregant...</span>
              </div>
            </div>
          )}

          {!isLoading && data && (
            <>
              <SummaryCards
                dateRange={appliedFilters.dateRange}
                summary={data.summary}
              />

              <ProviderSpendTable data={data.providerSpend} />

              <TopProductsTable data={data.topProducts} />
            </>
          )}

          {!isLoading && !data && !error && (
            <div className="text-center text-muted py-5">
              No hi ha dades per mostrar.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default StatisticsPage;
