import './Button.css';
interface ButtonProps {
  title: string;
  type?: 'button' | 'submit' | 'reset';
  onClick?: () => void;
}

const Button = ({ title, type = 'button', onClick }: ButtonProps) => {
  return (
    <button className="btn cta-button" onClick={onClick} type={type}>
      {title}
    </button>
  );
};
export default Button;
