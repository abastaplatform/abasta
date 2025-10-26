import './BenefitCard.scss';

interface BenefitCardProps {
  icon: string;
  title: string;
  description: string;
}

const BenefitCard = ({ icon, title, description }: BenefitCardProps) => {
  return (
    <div className="benefit-card p-4 text-center h-100">
      <i className={`mb-3 icon bi ${icon}`} />
      <h5 className="title mb-2">{title}</h5>
      <p className="description mb-0">{description}</p>
    </div>
  );
};
export default BenefitCard;
