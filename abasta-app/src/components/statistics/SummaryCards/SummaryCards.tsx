import type { FC } from 'react';
import type { DateRange, SummaryStats } from '../../../types/statistics.types';
import './SummaryCards.scss';

interface SummaryCardsProps {
  dateRange: DateRange;
  summary: SummaryStats;
}

const formatDateLabel = (iso: string) => {
  if (!iso) return '';
  const onlyDate = iso.split(' ')[0];
  const [y, m, d] = onlyDate.split('-');
  if (!y || !m || !d) return iso;
  return `${d}/${m}/${y}`;
};

const SummaryCards: FC<SummaryCardsProps> = ({ dateRange, summary }) => {
  const from = formatDateLabel(dateRange.from);
  const to = formatDateLabel(dateRange.to);

  return (
    <section className="summary-cards mt-4 mb-5">
      <p className="fw-semibold text-primary mb-3">
        Resum del període:&nbsp;
        <span className="summary-cards__period">
          {from} – {to}
        </span>
      </p>

      <div className="row g-3">

        <div className="col-md-4">
          <div className="summary-cards__card text-center">
            <p className="summary-cards__card-text">COMANDES</p>
            <p className="summary-cards__card-value">
              {summary.totalOrders.toLocaleString('ca-ES')}
            </p>
            <p className="summary-cards__card-text">Total comandes</p>
          </div>
        </div>

        <div className="col-md-4">
          <div className="summary-cards__card text-center">
            <p className="summary-cards__card-text">DESPESA</p>
            <p className="summary-cards__card-value">
              {summary.totalSpend.toLocaleString('ca-ES', {
                minimumFractionDigits: 0,
                maximumFractionDigits: 0,
              })}
              €
            </p>
            <p className="summary-cards__card-text">Despesa total</p>
          </div>
        </div>

        <div className="col-md-4">
          <div className="summary-cards__card text-center">
            <p className="summary-cards__card-text">MITJANA</p>
            <p className="summary-cards__card-value">
              {summary.averagePerOrder.toLocaleString('ca-ES', {
                minimumFractionDigits: 0,
                maximumFractionDigits: 0,
              })}
              €
            </p>
            <p className="summary-cards__card-text">Per comanda</p>
          </div>
        </div>
      </div>
    </section>
  );
};

export default SummaryCards;
