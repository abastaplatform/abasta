import { Routes, Route } from 'react-router-dom';

import PublicLayout from '../layouts/PublicLayout';
import PrivateLayout from '../layouts/PrivateLayout';

import HomePage from '../pages/Home';
import LoginForm from '../components/auth/LoginForm/LoginForm';
import RegisterForm from '../components/auth/RegisterForm/RegisterForm';
import VerifyEmail from '../components/auth/VerifyEmail/VerifyEmail';
import RecoverPasswordForm from '../components/auth/RecoverPasswordForm/RecoverPasswordForm';
import ResetPasswordForm from '../components/auth/ResetPasswordForm/ResetPasswordForm';

const AppRoutes = () => {
  return (
    <Routes>
      <Route element={<PublicLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginForm />} />
        <Route path="/register" element={<RegisterForm />} />
        <Route path="/verify-email" element={<VerifyEmail />} />
        <Route path="/recover" element={<RecoverPasswordForm />} />
        <Route path="/reset-password" element={<ResetPasswordForm />} />
        <Route path="/privacy" element={<div>Privacy page</div>} />
        <Route path="/terms" element={<div>Terms page</div>} />
        <Route path="/cookies" element={<div>Cookies page</div>} />
        <Route path="/accessibility" element={<div>Accessibility page</div>} />
      </Route>

      <Route element={<PrivateLayout />}>
        <Route path="/dashboard" element={<div>Dashboard page</div>} />
        <Route path="/orders" element={<div>Orders page</div>} />
        <Route path="/suppliers" element={<div>Suppliers page</div>} />
        <Route path="/products" element={<div>Products page</div>} />
        <Route path="/reports" element={<div>Reports page</div>} />
        <Route path="/Company" element={<div>Company page</div>} />
        <Route path="/Users" element={<div>Users page</div>} />
      </Route>
    </Routes>
  );
};

export default AppRoutes;
