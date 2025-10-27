import { Card } from 'react-bootstrap';
import './DoToCard.scss';

interface DoToCardProps {
  title: string;
  description: string;
  image: string;
}

const DoToCard = ({ title, description, image }: DoToCardProps) => {
  return (
    <Card className="do-to-card text-center">
      <div className="p-4 h-100 d-flex flex-column align-items-center justify-content-center">
        {image && (
          <div className="icon-wrapper mb-3">
            <img src={image} alt={title} className="do-to-card-image" />
          </div>
        )}
        <h5 className="title mb-2">{title}</h5>
        <p className="description mb-0">{description}</p>
      </div>
    </Card>
  );
};

export default DoToCard;