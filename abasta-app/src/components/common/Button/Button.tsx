import './Button.css';
interface ButtonProps {
  title: string;
  type?: 'button' | 'submit' | 'reset';
  onClick?: () => void;
  disabled?: boolean;
  isLoading?: boolean;
}

const Button = ({
  title,
  type = 'button',
  disabled,
  isLoading,
  onClick,
}: ButtonProps) => {
  return (
    <button
      className="btn cta-button"
      onClick={onClick}
      type={type}
      disabled={disabled || isLoading}
    >
      {title}
    </button>
  );
};
export default Button;
