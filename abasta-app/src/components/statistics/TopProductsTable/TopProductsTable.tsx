import type { FC } from 'react';
import type { ProductDemand } from '../../../types/statistics.types';
import './TopProductsTable.scss';

interface TopProductsTableProps {
  data: ProductDemand[];
}

const TopProductsTable: FC<TopProductsTableProps> = ({ data }) => {
  const sorted = [...data]
    .sort((a, b) => b.quantity - a.quantity)
    .slice(0, 10);

  return (
    <section className="top-products mt-5">
      <h3 className="top-products__title mb-1">
        Top 10 productes més demandats
      </h3>
      <p className="text-muted small mb-3">
        Els productes que més has demanat en aquest període
      </p>

      <div className="card top-products__card">
        <div className="card-body">
          <div className="table-responsive">
            <table className="table top-products__table mb-0">
              <thead>
                <tr>
                  <th className="top-products__th" style={{ width: 70 }}>
                    #
                  </th>
                  <th className="top-products__th">Producte</th>
                  <th className="top-products__th text-end">Quantitat</th>
                  <th className="top-products__th text-end">Despesa total</th>
                </tr>
              </thead>

              <tbody>
                {sorted.map((product, index) => (
                  <tr
                    key={
                      (product as any).productId ??
                      `${product.productName}-${index}`
                    }
                  >
                    <td className="top-products__cell top-products__cell-rank">
                      {index + 1}
                    </td>

                    <td
                      className="top-products__cell top-products__cell-name"
                      data-rank={index + 1}
                    >
                      {product.productName}
                    </td>

                    <td className="top-products__cell top-products__cell-qty text-end">
                      {product.quantity} u.
                    </td>

                    <td className="top-products__cell top-products__cell-amount text-end">
                      {product.totalAmount.toLocaleString('ca-ES', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2,
                      })}
                      €
                    </td>
                  </tr>
                ))}

                {sorted.length === 0 && (
                  <tr>
                    <td colSpan={4} className="text-center text-muted py-4">
                      No hi ha dades de productes per al període seleccionat.
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

export default TopProductsTable;
