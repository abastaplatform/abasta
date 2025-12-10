import type { FC } from 'react';
import type { DateRange, QuickRange } from '../../../types/statistics.types';
import { statisticsService } from '../../../services/statisticsService';
import Button from '../../common/Button/Button';
import './TimeFilter.scss';

interface TimeFilterProps {
  dateRange: DateRange;
  quickRange: QuickRange;
  onDateChange: (field: keyof DateRange, value: string) => void;
  onQuickRangeChange: (quickRange: QuickRange) => void;
  onApply: () => void;
}

const TimeFilter: FC<TimeFilterProps> = ({
  dateRange,
  quickRange,
  onDateChange,
  onQuickRangeChange,
  onApply,
}) => {
  const handleExportPdf = async () => {
    try {
      const blob = await statisticsService.exportStatisticsPdf(dateRange);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `estadistiques_${dateRange.from}_${dateRange.to}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="time-filter shadow-sm p-4">

      <p className="text-muted mb-4">Analitza el rendiment de les teves comandes</p>

      <h5 className="fw-semibold text-primary mb-1">Filtre temporal</h5>
      <p className="text-muted small mb-3">Selecciona el període a analitzar</p>

      <div className="row g-3 mb-3">
        <div className="col-md-6">
          <label className="form-label fw-semibold text-primary">Data (des de)</label>
          <input
            type="date"
            className="form-control time-filter-input"
            value={dateRange.from}
            onChange={e => onDateChange('from', e.target.value)}
          />
        </div>

        <div className="col-md-6">
          <label className="form-label fw-semibold text-primary">Data (fins a)</label>
          <input
            type="date"
            className="form-control time-filter-input"
            value={dateRange.to}
            onChange={e => onDateChange('to', e.target.value)}
          />
        </div>
      </div>

      <p className="fw-semibold text-primary small mb-2">Períodes ràpids</p>

      <div className="d-flex gap-2 flex-wrap mb-4">
        <Button title="Última setmana" size="sm" variant="outline"
          className={quickRange === 'LAST_WEEK' ? 'active-filter' : ''}
          onClick={() => onQuickRangeChange('LAST_WEEK')}
        />
        <Button title="Últim mes" size="sm" variant="outline"
          className={quickRange === 'LAST_MONTH' ? 'active-filter' : ''}
          onClick={() => onQuickRangeChange('LAST_MONTH')}
        />
        <Button title="Últim any" size="sm" variant="outline"
          className={quickRange === 'LAST_YEAR' ? 'active-filter' : ''}
          onClick={() => onQuickRangeChange('LAST_YEAR')}
        />
      </div>

      <div className="time-filter-actions d-flex justify-content-end gap-2 flex-wrap">
        <Button title="Aplicar filtres" variant="solid" onClick={onApply}/>
        <Button title="Exportar PDF" variant="outline" onClick={handleExportPdf} disabled={!dateRange.from || !dateRange.to}/>
      </div>
    </div>
  );
};

export default TimeFilter;
