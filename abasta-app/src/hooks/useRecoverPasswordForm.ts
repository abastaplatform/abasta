import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useAuth } from '../context/useAuth';

interface RecoverFormInputs {
  email: string;
}

export const useRecoverPasswordForm = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);
  const [error, setError] = useState('');

  const { requestPasswordReset } = useAuth();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RecoverFormInputs>({
    mode: 'onBlur',
  });

  const onSubmit = async (data: RecoverFormInputs) => {
    setIsLoading(true);
    setError('');

    try {
      await requestPasswordReset(data.email);

      setIsSubmitted(true);

      setTimeout(() => setShowSuccess(true), 2000);
    } catch (err) {
      const errorMessage =
        err instanceof Error
          ? err.message
          : "No s'ha pogut enviar el correu. Torna-ho a provar.";

      console.error('Password reset request error:', err);
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return {
    register,
    handleSubmit: handleSubmit(onSubmit),
    errors,
    isLoading,
    error,
    setError,
    isSubmitted,
    showSuccess,
  };
};
