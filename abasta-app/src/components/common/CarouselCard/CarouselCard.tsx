import { Card } from 'react-bootstrap';
import './CarouselCard.scss';

interface CarouselCardProps {
  text:string;
  name:string;
  role:string;
  img:string;
  color:string;
}
const CarouselCard = ({ text, name, role, img, color }: CarouselCardProps) => {
  return (
    <Card className="carousel-card shadow-sm border-0 mx-auto">
      <Card.Body className="p-4 text-start">
        <p className="carousel-card__text mb-4">“{text}”</p>

        <div className="d-flex align-items-center">
          <img
            src={img}
            alt={name}
            className="carousel-card__img me-3"
            style={{ borderColor: color }}
          />
          <div>
            <h2 className="carousel-card__name mb-0 fw-semibold text-primary">
              {name}
            </h2>
            <p className="carousel-card__role mb-0">{role}</p>
          </div>
        </div>
      </Card.Body>
    </Card>
  );
};

export default CarouselCard;