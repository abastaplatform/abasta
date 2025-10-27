import { Container, Carousel } from 'react-bootstrap'; 
import CarouselCard from '../CarouselCard/CarouselCard';
import './Carousel.scss';

const testimonials = [
  {
    text: 'Abans fèiem les comandes per WhatsApp i fulls de càlcul. Amb Abasta, ho tenim tot en un sol lloc.',
    name: 'Anna',
    role: 'Restaurant Can Blau',
    img: 'https://i.pravatar.cc/100?img=5',
    color: '#ffb6c1',
  },
  {
    text: 'Hem reduït errors i estalviem molt de temps en cada comanda.',
    name: 'Bernat',
    role: 'Taller de la Vila',
    img: 'https://i.pravatar.cc/100?img=8',
    color: '#7dd3fc',
  },
  {
    text: 'Ideal per negocis petits: senzill, visual i molt pràctic.',
    name: 'Marta',
    role: 'Centre de Bellesa Llum',
    img: 'https://i.pravatar.cc/100?img=12',
    color: '#c084fc',
  },
  {
    text: 'La gestió de comandes mai havia estat tan fàcil i ràpida.',
    name: 'Jordi',
    role: 'Ferreteria Cal Jordi',
    img: 'https://i.pravatar.cc/100?img=15',
    color: '#fbbf24',
  },
  {
    text: 'Recomanable 100%. El nostre negoci ha millorat molt amb aquesta eina.',
    name: 'Carla',
    role: 'Floristeria Primavera',
    img: 'https://i.pravatar.cc/100?img=20',
    color: '#86efac',
  },
  {
    text: 'Perfecte per tenir controlats els proveïdors i els productes.',
    name: 'Pere',
    role: 'Bar Esport',
    img: 'https://i.pravatar.cc/100?img=33',
    color: '#fb923c',
  },
  {
    text: 'Amb Abasta, podem fer comandes en minuts i sense confusions. Ens ha facilitat molt el dia a dia.',
    name: 'Núria',
    role: 'Forn Sant Pau',
    img: 'https://i.pravatar.cc/100?img=24',
    color: '#a5b4fc',
  },
  {
    text: 'És molt intuïtiu i el suport tècnic és excel·lent. Ens sentim molt acompanyats.',
    name: 'Ramon',
    role: 'Carnisseria El Tall',
    img: 'https://i.pravatar.cc/100?img=28',
    color: '#fca5a5',
  },
  {
    text: 'Hem millorat la comunicació amb els proveïdors i reduït els malentesos.',
    name: 'Laura',
    role: 'Botiga La Plaça',
    img: 'https://i.pravatar.cc/100?img=35',
    color: '#f9a8d4',
  },
  {
    text: 'Les estadístiques de compres ens ajuden molt a controlar el pressupost mensual.',
    name: 'Xavier',
    role: 'Cafeteria Bon Matí',
    img: 'https://i.pravatar.cc/100?img=47',
    color: '#bef264',
  },
  {
    text: 'Ara tot l’equip pot veure les comandes i treballar de manera coordinada.',
    name: 'Ester',
    role: 'Llar de Cuina',
    img: 'https://i.pravatar.cc/100?img=51',
    color: '#67e8f9',
  },
  {
    text: 'Una eina imprescindible si vols estalviar temps i evitar errors en les comandes.',
    name: 'Marc',
    role: 'Pastisseria Dolç Temps',
    img: 'https://i.pravatar.cc/100?img=60',
    color: '#fcd34d',
  },
];

const chunkArray = (array: any[], size: number) => {
  const chunkedArr = [];
  for (let i = 0; i < array.length; i += size) {
    chunkedArr.push(array.slice(i, i + size));
  }
  return chunkedArr;
};

const CarouselTestimonials = () => {
  const slides = chunkArray(testimonials, 3);

  return (
    <section className="py-5 testimonials-section">
      <Container>
        <h2 className="text-primary fw-bold text-center mb-5">
          Històries reals,
          <br />
          resultats reals
        </h2>
        <Carousel 
          controls={false}      
          indicators={false}
          interval={4000} 
          fade={false}
          className="multi-card-carousel"
        >
          {slides.map((group, idx) => (
            <Carousel.Item key={idx}> 
              <div className="row justify-content-center gx-4 carousel-row-md-2">
                {group.map((t, i) => (
                  <div 
                    key={i} 
                    className="col-12 col-md-6 col-lg-4 d-flex mb-4"
                  >
                    <CarouselCard {...t} />
                  </div>
                ))}
              </div>
            </Carousel.Item>
          ))}
        </Carousel>
      </Container>
    </section>
  );
};

export default CarouselTestimonials;