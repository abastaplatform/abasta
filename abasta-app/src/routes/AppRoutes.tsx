import { Routes, Route } from 'react-router-dom';

import PublicLayout from '../layouts/PublicLayout';
import PrivateLayout from '../layouts/PrivateLayout';

import HomePage from '../pages/Home';
import LoginForm from '../components/auth/LoginForm/LoginForm';
import RegisterForm from '../components/auth/RegisterForm/RegisterForm';
import VerifyEmail from '../components/auth/VerifyEmail/VerifyEmail';
import RecoverPasswordForm from '../components/auth/RecoverPasswordForm/RecoverPasswordForm';
import ResetPasswordForm from '../components/auth/ResetPasswordForm/ResetPasswordForm';
import SupplierList from '../components/suppliers/SupplierList/SupplierList';
import CompanyConfigForm from '../components/auth/CompanyConfigForm/CompanyConfigForm';
import Privacy from '../pages/Home/Privacy/Privacy';
import Terms from '../pages/Home/Terms/Terms';
import Cookies from '../pages/Home/Cookies/Cookies';
import Accessibility from '../pages/Home/Accessibility/Accessibility';
import SupplierManager from '../components/suppliers/SupplierManager/SupplierManager';
import ProductList from '../components/products/ProductList/ProductList';
import ProductManager from '../components/products/ProductManager/ProductManager';
import Orders from '../components/orders/sendOrder/SendOrder';

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
        <Route path="/suppliers" element={<SupplierList />} />
        <Route
          path="/suppliers/new"
          element={<SupplierManager mode="create" />}
        />
        <Route
          path="/suppliers/:uuid"
          element={<SupplierManager mode="detail" />}
        />
        <Route
          path="/suppliers/edit/:uuid"
          element={<SupplierManager mode="edit" />}
        />
        <Route path="/orders" element={<Orders />} />
        <Route path="/products" element={<ProductList />} />
        <Route
          path="/products/new"
          element={<ProductManager mode="create" />}
        />
        <Route
          path="/products/:uuid"
          element={<ProductManager mode="detail" />}
        />
        <Route
          path="/products/edit/:uuid"
          element={<ProductManager mode="edit" />}
        />
        <Route path="/reports" element={<div>Reports page</div>} />
        <Route path="/Company" element={<CompanyConfigForm />} />
        <Route path="/Users" element={<div>Users page</div>} />
      </Route>
    </Routes>
  );
};

export default AppRoutes;
