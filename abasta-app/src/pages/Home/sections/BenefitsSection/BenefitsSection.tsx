import { Link } from 'react-router-dom';
import BenefitCard from '../../../../components/common/BenefitCard/BenefitCard';
import Button from '../../../../components/common/Button/Button';

import './BenefitsSection.scss';
import benefits from '../../../../assets/images/benefits-1.png';

const BenefitsSection = () => {
  return (
    <section className="benefits-section d-flex align-items-center">
      <img
        src={benefits}
        className="benefits-bg"
        alt="Imatge secció beneficis"
      />

      <div className="container">
        <div className="row">
          <h2 className="text-primary text-center mb-5">Beneficis</h2>
          <div className="col-12 col-md-6 text-center mb-4">
            <BenefitCard
              icon="bi-clock"
              title="Estalvia temps"
              description="Automatitza enviaments i rep recomanacions intel·ligents"
            />
          </div>
          <div className="col-12 col-md-6 text-center mb-4">
            <BenefitCard
              icon="bi-graph-up"
              title="Pren millors decisions"
              description="Estadístiques i informes clars de despesa i comandes"
            />
          </div>
          <div className="col-12 col-md-6 text-center mb-4">
            <BenefitCard
              icon="bi-people"
              title="Multiusuari"
              description="Gestiona rols i tasques dins l’empresa fàcilment"
            />
          </div>
          <div className="col-12 col-md-6 text-center mb-4">
            <BenefitCard
              icon="bi-box-seam"
              title="Tot organitzat"
              description="Proveïdors, productes i comandes en un sol lloc"
            />
          </div>
          <div className="col-12 text-center my-5">
            <Link to="/register">
              <Button title="Registra la teva empresa" />
            </Link>
          </div>
        </div>
      </div>
    </section>
  );
};

export default BenefitsSection;
