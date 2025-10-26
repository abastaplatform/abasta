import './HeroSection.scss';

import Button from '../../../../components/common/Button/Button';
import { Link } from 'react-router-dom';

const HeroSection = () => {
  return (
    <section className="hero-section d-flex align-items-center">
      <img src="/images/hero-1.png" className="hero-bg" />
      <div className="container text-center">
        <div className="row g-4">
          <div className="col-12 col-md-5">
            <img
              src="/icons/logo-v.png"
              alt="Abasta vertical logo"
              className="logo"
            />
          </div>

          <div className="col-12 col-md-7 text-start">
            <h2 className="hero-title mb-4 text-light ">
              Gestiona comandes i proveïdors fàcilment
            </h2>
            <p className="hero-subtitle mb-4 text-light">
              Abasta centralitza la teva gestió i automatitza les comandes
              perquè guanyis temps i control al teu negoci
            </p>
            <Link to="/register">
              <Button title="Comença ara" />
            </Link>
          </div>
        </div>
      </div>
    </section>
  );
};

export default HeroSection;
