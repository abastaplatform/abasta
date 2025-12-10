import { useState, useEffect, useCallback } from 'react';
import type { DateRange, QuickRange, StatisticsFilters } from '../types/statistics.types';

const formatDate = (date: Date): string => {
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, '0');
  const day = `${date.getDate()}`.padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const getInitialLastWeekRange = (): DateRange => {
  const today = new Date();
  const end = new Date(today);
  const start = new Date(today);
  start.setDate(start.getDate() - 7);
  return {
    from: formatDate(start),
    to: formatDate(end),
  };
};

const getQuickRangeDateRange = (quickRange: QuickRange): DateRange => {
  const today = new Date();
  const end = new Date(today);
  const start = new Date(today);

  switch (quickRange) {
    case 'LAST_WEEK':
      start.setDate(start.getDate() - 7);
      break;
    case 'LAST_MONTH':
      start.setMonth(start.getMonth() - 1);
      break;
    case 'LAST_YEAR':
      start.setFullYear(start.getFullYear() - 1);
      break;
    case 'CUSTOM':
    default:
      return getInitialLastWeekRange();
  }

  return {
    from: formatDate(start),
    to: formatDate(end),
  };
};

export const useStatisticsFilters = () => {
  const [filters, setFilters] = useState<StatisticsFilters>({
    dateRange: getInitialLastWeekRange(),
    quickRange: 'LAST_WEEK',
  });

  const [appliedFilters, setAppliedFilters] = useState<StatisticsFilters>(filters);

  useEffect(() => {
    if (filters.quickRange !== 'CUSTOM') {
      const newRange = getQuickRangeDateRange(filters.quickRange);
      setFilters(prev => ({
        ...prev,
        dateRange: newRange,
      }));
    }
  }, [filters.quickRange]);

  const handleDateChange = (field: keyof DateRange, value: string) => {
    setFilters(prev => ({
      ...prev,
      quickRange: 'CUSTOM',
      dateRange: {
        ...prev.dateRange,
        [field]: value,
      },
    }));
  };

  const handleQuickRangeChange = (quickRange: QuickRange) => {
    setFilters(prev => ({
      ...prev,
      quickRange,
    }));
  };

  const applyFilters = useCallback(() => {
    setAppliedFilters(filters);
  }, [filters]);

  return {
    filters,
    appliedFilters,
    handleDateChange,
    handleQuickRangeChange,
    applyFilters,
  };
};
