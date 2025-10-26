import { Outlet } from 'react-router-dom';
import Navbar from '../components/common/Navbar/Navbar';

const PrivateLayout = () => (
  <>
    <Navbar />
    <Outlet />
  </>
);

export default PrivateLayout;
