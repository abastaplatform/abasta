import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/useAuth';

interface LoginFormInputs {
  email: string;
  password: string;
}

export const useLoginForm = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const { login } = useAuth();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormInputs>({
    mode: 'onBlur',
  });

  const onSubmit = async (data: LoginFormInputs) => {
    setIsLoading(true);
    setError('');

    try {
      await login(data.email, data.password);
      navigate('/dashboard');
    } catch (err) {
      setError('Correu o contrasenya incorrectes. Torna-ho a provar.');
      console.error('Login error:', err);
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
  };
};
