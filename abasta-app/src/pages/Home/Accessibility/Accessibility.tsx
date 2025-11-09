const Accessibility = () => {
  return (
    <section className="container py-5 text-dark" style={{ lineHeight: 1.7 }}>
      <h1 className="mb-4 text-primary fw-bold p-3 text-center">
        Declaració d’Accessibilitat d’Abasta
      </h1>

      <p><strong>Darrera actualització:</strong> 9 de novembre de 2025</p>

      <p>
        A <strong>Abasta, S.L.</strong> (en endavant “Abasta”), treballem per garantir que totes les persones, 
        independentment de les seves capacitats, puguin accedir, navegar i utilitzar amb facilitat el nostre lloc web 
        i els nostres serveis digitals. Aquesta declaració descriu les nostres polítiques, estàndards i mesures per 
        assegurar la màxima accessibilitat i inclusió.
      </p>

      <h2 className="mt-5 text-primary fw-semibold border-bottom border-2 pb-2">1. Compromís amb l’accessibilitat</h2>
      <p>
        El nostre objectiu és que el web d’Abasta sigui accessible per a tots els usuaris, incloses les persones amb 
        discapacitat visual, auditiva, motora o cognitiva. Per això, seguim els principis establerts a les 
        <strong> Directrius d’Accessibilitat per al Contingut Web (WCAG) 2.1</strong> del W3C, 
        amb un nivell de conformitat <strong>AA</strong>.
      </p>

      <h2 className="mt-4 text-primary fw-semibold border-bottom border-2 pb-2">2. Mesures adoptades</h2>
      <p>Per millorar l’accessibilitat del nostre lloc web, Abasta ha implementat les següents accions:</p>
      <ul>
        <li>Ús d’etiquetes semàntiques HTML i estructures coherents de títols (H1, H2, H3...).</li>
        <li>Colors i contrastos que garanteixen la llegibilitat segons els criteris WCAG.</li>
        <li>Compatibilitat amb lectors de pantalla i tecnologies assistives.</li>
        <li>Possibilitat de navegar amb teclat sense dependència del ratolí.</li>
        <li>Textos alternatius en imatges i icones.</li>
        <li>Enllaços descriptius i fàcilment identificables.</li>
        <li>Temps suficient per llegir i interactuar amb el contingut.</li>
        <li>Evitem l’ús de contingut que pugui provocar convulsions o distraccions excessives.</li>
      </ul>

      <h2 className="mt-4 text-primary fw-semibold border-bottom border-2 pb-2">3. Situació de conformitat</h2>
      <p>
        Actualment, el lloc web d’Abasta <strong>és parcialment conforme</strong> amb el nivell AA de les WCAG 2.1 
        a causa d’algunes excepcions descrites a continuació:
      </p>
      <ul>
        <li>Alguns continguts multimèdia antics poden no disposar de transcripció o subtítols.</li>
        <li>Determinats documents PDF o arxius adjunts poden no ser totalment accessibles.</li>
        <li>Alguns components externs o d’integració (p. ex., mapes o vídeos incrustats) poden no complir tots els requisits d’accessibilitat.</li>
      </ul>
      <p>
        Estem treballant activament per corregir aquests aspectes i assolir una conformitat completa.
      </p>

      <h2 className="mt-4 text-primary fw-semibold border-bottom border-2 pb-2">4. Millora contínua</h2>
      <p>
        A Abasta revisem regularment el nostre lloc web per detectar possibles barreres d’accessibilitat. 
        Les noves funcionalitats o seccions s’avaluen abans de ser publicades per garantir-ne la compatibilitat 
        amb els estàndards d’accessibilitat i les bones pràctiques internacionals.
      </p>

      <h2 className="mt-4 text-primary fw-semibold border-bottom border-2 pb-2">5. Assistència i contacte</h2>
      <p>
        Si trobes alguna dificultat per accedir a continguts o funcionalitats del web, o vols fer una suggerència 
        per millorar-ne l’accessibilitat, pots contactar amb nosaltres mitjançant els següents canals:
      </p>
      <ul>
        <li><strong>Correu electrònic:</strong> <a href="mailto:abasta.platform@gmail.com" className="link-primary text-decoration-none">abasta.platform@gmail.com</a></li>
        <li><strong>Adreça postal:</strong> Abasta, S.L. – Departament d’Accessibilitat, 08000 Barcelona, Espanya</li>
      </ul>
      <p>
        Ens comprometem a respondre totes les consultes o incidències en un termini màxim de <strong>15 dies hàbils</strong>.
      </p>

      <h2 className="mt-4 text-primary fw-semibold border-bottom border-2 pb-2">6. Preparació d’aquesta declaració</h2>
      <p>
        Aquesta declaració d’accessibilitat ha estat elaborada el <strong>9 de novembre de 2025</strong> 
        seguint l’<em>Annex 1 del Reial Decret 1112/2018</em>, sobre accessibilitat dels llocs web i aplicacions 
        per a dispositius mòbils del sector públic i empreses que ofereixen serveis essencials.
      </p>

      <h2 className="mt-4 text-primary fw-semibold border-bottom border-2 pb-2">7. Referències legals</h2>
      <ul>
        <li><strong>Normativa europea:</strong> Directiva (UE) 2016/2102 del Parlament Europeu i del Consell.</li>
        <li><strong>Normativa espanyola:</strong> Reial Decret 1112/2018, de 7 de setembre, sobre accessibilitat dels llocs web i aplicacions per a dispositius mòbils.</li>
        <li><strong>Estàndards tècnics:</strong> WCAG 2.1 del W3C – Nivell AA.</li>
      </ul>

      <div className="alert alert-light mt-5 border-start border-4 border-primary shadow-sm">
        Abasta reafirma el seu compromís amb la inclusió digital i la igualtat d’oportunitats per a totes les persones usuàries.
      </div>
    </section>
  );
};

export default Accessibility;
