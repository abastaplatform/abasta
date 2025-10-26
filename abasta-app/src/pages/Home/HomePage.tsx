import 'bootstrap/dist/css/bootstrap.min.css';
import Carousel from '../../components/common/Carousel';
import FinalCTA from './sections/FinalCTA/FinalCTA';
import Footer from '../../components/common/Footer/Footer';

const HomePage = () => {
  return (
    <div>
      {' '}
      {/* Contingut principal */}
      <main className="flex-fill text-center mt-5">
        <h1>Welcome to the Home Page!</h1>
      </main>
      <Carousel />
      <FinalCTA />
      <Footer />
    </div>
  );
};

export default HomePage;
