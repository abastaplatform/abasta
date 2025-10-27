import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/useAuth';

interface RegisterFormInputs {
  companyName: string;
  taxId: string;
  adminFirstName: string;
  adminLastName: string;
  adminEmail: string;
  adminPassword: string;
}

export const useRegisterForm = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const { register: registerUser } = useAuth();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormInputs>({ mode: 'onBlur' });

  const onSubmit = async (data: RegisterFormInputs) => {
    setIsLoading(true);
    setError('');
    setSuccess('');

    try {
      await registerUser(data);
      setSuccess(
        'Compte creat correctament! Revisa el teu correu electrònic per verificar el teu compte.'
      );

      setTimeout(() => navigate('/login'), 4000);
    } catch (err) {
      console.error('Register error:', err);
      setError('Error en el registre. Torna-ho a provar.');
    } finally {
      setIsLoading(false);
    }
  };

  return {
    register,
    handleSubmit: handleSubmit(onSubmit),
    errors,
    showPassword,
    setShowPassword,
    isLoading,
    error,
    setError,
    success,
  };
};
