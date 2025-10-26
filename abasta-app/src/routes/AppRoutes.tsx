import { Routes, Route } from 'react-router-dom';

import PublicLayout from '../layouts/PublicLayout';

import HomePage from '../pages/Home';

const AppRoutes = () => {
  return (
    <Routes>
      <Route element={<PublicLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<div>Login Page</div>} />
      </Route>
    </Routes>
  );
};

export default AppRoutes;
