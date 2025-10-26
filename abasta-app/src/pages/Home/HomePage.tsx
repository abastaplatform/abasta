import HeroSection from './sections/HeroSection/HeroSection';
import Carousel from '../../components/common/Carousel';
import FinalCTA from './sections/FinalCTA/FinalCTA';

const HomePage = () => {
  return (
    <div>
      <HeroSection />
      <Carousel />
      <FinalCTA />
    </div>
  );
};

export default HomePage;
