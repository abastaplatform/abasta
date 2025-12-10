import type { FC } from 'react';
import type { ProviderSpend } from '../../../types/statistics.types';
import './ProviderSpendTable.scss';

interface ProviderSpendTableProps {
  data: ProviderSpend[];
}

const ProviderSpendTable: FC<ProviderSpendTableProps> = ({ data }) => {
  return (
    <section className="provider-spend mt-4 mb-5">
      <h3 className="provider-spend__title mb-1">Despesa per proveïdor</h3>
      <p className="text-muted small mb-3">
        Distribució de la despesa entre els teus proveïdors
      </p>

      <div className="card provider-spend__card">
        <div className="card-body">
          <div className="table-responsive">
            <table className="table provider-spend__table mb-0">
              <thead>
                <tr>
                  <th className="provider-spend__th">Proveïdor</th>
                  <th className="provider-spend__th text-end">Comandes</th>
                  <th className="provider-spend__th text-end">Despesa</th>
                  <th className="provider-spend__th text-end">% Total</th>
                </tr>
              </thead>
              <tbody>
                {data.map(provider => (
                  <tr key={provider.providerId}>
                    <td data-label="Proveïdor">{provider.providerName}</td>

                    <td className="text-end" data-label="Comandes">
                      {provider.ordersCount.toLocaleString('ca-ES')}
                    </td>

                    <td className="text-end" data-label="Despesa">
                      {provider.amount.toLocaleString('ca-ES', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2,
                      })}
                      €
                    </td>

                    <td className="provider-spend__percent-cell text-end">
                      <div className="provider-spend__percent-row">
                        <span className="provider-spend__percent-label">
                          % Total
                        </span>
                        <span className="provider-spend__percent-value">
                          {provider.percentage.toFixed(1)}%
                        </span>
                      </div>
                      <div className="provider-spend__bar-bg">
                        <div
                          className="provider-spend__bar-fill"
                          style={{ width: `${provider.percentage}%` }}
                        />
                      </div>
                    </td>
                  </tr>
                ))}

                {data.length === 0 && (
                  <tr>
                    <td colSpan={4} className="text-center text-muted py-4">
                      No hi ha dades per al període seleccionat.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>
  );
};

export default ProviderSpendTable;
