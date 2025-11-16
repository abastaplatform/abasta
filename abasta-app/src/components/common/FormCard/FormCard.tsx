import type { ReactNode } from 'react';

import './FormCard.scss';

interface FormCardProps {
  title: string;
  subtitle?: string;
  children: ReactNode;
  className?: string;
}

const FormCard = ({
  title,
  subtitle,
  children,
  className = '',
}: FormCardProps) => {
  return (
    <div className={`form-card card shadow-sm ${className}`}>
      <div className="card-body">
        <h2 className="form-card-title card-title mb-3 text-primary fw-bold">
          {title}
        </h2>
        {subtitle && (
          <p className="form-card-subtitle text-muted mb-4">{subtitle}</p>
        )}
        <div className="form-card-content">{children}</div>
      </div>
    </div>
  );
};

export default FormCard;
