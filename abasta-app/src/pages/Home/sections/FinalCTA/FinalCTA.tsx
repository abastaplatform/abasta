import "./FinalCTA.css";
import { Link } from 'react-router-dom';
import Button from"../../../../components/common/Button/Button";

const FinalCTA: React.FC = () => {
  const handleClick= () => {
    console.log("TEST Button")
  }
  return (
    <section className="cta-abasta d-flex justify-content-center align-items-center text-center py-5">
      <div className="container">
        <h2 className="cta-title mb-3">Preparat per començar?</h2>
        <p className="cta-subtitle mb-4">
          Uneix-te a Abasta i transforma la gestió<br/>
          de la teva empresa avui mateix
        </p>
          <Link to="/register">
            <Button title="Registra la teva empresa" />
          </Link>
      </div>
    </section>
  );
};

export default FinalCTA; 