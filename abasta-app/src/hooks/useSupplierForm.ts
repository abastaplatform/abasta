import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';

import type { SupplierFormData } from '../types/supplier.types';
import { supplierService } from '../services/supplierService';

interface UseSupplierFormOptions {
  initialData?: SupplierFormData;
  mode?: 'create' | 'edit';
}

export const useSupplierForm = ({
  initialData,
  mode = 'create',
}: UseSupplierFormOptions = {}) => {
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
    defaultValues: initialData || {
      name: '',
      contactName: '',
      email: '',
      phone: '',
      address: '',
      city: '',
      zipCode: '',
      notes: '',
    },
  });

  useEffect(() => {
    if (initialData) reset(initialData);
  }, [initialData, reset]);

  const onSubmit = async (data: SupplierFormData) => {
    setIsLoading(true);
    setError('');
    setSuccessMessage('');

    try {
      if (mode === 'edit' && data.uuid) {
        await supplierService.updateSupplier(data.uuid, data);
        setSuccessMessage('Proveïdor actualitzat correctament');
      } else {
        await supplierService.createSupplier(data);
        setSuccessMessage('Proveïdor creat correctament');
      }

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
