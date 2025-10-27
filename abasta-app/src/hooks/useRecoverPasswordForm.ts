import { useState } from 'react';
import { useForm } from 'react-hook-form';
import api from '../services/api';

interface RecoverFormInputs {
  email: string;
}

interface RecoverResponse {
  success: boolean;
  message: string;
  timestamp?: string;
}

export const useRecoverPasswordForm = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);
  const [error, setError] = useState('');
  const [serverMessage, setServerMessage] = useState<string | null>(null);

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
    setServerMessage(null);

    try {
      const response = await api.post<RecoverResponse>(
        '/auth/forgot-password',
        { email: data.email }
      );

      if (response.success) {
        setServerMessage(response.message);
        setIsSubmitted(true);

        setTimeout(() => setShowSuccess(true), 2000);
      } else {
        setError(
          response.message ||
            'No s’ha pogut enviar el correu. Torna-ho a provar.'
        );
        setServerMessage(response.message);
      }
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (err) {
      setError('No s’ha pogut enviar el correu. Torna-ho a provar.');
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
    serverMessage,
    setError,
    isSubmitted,
    showSuccess,
  };
};
