import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';

import type { SupplierFormData } from '../types/supplier.types';
import { supplierService } from '../services/supplierService';

export const useSupplierForm = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<SupplierFormData>({
    mode: 'onBlur',
    defaultValues: {
      name: '',
      contactName: '',
      email: '',
      phone: '',
      address: '',
      notes: '',
    },
  });

  const onSubmit = async (data: SupplierFormData) => {
    setIsLoading(true);
    setError('');
    setSuccessMessage('');

    try {
      await supplierService.createSupplier(data);
      setSuccessMessage('Proveïdor creat correctament');

      setTimeout(() => {
        reset();

        navigate('/suppliers');
      }, 2000);
    } catch (error) {
      const errorMessage =
        error instanceof Error
          ? error.message
          : 'Error al crear el proveïdor. Torna-ho a provar';

      console.error('Create supplier error: ', error);
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCancel = () => {
    reset();
    navigate('/suppliers');
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
