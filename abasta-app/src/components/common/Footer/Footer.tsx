import './Footer.css';

const Footer: React.FC = () => {
  return (
    <footer className="footer-abasta">
      <div className="container">
        <div className="row align-items-start">
          <div
            className="col-6 col-md-4 d-flex flex-column justify-content-between text-start mb-2 mb-md-0"
            style={{ height: '150px' }}
          >
            <div>
              <h3 className="footer-title mb-2">Abasta</h3>
              <p className="footer-description mb-0 text-break">
                Simplifica la gestió
                <br />
                de comandes i proveïdors.
              </p>
            </div>

            <p className="footer-contact text-break mb-0">
              <a
                href="mailto:abasta.platform@gmail.com"
                className="text-decoration-none text-light"
              >
                abasta.platform@gmail.com
              </a>
              <br />
              Barcelona, Catalunya
            </p>
          </div>

          <div className="col-6 col-md-4 text-center mb-2 mb-md-0">
            <div className="footer-logo">LOGO</div>
            <p className="footer-copyright">
              © 2025 Abasta. Tots els drets reservats.
              <br />
              <a href="#">Política de privacitat</a>
              {' · '}
              <a href="#">Termes i condicions</a>
            </p>
          </div>

          <div className="col-6 col-md-4 text-start mb-2 mb-md-0">
            <ul className="footer-menu">
              <li>
                <a href="#">Inici</a>
              </li>
              <li>
                <a href="#">Beneficis</a>
              </li>
              <li>
                <a href="#">Com funciona</a>
              </li>
              <li>
                <a href="#">Sectors</a>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
