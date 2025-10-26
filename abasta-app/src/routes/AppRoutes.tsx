import { Routes, Route } from 'react-router-dom';

import PublicLayout from '../layouts/PublicLayout';

import HomePage from '../pages/Home';
import LoginForm from '../components/auth/LoginForm/LoginForm';

const AppRoutes = () => {
  return (
    <Routes>
      <Route element={<PublicLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginForm />} />
        <Route path="/registrer" element={<div>Register page</div>} />
      </Route>

      <Route element={<PublicLayout />}>
        <Route path="/dashboard" element={<div>Dashboard page</div>} />
      </Route>
    </Routes>
  );
};

export default AppRoutes;
