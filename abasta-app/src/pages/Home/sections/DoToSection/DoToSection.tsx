import { Link } from 'react-router-dom';

import Button from '../../../../components/common/Button/Button';
import DoToCard from '../../../../components/common/DoToCard/DoToCard';

import './DoToSection.scss';
import bellesa from '../../../../assets/images/bellesa-icon.png';
import restaurant from '../../../../assets/images/restaurant-icon.png';
import taller from '../../../../assets/images/taller-icon.png';

const DoToSection = () => {
  return (
    <section className="do-to-section">
      <div className="container">
        <h2 className="text-primary text-center mb-5">
          Fet per a empreses <br />
          com la teva
        </h2>
        <div className="row justify-content-center">
          <div className="col-md-4 mb-4">
            <DoToCard
              title="Restaurant"
              description="Crea comandes setmanals de carn, peix i verdures"
              image={restaurant}
            />
          </div>
          <div className="col-md-4 mb-4">
            <DoToCard
              title="Taller mecànic"
              description="Gestiona les comandes de peces i subministraments fàcilment"
              image={taller}
            />
          </div>
          <div className="col-md-4 mb-4">
            <DoToCard
              title="Centre de bellesa"
              description="Organitza productes i envia comandes recurrents automàticament"
              image={bellesa}
            />
          </div>
        </div>
        <div className="text-center my-5">
          <Link to="/register">
            <Button title="Descobreix com funciona" />
          </Link>
        </div>
      </div>
    </section>
  );
};

export default DoToSection;
