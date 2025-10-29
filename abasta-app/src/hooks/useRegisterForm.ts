import { useState } from 'react';
import { useForm } from 'react-hook-form';
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
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);

  const { register: registerUser } = useAuth();

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<RegisterFormInputs>({ mode: 'onBlur' });

  const onSubmit = async (data: RegisterFormInputs) => {
    setIsLoading(true);
    setError('');

    try {
      await registerUser(data);
      setIsSubmitted(true);
      reset();

      setTimeout(() => {
        setShowSuccess(true);
      }, 2000);
    } catch (err) {
      const errorMessage =
        err instanceof Error
          ? err.message
          : 'Error en el registre. Torna-ho a provar.';

      console.error('Register error:', err);
      setError(errorMessage);
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
    isSubmitted,
    showSuccess,
  };
};
