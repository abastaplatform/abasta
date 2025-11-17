import type { Product } from '../../../../types/product.types';
import ActionDropdown from '../ActionDropDown/ActionDropDown';
import './ProductTable.scss';

interface ProductTableProps {
  products: Product[];
  onDelete: (productId: string, productName: string) => void;
}

const ProductTable: React.FC<ProductTableProps> = ({ products, onDelete }) => {
  if (products.length === 0) {
    return (
      <div className="product-table-empty">
        <i className="bi bi-inbox"></i>
        <p>No s'han trobat productes</p>
      </div>
    );
  }

  return (
    <div className="product-table-container d-none d-md-block">
      <table className="product-table">
        <thead>
          <tr>
            <th>Nom</th>
            <th>Proveïdor</th>
            <th>Categoria</th>
            <th>Preu</th>
            <th>Unitat</th>
            <th className="text-end">Accions</th>
          </tr>
        </thead>

        <tbody>
          {products.map(product => (
            <tr key={product.uuid}>
              <td className="product-name">{product.name}</td>

              <td>{product.supplier?.name || '-'}</td>

              <td>{product.category || '-'}</td>

              <td>{product.price} €</td>

              <td>
                {product.volume != null ? `${product.volume} ` : ''}
                {product.unit || ''}
              </td>

              <td className="text-end">
                <ActionDropdown
                  itemUuid={product.uuid}
                  onDelete={() => onDelete(product.uuid, product.name)}
                />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ProductTable;
