const Cookies = () => {
  return (
    <section className="container py-5 text-dark" style={{ lineHeight: 1.7 }}>
      <h1 className="mb-4 text-primary fw-bold p-3 text-center">
        Política de Cookies d’Abasta
      </h1>

      <p><strong>Darrera actualització:</strong> 9 de novembre de 2025</p>

      <p>
        En aquesta Política de Cookies t’expliquem com <strong>Abasta, S.L.</strong> 
        (en endavant, “Abasta” o “nosaltres”) utilitza cookies i tecnologies similars al nostre lloc web.  
        En continuar navegant, acceptes l’ús de les cookies en els termes indicats a continuació.
      </p>

      <h2 className="mt-5 text-primary fw-semibold border-bottom border-2 pb-2">
        1. Què són les cookies?
      </h2>
      <p>
        Les <strong>cookies</strong> són petits fitxers de text que s’emmagatzemen al teu dispositiu 
        (ordinador, mòbil o tauleta) quan visites un lloc web. 
        Serveixen per recordar informació sobre la teva visita, com les preferències d’idioma, 
        les opcions seleccionades o la sessió iniciada.
      </p>

      <h2 className="mt-4 text-primary fw-semibold border-bottom border-2 pb-2">
        2. Tipus de cookies que utilitzem
      </h2>
      <ul className="list-group list-group-flush mb-4">
        <li className="list-group-item">
          <strong>Cookies tècniques o necessàries:</strong>  
          imprescindibles per al funcionament del lloc web i per permetre l’accés segur a les àrees restringides.
        </li>
        <li className="list-group-item">
          <strong>Cookies de preferències:</strong>  
          permeten recordar configuracions com l’idioma o la regió per millorar l’experiència d’usuari.
        </li>
        <li className="list-group-item">
          <strong>Cookies estadístiques (analítiques):</strong>  
          recullen informació sobre com els usuaris utilitzen el web per ajudar-nos a millorar-ne el contingut i el rendiment.
        </li>
        <li className="list-group-item">
          <strong>Cookies de màrqueting o publicitàries:</strong>  
          s’utilitzen per mostrar publicitat personalitzada en funció dels teus interessos i activitat de navegació.
        </li>
      </ul>

      <h2 className="text-primary fw-semibold border-bottom border-2 pb-2">
        3. Cookies pròpies i de tercers
      </h2>
      <p>
        Utilitzem cookies pròpies (gestionades directament per Abasta) i cookies de tercers,
        com ara proveïdors de serveis d’analítica o publicitat (p. ex., Google Analytics o Meta Pixel).  
        Aquests tercers poden utilitzar la informació per oferir serveis per compte d’Abasta o per als seus propis fins.
      </p>

      <h2 className="text-primary fw-semibold border-bottom border-2 pb-2">
        4. Durada de les cookies
      </h2>
      <p>
        Les cookies poden ser:
      </p>
      <ul>
        <li><strong>De sessió:</strong> s’eliminen automàticament quan tanques el navegador.</li>
        <li><strong>Persistents:</strong> romanen al dispositiu fins que caduquen o les elimines manualment.</li>
      </ul>

      <h2 className="text-primary fw-semibold border-bottom border-2 pb-2">
        5. Consentiment i configuració
      </h2>
      <p>
        Quan visites per primera vegada el nostre lloc web, es mostra un <strong>banner de consentiment</strong> 
        que t’informa sobre l’ús de cookies i et permet acceptar-les, rebutjar-les o configurar-les.
      </p>
      <p>
        Pots modificar el teu consentiment en qualsevol moment a través del nostre 
        <strong> Centre de preferències</strong> o mitjançant la configuració del teu navegador:
      </p>

      <ul className="list-group list-group-flush mb-4">
        <li className="list-group-item">
          <a href="https://support.google.com/chrome/answer/95647" target="_blank" rel="noreferrer" className="link-primary text-decoration-none">
            Google Chrome
          </a>
        </li>
        <li className="list-group-item">
          <a href="https://support.mozilla.org/es/kb/Deshabilitar%20cookies" target="_blank" rel="noreferrer" className="link-primary text-decoration-none">
            Mozilla Firefox
          </a>
        </li>
        <li className="list-group-item">
          <a href="https://support.apple.com/ca-es/guide/safari/sfri11471/mac" target="_blank" rel="noreferrer" className="link-primary text-decoration-none">
            Safari
          </a>
        </li>
        <li className="list-group-item">
          <a href="https://support.microsoft.com/es-es/help/17442/windows-internet-explorer-delete-manage-cookies" target="_blank" rel="noreferrer" className="link-primary text-decoration-none">
            Internet Explorer / Edge
          </a>
        </li>
      </ul>

      <h2 className="text-primary fw-semibold border-bottom border-2 pb-2">
        6. Com desactivar les cookies
      </h2>
      <p>
        Pots bloquejar o eliminar les cookies mitjançant la configuració del teu navegador.
        Tanmateix, algunes parts del lloc podrien no funcionar correctament si desactives totes les cookies.
      </p>

      <h2 className="text-primary fw-semibold border-bottom border-2 pb-2">
        7. Actualitzacions de la política
      </h2>
      <p>
        Abasta pot modificar aquesta Política per adaptar-la a noves exigències legals o tècniques.
        Et recomanem revisar-la periòdicament per mantenir-te informat.
      </p>

      <h2 className="text-primary fw-semibold border-bottom border-2 pb-2">
        8. Contacte
      </h2>
      <p>
        Si tens dubtes sobre l’ús de cookies, pots contactar amb nosaltres a 
        <a href="mailto:abasta.platform@gmail.com" className="ms-1 link-primary text-decoration-none fw-semibold">abasta.platform@gmail.com</a> 
        o per correu postal a Abasta, S.L. – 08000 Barcelona, Espanya.
      </p>

      <div className="alert alert-light mt-5 border-start border-4 border-primary shadow-sm">
        En continuar navegant pel nostre lloc web, acceptes l’ús de cookies segons aquesta Política.
      </div>
    </section>
  );
};

export default Cookies;