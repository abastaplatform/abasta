import { useState } from 'react';
import { useForm } from 'react-hook-form';
import api from '../services/api';
import { useSearchParams } from 'react-router-dom';

interface ResetFormInputs {
  password: string;
  repeatPassword: string;
}

interface ResetResponse {
  success: boolean;
  message: string;
  timestamp?: string;
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
  const [serverMessage, setServerMessage] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResetFormInputs>({
    mode: 'onBlur',
  });

  const onSubmit = async (data: ResetFormInputs) => {
    setIsLoading(true);
    setError('');
    setServerMessage(null);

    if (data.password !== data.repeatPassword) {
      setIsLoading(false);
      return setError('Les contrasenyes no coincideixen.');
    }

    try {
      const response = await api.post<ResetResponse>('/auth/reset-password', {
        token,
        newPassword: data.password,
      });

      if (response.success) {
        setServerMessage(response.message);
        setIsSubmitted(true);

        setTimeout(() => setShowSuccess(true), 2000);
      } else {
        setError(response.message || 'No s’ha pogut restablir la contrasenya.');
        setServerMessage(response.message);
      }
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (err) {
      setError('No s’ha pogut restablir la contrasenya. Torna-ho a provar.');
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
    serverMessage,
    setError,
    isSubmitted,
    showSuccess,
  };
};
