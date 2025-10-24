import Footer from "../../components/common/Footer";

const HomePage = () => {
  return (
    <div className="d-flex flex-column min-vh-100">
      {/* Contingut principal */}
      <main className="flex-fill text-center mt-5">
        <h1>Welcome to the Home Page!</h1>
      </main>
      <Footer />
    </div>
  );
};


export default HomePage;
