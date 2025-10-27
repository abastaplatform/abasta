import { HashLink } from 'react-router-hash-link';
import './Footer.scss';
import { Link } from 'react-router-dom';

const Footer: React.FC = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="footer-abasta">
      <div className="container py-5">
        <div className="row g-4 mb-4">
          <div className="col-lg-3 col-md-6">
            <h5 className="footer-title mb-3">Abasta</h5>
            <p className="footer-description mb-0">
              Simplifica la gestió de comandes i proveïdors.
            </p>
          </div>

          <div className="col-lg-2 col-md-6 col-6">
            <h6 className="footer-subtitle mb-3">Empresa</h6>
            <ul className="footer-menu list-unstyled">
              <li>
                <HashLink smooth to="/#home">
                  Inici
                </HashLink>
              </li>
              <li>
                <HashLink smooth to="/#benefits">
                  Beneficis
                </HashLink>
              </li>
              <li>
                <HashLink smooth to="/#how-it-works">
                  Com funciona
                </HashLink>
              </li>
              <li>
                <HashLink smooth to="/#features">
                  Sectors
                </HashLink>
              </li>
            </ul>
          </div>

          <div className="col-lg-2 col-md-6 col-6">
            <h6 className="footer-subtitle mb-3">Legal</h6>
            <ul className="footer-menu list-unstyled">
              <li>
                <Link to="/privacy">Política de privacitat</Link>
              </li>
              <li>
                <Link to="/terms">Termes i condicions</Link>
              </li>
              <li>
                <Link to="/cookies">Cookies</Link>
              </li>
              <li>
                <Link to="/accessibility">Accessibilitat</Link>
              </li>
            </ul>
          </div>

          <div className="col-lg-5 col-md-6">
            <h6 className="footer-subtitle mb-3">Contacte</h6>
            <ul className="footer-contact list-unstyled">
              <li>
                <a href="mailto:abasta.platform@gmail.com">
                  abasta.platform@gmail.com
                </a>
              </li>
              <li>Barcelona, Catalunya</li>
            </ul>
          </div>
        </div>

        <hr className="footer-divider my-4" />

        <div className="row align-items-center">
          <div className="col-md-8 text-center text-md-start mb-3 mb-md-0">
            <p className="footer-copyright mb-0">
              © {currentYear} Abasta. Tots els drets reservats.
            </p>
          </div>
          <div className="col-md-4 text-center text-md-end">
            <div className="footer-social">
              <a
                href="https://linkedin.com"
                target="_blank"
                rel="noopener noreferrer"
                aria-label="LinkedIn"
              >
                <i className="bi bi-linkedin"></i>
              </a>
              <a
                href="https://twitter.com"
                target="_blank"
                rel="noopener noreferrer"
                aria-label="Twitter/X"
              >
                <i className="bi bi-twitter-x"></i>
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
