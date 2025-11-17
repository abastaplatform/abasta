import type { Product } from '../../../../types/product.types';

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
    <div className="product-table-container">
      <table className="product-table">
        <thead>
          <tr>
            <th>Nom</th>
            <th>Category</th>
            <th>Volum</th>
            <th>Preu</th>
            <th>Proveïdor</th>
            <th className="text-end">Accions</th>
          </tr>
        </thead>
        <tbody>
          {products.map(product => (
            <tr key={product.uuid}>
              <td className="product-name">{product.name}</td>
              <td>{product.category}</td>
              <td>
                {product.volume}
                {product.unit}
              </td>
              <td>{product.price}€</td>
              <td>{product.supplier.name}</td>
              <td className="text-end">
                <ActionDropdown
                  productUuid={product.uuid}
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
