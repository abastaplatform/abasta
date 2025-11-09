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
  id: number;
  uuid: string;
  status: 'ACTIVE' | 'INACTIVE' | 'PENDING';
  createdAt: string;
  updatedAt: string;
}

export interface ApiResponseDTO<T> {
  success: boolean;
  message: string;
  data: T;
}

/**Llegeix empresa per UUID */
export const getCompanyByUuid = async (
  uuid: string
): Promise<CompanyResponseDTO> => {
  const res = await ApiService.get<ApiResponseDTO<CompanyResponseDTO>>(
    `/companies/${uuid}`
  );
  return res.data;
};

/**Actualitza una empresa per UUID*/
export const updateCompanyByUuid = async (
  uuid: string,
  data: Partial<CompanyFormInputs>
): Promise<CompanyResponseDTO> => {
  const res = await ApiService.put<ApiResponseDTO<CompanyResponseDTO>>(
    `/companies/${uuid}`,
    data
  );
  return res.data;
};
