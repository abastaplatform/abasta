import { Navigate, Outlet } from 'react-router-dom';
import Leftbar from '../components/common/Leftbar/Leftbar';
import { useAuth } from '../context/useAuth';

const PrivateLayout = () => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) return <div>Loading...</div>;

  if (!isAuthenticated) return <Navigate to="/login" replace />;

  return (
    <>
      <Leftbar />
      <Outlet />
    </>
  );
};

export default PrivateLayout;
