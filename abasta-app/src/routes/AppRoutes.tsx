import { Routes, Route } from 'react-router-dom';

import PublicLayout from '../layouts/PublicLayout';
import PrivateLayout from '../layouts/PrivateLayout';

import HomePage from '../pages/Home';
import LoginForm from '../components/auth/LoginForm/LoginForm';
import RegisterForm from '../components/auth/RegisterForm/RegisterForm';

const AppRoutes = () => {
  return (
    <Routes>
      <Route element={<PublicLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginForm />} />
        <Route path="/register" element={<RegisterForm />} />
        <Route path="/recover" element={<div>Recover page</div>} />
        <Route path="/reset" element={<div>Reset page</div>} />
        <Route path="/privacy" element={<div>Privacy page</div>} />
        <Route path="/terms" element={<div>Terms page</div>} />
        <Route path="/cookies" element={<div>Cookies page</div>} />
        <Route path="/accessibility" element={<div>Accessibility page</div>} />
      </Route>

      <Route element={<PrivateLayout />}>
        <Route path="/dashboard" element={<div>Dashboard page</div>} />
      </Route>
    </Routes>
  );
};

export default AppRoutes;
