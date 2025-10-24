import "./Button.css"
interface ButtonProps {title:string, onClick:()=> void}

const Button = ({
    title, onClick
}: ButtonProps) =>{
    return(
        <button className="btn cta-button" onClick={onClick}>
            {title}
        </button>
    );
};
export default Button