import './Carousel.css';

const carousel = [
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
];

const TestimonialsCarousel: React.FC = () => {
  return (
    <section className="testimonials-section py-5 text-center">
      <div className="container">
        <h2 className="testimonials-title mb-5">
          Històries reals,
          <br />
          resultats reals
        </h2>

        <div
          id="testimonialCarousel"
          className="carousel slide"
          data-bs-ride="carousel"
        >
          <div className="carousel-inner">
            {carousel.map((t, index) => (
              <div
                key={index}
                className={`carousel-item ${index === 0 ? 'active' : ''}`}
              >
                <div className="testimonial-card mx-auto shadow-sm">
                  <p className="testimonial-text">“{t.text}”</p>
                  <div className="testimonial-user d-flex align-items-center justify-content-center mt-4">
                    <img
                      src={t.img}
                      alt={t.name}
                      className="testimonial-img me-3"
                      style={{ borderColor: t.color }}
                    />
                    <div>
                      <h6 className="testimonial-name mb-0">{t.name}</h6>
                      <p className="testimonial-role mb-0">{t.role}</p>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Controles del carrusel */}
          <button
            className="carousel-control-prev"
            type="button"
            data-bs-target="#testimonialCarousel"
            data-bs-slide="prev"
          >
            <span className="carousel-control-prev-icon"></span>
          </button>
          <button
            className="carousel-control-next"
            type="button"
            data-bs-target="#testimonialCarousel"
            data-bs-slide="next"
          >
            <span className="carousel-control-next-icon"></span>
          </button>
        </div>
      </div>
    </section>
  );
};

export default TestimonialsCarousel;
