import './Button.scss';

interface ButtonProps {
  title: string;
  type?: 'button' | 'submit' | 'reset';
  variant?: 'solid' | 'outline';
  size?: 'sm' | 'md' | 'lg';
  block?: boolean;
  onClick?: () => void;
  disabled?: boolean;
  isLoading?: boolean;
  className?: string;
}

const Button = ({
  title,
  type = 'button',
  variant = 'solid',
  size = 'md',
  block = false,
  disabled,
  isLoading,
  className,
  onClick,
}: ButtonProps) => {
  const buttonClasses = [
    'btn',
    'cta-button',
    variant === 'outline' && 'cta-button-outline',
    size === 'sm' && 'cta-button-sm',
    size === 'lg' && 'cta-button-lg',
    block && 'cta-button-block',
    className,
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <button
      className={buttonClasses}
      onClick={onClick}
      type={type}
      disabled={disabled || isLoading}
    >
      {isLoading ? (
        <>
          <span
            className="spinner-border spinner-border-sm me-2"
            role="status"
            aria-hidden="true"
          ></span>
          {title}
        </>
      ) : (
        title
      )}
    </button>
  );
};

export default Button;
