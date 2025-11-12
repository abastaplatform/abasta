import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { getCompany, updateCompany } from '../services/company.api';
import type { CompanyFormInputs } from '../services/company.api';

export const useCompanyConfigForm = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [isFetching, setIsFetching] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [originalData, setOriginalData] = useState<CompanyFormInputs | null>(null);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CompanyFormInputs>({ mode: 'onBlur' });

  // Carregar dades inicials
  useEffect(() => {
    const fetchCompany = async () => {
      try {
        const data = await getCompany();
        setOriginalData(data);
        reset(data);
      } catch (err: any) {
        console.error('Error al carregar dades:', err);
        setError(err.message || 'Error al carregar les dades de l’empresa.');
      } finally {
        setIsFetching(false);
      }
    };
    fetchCompany();
  }, [reset]);

  // Actualitzar empresa
  const onSubmit = async (formData: CompanyFormInputs) => {
    setIsLoading(true);
    setError('');
    setSuccess(false);

    try {
      const updated = await updateCompany(formData);
      setOriginalData(updated);
      setSuccess(true);
      setIsEditing(false);
    } catch (err: any) {
      console.error('Error al desar:', err);
      setError(err.message || 'No s’han pogut desar les dades de l’empresa.');
    } finally {
      setIsLoading(false);
    }
  };

  return {
    register,
    handleSubmit: handleSubmit(onSubmit),
    errors,
    isEditing,
    toggleEdit: () => setIsEditing((prev) => !prev),
    isLoading,
    isFetching,
    error,
    success,
    reset,
    originalData,
  };
};
