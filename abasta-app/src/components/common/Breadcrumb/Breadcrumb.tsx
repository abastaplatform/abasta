import { Link } from 'react-router-dom';
import './Breadcrumb.scss';

export interface BreadcrumbItem {
  label: string;
  path?: string;
  active?: boolean;
}

interface BreadcrumbProps {
  items: BreadcrumbItem[];
}

const Breadcrumb = ({ items }: BreadcrumbProps) => {
  return (
    <nav aria-label="breadcrumb" className="breadcrumb-nav mb-3">
      <ol className="breadcrumb">
        {items.map((item, index) => (
          <li
            key={index}
            className={`breadcrumb-item ${item.active || index === items.length - 1 ? 'active' : ''}`}
            aria-current={
              item.active || index === items.length - 1 ? 'page' : undefined
            }
          >
            {item.path && !item.active && index !== items.length - 1 ? (
              <Link to={item.path}>{item.label}</Link>
            ) : (
              <span>{item.label}</span>
            )}
          </li>
        ))}
      </ol>
    </nav>
  );
};

export default Breadcrumb;
