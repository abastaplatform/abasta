import { Navigate, Outlet } from 'react-router-dom';
import Navbar from '../components/common/Navbar/Navbar';
import { useAuth } from '../context/useAuth';

const PrivateLayout = () => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) return <div>Loading...</div>;

  if (!isAuthenticated) return <Navigate to="/login" replace />;

  return (
    <>
      <Navbar />
      <Outlet />
    </>
  );
};

export default PrivateLayout;
