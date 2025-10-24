import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";
//import "./Footer.css";

const Footer = () => {
  return (
    <footer className="footer text-light py-4 mt-5">
      <div className="container">
        <div className="row align-items-center justify-content-between text-center text-md-start">
          
          {/* Columna esquerra */}
          <div className="col-12 col-md-4 mb-4 mb-md-0">
            <h5 className="fw-bold mb-2">Abasta</h5>
            <p className="mb-1">Simplifica la gestió de comandes i proveïdors.</p>
            <p className="mb-1">
              <a
                href="mailto:hola@abasta.cat"
                className="text-light text-decoration-none"
              >
                hola@abasta.cat
              </a>
            </p>
            <p className="mb-0">Barcelona, Catalunya</p>
          </div>

          {/* Columna central */}
          <div className="col-12 col-md-4 mb-4 mb-md-0 text-center">
            <div className="footer-logo mb-2">
              <span className="footer-logo-a">A</span>
            </div>
            <small className="d-block">
              © 2025 Abasta. Tots els drets reservats.
            </small>
            <small>
              <a
                href="#"
                className="text-light text-decoration-underline mx-1"
              >
                Política de privacitat
              </a>
              ·
              <a
                href="#"
                className="text-light text-decoration-underline mx-1"
              >
                Termes i condicions
              </a>
            </small>
          </div>

          {/* Columna dreta */}
          <div className="col-12 col-md-3 text-md-end">
            <ul className="list-unstyled mb-0">
              <li>
                <a href="#" className="text-light text-decoration-none">
                  Inici
                </a>
              </li>
              <li>
                <a href="#" className="text-light text-decoration-none">
                  Beneficis
                </a>
              </li>
              <li>
                <a href="#" className="text-light text-decoration-none">
                  Com funciona
                </a>
              </li>
              <li>
                <a href="#" className="text-light text-decoration-none">
                  Sectors
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;