import './Pagination.scss';

interface PaginationProps {
  type: 'producte' | 'proveïdor' | 'comanda';
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  itemsPerPage: number;
  totalItems: number;
  onItemsPerPageChange?: (itemsPerPage: number) => void;
}

const Pagination = ({
  type,
  currentPage,
  totalPages,
  onPageChange,
  itemsPerPage,
  totalItems,
  onItemsPerPageChange,
}: PaginationProps) => {
  const getPageNumbers = () => {
    const pages: (number | string)[] = [];
    const showPages = 5;

    if (totalPages <= showPages) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      if (currentPage <= 3) {
        pages.push(1, 2, 3, 4, '...', totalPages);
      } else if (currentPage >= totalPages - 2) {
        pages.push(
          1,
          '...',
          totalPages - 3,
          totalPages - 2,
          totalPages - 1,
          totalPages
        );
      } else {
        pages.push(
          1,
          '...',
          currentPage - 1,
          currentPage,
          currentPage + 1,
          '...',
          totalPages
        );
      }
    }

    return pages;
  };

  const startItem = (currentPage - 1) * itemsPerPage + 1;
  const endItem = Math.min(currentPage * itemsPerPage, totalItems);

  const typeCases = {
    producte: ['producte', 'productes'],
    proveïdor: ['proveïdor', 'proveïdors'],
    comanda: ['comanda', 'comandes'],
  };

  return (
    <div className="pagination-wrapper">
      <div className="pagination-info">
        <span className="pagination-text">
          Mostrant {startItem}-{endItem} de {totalItems}{' '}
          {totalItems === 1 ? typeCases[type][0] : typeCases[type][1]}
        </span>

        {onItemsPerPageChange && (
          <select
            className="form-select form-select-sm pagination-select"
            value={itemsPerPage}
            onChange={e => onItemsPerPageChange(Number(e.target.value))}
            aria-label="Elements per pàgina"
          >
            <option value={10}>10</option>
            <option value={20}>20</option>
            <option value={50}>50</option>
            <option value={100}>100</option>
          </select>
        )}
      </div>

      <nav aria-label="Paginació">
        <ul className="pagination mb-0">
          <li className={`page-item ${currentPage === 1 ? 'disabled' : ''}`}>
            <button
              className="page-link"
              onClick={() => onPageChange(currentPage - 1)}
              disabled={currentPage === 1}
            >
              <i className="bi bi-chevron-left"></i> Anterior
            </button>
          </li>

          {getPageNumbers().map((page, index) => (
            <li
              key={index}
              className={`page-item ${page === currentPage ? 'active' : ''} ${
                page === '...' ? 'disabled' : ''
              }`}
            >
              {page === '...' ? (
                <span className="page-link">...</span>
              ) : (
                <button
                  className="page-link"
                  onClick={() => onPageChange(page as number)}
                >
                  {page}
                </button>
              )}
            </li>
          ))}

          <li
            className={`page-item ${
              currentPage === totalPages ? 'disabled' : ''
            }`}
          >
            <button
              className="page-link"
              onClick={() => onPageChange(currentPage + 1)}
              disabled={currentPage === totalPages}
            >
              Següent <i className="bi bi-chevron-right"></i>
            </button>
          </li>
        </ul>
      </nav>
    </div>
  );
};

export default Pagination;
