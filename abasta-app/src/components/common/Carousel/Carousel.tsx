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
    text: 'Perfecte per tenir controlats els proveïdors i els productes.',
    name: 'Pere',
    role: 'Bar Esport',
    img: 'https://i.pravatar.cc/100?img=33',
    color: '#fb923c',
  },
  {
    text: 'Perfecte per tenir controlats els proveïdors i els productes.',
    name: 'Pere',
    role: 'Bar Esport',
    img: 'https://i.pravatar.cc/100?img=33',
    color: '#fb923c',
  },
  {
    text: 'Perfecte per tenir controlats els proveïdors i els productes.',
    name: 'Pere',
    role: 'Bar Esport',
    img: 'https://i.pravatar.cc/100?img=33',
    color: '#fb923c',
  },
  {
    text: 'Perfecte per tenir controlats els proveïdors i els productes.',
    name: 'Pere',
    role: 'Bar Esport',
    img: 'https://i.pravatar.cc/100?img=33',
    color: '#fb923c',
  },
  {
    text: 'Perfecte per tenir controlats els proveïdors i els productes.',
    name: 'Pere',
    role: 'Bar Esport',
    img: 'https://i.pravatar.cc/100?img=33',
    color: '#fb923c',
  },
  {
    text: 'Perfecte per tenir controlats els proveïdors i els productes.',
    name: 'Pere',
    role: 'Bar Esport',
    img: 'https://i.pravatar.cc/100?img=33',
    color: '#fb923c',
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