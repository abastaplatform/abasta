import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { getCompanyByUuid, updateCompanyByUuid } from '../services/company.api';
import type { CompanyFormInputs } from '../services/company.api';

export const useCompanyConfigForm = (uuid: string) => {
  const [isEditing, setIsEditing] = useState(false);
  const [isFetching, setIsFetching] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CompanyFormInputs>({
    mode: 'onBlur',
  });

  //Carregar dades inicials
  useEffect(() => {
    const fetchCompany = async () => {
      try {
        const data = await getCompanyByUuid(uuid);
        reset({
          name: data.name,
          taxId: data.taxId,
          email: data.email,
          phone: data.phone,
          address: data.address,
          city: data.city,
          postalCode: data.postalCode,
        });
      } catch (err: any) {
        console.error('Error al carregar dades:', err);
        setError(err.message || 'Error al carregar les dades de l’empresa.');
      } finally {
        setIsFetching(false);
      }
    };

    fetchCompany();
  }, [uuid, reset]);

  //Actualitzar empresa
  const onSubmit = async (formData: CompanyFormInputs) => {
    setIsLoading(true);
    setError('');
    setSuccess(false);

    try {
      await updateCompanyByUuid(uuid, formData);
      setSuccess(true);
      setIsEditing(false);
    } catch (err: any) {
      console.error('Error al desar:', err);
      setError(err.message || 'No s’han pogut desar les dades de l’empresa.');
    } finally {
      setIsLoading(false);
    }
  };

  const toggleEdit = () => setIsEditing((prev) => !prev);

  return {
    register,
    handleSubmit: handleSubmit(onSubmit),
    errors,
    isEditing,
    toggleEdit,
    isLoading,
    isFetching,
    error,
    success,
  };
};
