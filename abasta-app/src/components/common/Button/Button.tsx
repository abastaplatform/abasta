import './Button.css';
interface ButtonProps {
  title: string;
  type?: 'button' | 'submit' | 'reset';
  onClick?: () => void;
  disabled?: boolean;
  isLoading?: boolean;
  variant?: 'primary' | 'secondary';
}

const Button = ({
  title,
  type = 'button',
  disabled,
  isLoading,
  onClick,
  variant = 'primary',
}: ButtonProps) => {
  const variantClass =
    variant === 'primary' ? 'btn-primary-custom' : 'btn-secondary-custom';

  return (
    <button
      className={`btn cta-button ${variantClass}`}
      onClick={onClick}
      type={type}
      disabled={disabled || isLoading}
    >
      {isLoading ? '...' : title}
    </button>
  );
};
export default Button;
