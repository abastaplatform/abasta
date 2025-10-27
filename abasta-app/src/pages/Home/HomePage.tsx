import HeroSection from './sections/HeroSection/HeroSection';
import Carousel from '../../components/common/Carousel';
import FinalCTA from './sections/FinalCTA/FinalCTA';
import BenefitsSection from './sections/BenefitsSection/BenefitsSection';
import HowItWorksSection from './sections/HowItWorksSection/HowItWorksSection';

const HomePage = () => {
  return (
    <div>
      <span id="home">
        <HeroSection />
      </span>
      <span id="benefits">
        <BenefitsSection />
      </span>
      <span id="how-it-works">
        <HowItWorksSection />
      </span>
      <span id="features"></span>
      <span id="sectors"></span>
      <span id="testimonials">
        <Carousel />
      </span>

      <FinalCTA />
    </div>
  );
};

export default HomePage;
