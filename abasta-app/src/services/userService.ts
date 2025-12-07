import type {
  AdvancedSearchParams,
  ApiResponse,
  BasicSearchParams,
  CreateUserResponse,
  PaginatedResponse,
  PaginationParams,
  User,
  UserFormData,
} from '../types/user.types';
import api from './api';

export const userService = {
  createUser: async (
    data: UserFormData
  ): Promise<ApiResponse<CreateUserResponse<User>>> => {
    const res = await api.post<ApiResponse<CreateUserResponse<User>>>(
      '/users',
      data
    );
    console.log('createUser response:', res);
    return res;
  },

  getUserByUuid: async (uuid: string): Promise<ApiResponse<User>> => {
    return await api.get<ApiResponse<User>>(`/users/${uuid}`);
  },

  updateUser: async (
    uuid: string,
    data: UserFormData
  ): Promise<ApiResponse<UserFormData>> => {
    const response = await api.put<ApiResponse<User>>(`/users/${uuid}`, data);

    if (response.data) {
      return {
        ...response,
        data: response.data as UserFormData,
      };
    }
    return response as ApiResponse<UserFormData>;
  },

  getUsers: async (
    params: PaginationParams
  ): Promise<ApiResponse<PaginatedResponse<User>>> => {
    const queryString = new URLSearchParams({
      page: params.page.toString(),
      size: params.size.toString(),
      sortBy: params.sortBy || 'firstName',
      sortDir: params.sortDir || 'asc',
    }).toString();
    return await api.get<ApiResponse<PaginatedResponse<User>>>(
      `/users?${queryString}`
    );
  },

  searchUsers: async (
    params: BasicSearchParams
  ): Promise<ApiResponse<PaginatedResponse<User>>> => {
    const queryParams = new URLSearchParams();

    if (params.searchText) {
      queryParams.append('searchText', params.searchText);
    }
    if (params.page !== undefined) {
      queryParams.append('page', params.page.toString());
    }
    if (params.size !== undefined) {
      queryParams.append('size', params.size.toString());
    }

    return await api.get(`/users/search?${queryParams.toString()}`);
  },

  filterUsers: async (
    params: AdvancedSearchParams
  ): Promise<ApiResponse<PaginatedResponse<User>>> => {
    const queryParams = new URLSearchParams();

    if (params.firstName) {
      queryParams.append('firstName', params.firstName);
    }
    if (params.lastName) {
      queryParams.append('lastName', params.lastName);
    }
    if (params.email) {
      queryParams.append('email', params.email);
    }
    if (params.phone) {
      queryParams.append('phone', params.phone);
    }
    if (params.isActive !== undefined) {
      queryParams.append('isActive', params.isActive.toString());
    }
    if (params.role !== undefined) {
      queryParams.append('role', params.role.toString());
    }
    if (params.emailVerified !== undefined) {
      queryParams.append('emailVerified', params.emailVerified.toString());
    }
    if (params.page !== undefined) {
      queryParams.append('page', params.page.toString());
    }
    if (params.size !== undefined) {
      queryParams.append('size', params.size.toString());
    }

    return await api.get(`/users/filter?${queryParams.toString()}`);
  },

  deleteUser: async (uuid: string): Promise<void> => {
    await api.delete(`/users/${uuid}`);
  },
};
