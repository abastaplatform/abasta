import { Link } from 'react-router-dom';
import Button from '../../../../components/common/Button/Button';
import HowItWorksCard from '../../../../components/common/HowItWorksCard/HowItWorksCard';
import './HowItWorksSection.scss';

const HowItWorksSection = () => {
  return (
    <section className="how-it-works-section">
      <div className="container">
        <h2 className="text-primary text-center mb-5">Cóm funciona?</h2>
        <div className="row images-container">
          <div className="col-12">
            <img src="/images/how-it-works-1.png" className="hiw-bg" />
          </div>
          <div className="col-4 text-center mb-4">
            <HowItWorksCard
              title="Dona d’alta la teva empresa"
              description="Crea el teu compte i configura les dades bàsiques."
            />
          </div>
          <div className="col-4 text-center mb-4">
            <HowItWorksCard
              title="Introdueix productes i proveïdors"
              description="Afegeix els teus articles i la xarxa de proveïdors."
            />
          </div>
          <div className="col-4 text-center mb-4">
            <HowItWorksCard
              title="Crea comandes i envia-les "
              description="Genera comandes en un clic i envia-les directament."
            />
          </div>
          <div className="col-12 text-center my-5">
            <Link to="/register">
              <Button title="Comença avui" />
            </Link>
          </div>
        </div>
      </div>
    </section>
  );
};

export default HowItWorksSection;
