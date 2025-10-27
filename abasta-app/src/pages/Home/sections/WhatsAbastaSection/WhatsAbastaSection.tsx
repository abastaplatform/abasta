import { Container } from 'react-bootstrap';
import './WhatsAbastaSection.scss';

const WhatsAbastaSection = () => {
  return (
    <section className="section-abasta text-center py-5">
      <Container>
        <h2 className="text-primary fw-bold mb-4">Què fa Abasta?</h2>
        <ul className="list-unstyled section-abasta__list mx-auto">
          <li>Et dona el control del teu negoci sense maldecaps.</li>
          <li>No és un ERP pesat ni un gestor limitat:</li>
          <li>És una eina feta per a negocis petits que volen créixer amb ordre.</li>
        </ul>
      </Container>
    </section>
  );
};

export default WhatsAbastaSection;