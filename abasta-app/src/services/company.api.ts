import ApiService from './api';

export interface CompanyFormInputs {
  name: string;
  taxId: string;
  email: string;
  phone?: string;
  address?: string;
  city?: string;
  postalCode?: string;
}

export interface CompanyResponseDTO extends CompanyFormInputs {
  uuid: string;
  status: 'ACTIVE' | 'INACTIVE' | 'PENDING' | 'SUSPENDED';
  createdAt: string;
  updatedAt: string;
}

export interface ApiResponseDTO<T> {
  success: boolean;
  message: string;
  data: T;
}

/** Llegeix empresa de l’usuari autenticat */
export const getCompany = async (): Promise<CompanyResponseDTO> => {
  const res = await ApiService.get<ApiResponseDTO<CompanyResponseDTO>>('/companies');
  return res.data;
};

/** Actualitza empresa de l’usuari autenticat */
export const updateCompany = async (
  data: Partial<CompanyFormInputs>
): Promise<CompanyResponseDTO> => {
  const res = await ApiService.put<ApiResponseDTO<CompanyResponseDTO>>('/companies', data);
  return res.data;
};
