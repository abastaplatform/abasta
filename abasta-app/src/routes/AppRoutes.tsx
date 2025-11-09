import { Routes, Route } from 'react-router-dom';

import PublicLayout from '../layouts/PublicLayout';
import PrivateLayout from '../layouts/PrivateLayout';

import HomePage from '../pages/Home';
import LoginForm from '../components/auth/LoginForm/LoginForm';
import RegisterForm from '../components/auth/RegisterForm/RegisterForm';
import VerifyEmail from '../components/auth/VerifyEmail/VerifyEmail';
import RecoverPasswordForm from '../components/auth/RecoverPasswordForm/RecoverPasswordForm';
import ResetPasswordForm from '../components/auth/ResetPasswordForm/ResetPasswordForm';
import CompanyConfigForm from '../components/auth/CompanyConfigForm/CompanyConfigForm';
import Privacy from '../pages/Home/Privacy/Privacy';
import Terms from '../pages/Home/Terms/Terms';
import Cookies from '../pages/Home/Cookies/Cookies';
import Accessibility from '../pages/Home/Accessibility/Accessibility';


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
        <Route path="/privacy" element={<Privacy />} />
        <Route path="/terms" element={<Terms />} />
        <Route path="/cookies" element={<Cookies />} />
        <Route path="/accessibility" element={<Accessibility />} />
      </Route>

      <Route element={<PrivateLayout />}>
        <Route path="/dashboard" element={<div>Dashboard page</div>} />
        <Route path="/orders" element={<div>Orders page</div>} />
        <Route path="/suppliers" element={<div>Suppliers page</div>} />
        <Route path="/products" element={<div>Products page</div>} />
        <Route path="/reports" element={<div>Reports page</div>} />
        <Route path="/Company" element={<CompanyConfigForm />} />
        <Route path="/Users" element={<div>Users page</div>} />
        
      </Route>
    </Routes>
  );
};

export default AppRoutes;
