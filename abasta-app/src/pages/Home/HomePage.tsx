import HeroSection from './sections/HeroSection/HeroSection';
import Carousel from '../../components/common/Carousel';
import FinalCTA from './sections/FinalCTA/FinalCTA';
import BenefitsSection from './sections/BenefitsSection/BenefitsSection';
import DoToSection from './sections/DoToSection/DoToSection';
import HowItWorksSection from './sections/HowItWorksSection/HowItWorksSection';
import WhatsAbastaSection from './sections/WhatsAbastaSection/WhatsAbastaSection';

const HomePage = () => {
  return (
    <div>
      <HeroSection />
      <BenefitsSection />
      <HowItWorksSection />
      <DoToSection />
      <WhatsAbastaSection />
      <Carousel />
      <FinalCTA />
    </div>
  );
};

export default HomePage;
