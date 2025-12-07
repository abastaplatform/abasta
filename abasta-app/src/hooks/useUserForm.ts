import { useForm } from 'react-hook-form';
import type { UserFormData } from '../types/user.types';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { userService } from '../services/userService';

interface UseUserFormOptions {
  initialData?: UserFormData;
  mode?: 'create' | 'edit';
}

export const useUserForm = ({
  initialData,
  mode = 'create',
}: UseUserFormOptions = {}) => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<UserFormData>({
    mode: 'onBlur',
    defaultValues: initialData || {
      email: '',
      firstName: '',
      lastName: '',
      role: 'USER',
      phone: '',
      password: '',
      isActive: true,
    },
  });

  useEffect(() => {
    if (initialData) reset(initialData);
  }, [initialData, reset]);

  const onSubmit = async (data: UserFormData) => {
    setIsLoading(true);
    setError('');
    setSuccessMessage('');

    try {
      if (mode === 'edit' && data.uuid) {
        await userService.updateUser(data.uuid, data);
        setSuccessMessage('Usuari actualitzat correctament');
      } else {
        await userService.createUser(data);
        setSuccessMessage('Usuari creat correctament');
      }

      setTimeout(() => {
        reset();

        navigate('/users');
      }, 2000);
    } catch (error) {
      const errorMessage =
        error instanceof Error
          ? error.message
          : "Error al crear l'usuari. Torna-ho a provar";

      console.error('Create user error: ', error);
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCancel = () => {
    reset();
    navigate('/users');
  };

  return {
    register,
    handleSubmit: handleSubmit(onSubmit),
    errors,
    isLoading,
    error,
    setError,
    successMessage,
    handleCancel,
  };
};
