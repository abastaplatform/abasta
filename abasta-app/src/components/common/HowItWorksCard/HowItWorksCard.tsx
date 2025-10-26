import './HowItWorksCard.scss';

interface HowItWorksProps {
  title: string;
  description: string;
}

const HowItWorks = ({ title, description }: HowItWorksProps) => {
  return (
    <div className="how-it-works-card p-4 text-center h-100">
      <h5 className="title mb-2">{title}</h5>
      <p className="description mb-0">{description}</p>
    </div>
  );
};
export default HowItWorks;
