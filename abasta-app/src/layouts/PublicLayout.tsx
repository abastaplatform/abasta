import { Outlet } from 'react-router-dom';
import Navbar from '../components/common/Navbar/Navbar';

const PublicLayout = () => (
  <>
    <Navbar />
    <Outlet />
  </>
);

export default PublicLayout;
