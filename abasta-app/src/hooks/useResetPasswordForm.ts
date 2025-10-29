import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/useAuth';

interface ResetFormInputs {
  password: string;
  repeatPassword: string;
}

export const useResetPasswordForm = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token') || '';

  const [showPassword, setShowPassword] = useState(false);
  const [showRepeatPassword, setShowRepeatPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);
  const [error, setError] = useState('');

  const { resetPassword } = useAuth();

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<ResetFormInputs>({
    mode: 'onBlur',
  });

  const onSubmit = async (data: ResetFormInputs) => {
    setIsLoading(true);
    setError('');

    if (data.password !== data.repeatPassword) {
      setError('Les contrasenyes no coincideixen.');
      setIsLoading(false);
      return;
    }

    try {
      await resetPassword(token, data.password);

      setIsSubmitted(true);

      setTimeout(() => {
        setShowSuccess(true);
      }, 2000);
    } catch (err) {
      const errorMessage =
        err instanceof Error
          ? err.message
          : "No s'ha pogut restablir la contrasenya. Torna-ho a provar.";

      console.error('Password reset error:', err);
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
    showRepeatPassword,
    setShowRepeatPassword,
    isLoading,
    error,
    setError,
    isSubmitted,
    showSuccess,
    token,
    password: watch('password'),
  };
};
