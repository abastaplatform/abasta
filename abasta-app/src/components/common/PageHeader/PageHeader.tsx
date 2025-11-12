import type { ReactNode } from 'react';

import type { BreadcrumbItem } from '../Breadcrumb/Breadcrumb';
import Breadcrumb from '../Breadcrumb/Breadcrumb';

import './PageHeader.scss';

interface PageHeaderProps {
  title: string;
  breadcrumbItems?: BreadcrumbItem[];
  actions?: ReactNode;
}

const PageHeader = ({ title, breadcrumbItems, actions }: PageHeaderProps) => {
  return (
    <div className="page-header mb-4">
      {breadcrumbItems && breadcrumbItems.length > 0 && (
        <Breadcrumb items={breadcrumbItems} />
      )}

      <div className="d-flex justify-content-between align-items-center flex-wrap gap-3">
        <h2 className="page-title mb-0 fw-bold text-primary">{title}</h2>
        {actions && <div className="page-actions">{actions}</div>}
      </div>
    </div>
  );
};

export default PageHeader;
