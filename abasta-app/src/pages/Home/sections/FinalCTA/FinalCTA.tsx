import "./FinalCTA.css";
import Button from"../../../../components/common/Button/Button";

const FinalCTA: React.FC = () => {
  const handleClick= () => {
    console.log("TEST Button")
  }
  return (
    <section className="cta-abasta d-flex justify-content-center align-items-center text-center py-5">
      <div className="container">
        <h1 className="cta-title mb-3">Preparat per començar?</h1>
        <p className="cta-subtitle mb-4">
          Uneix-te a Abasta i transforma la gestió<br/>
          de la teva empresa avui mateix
        </p>
          <Button title="Registra la teva empresa"  onClick={handleClick}/>
      </div>
    </section>
  );
};

export default FinalCTA; 