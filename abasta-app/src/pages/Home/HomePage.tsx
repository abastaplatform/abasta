import HeroSection from './sections/HeroSection/HeroSection';
import Carousel from '../../components/common/Carousel';
import FinalCTA from './sections/FinalCTA/FinalCTA';
import BenefitsSection from './sections/BenefitsSection/BenefitsSection';

const HomePage = () => {
  return (
    <div>
      <HeroSection />
      <BenefitsSection />
      <Carousel />
      <FinalCTA />
    </div>
  );
};

export default HomePage;
