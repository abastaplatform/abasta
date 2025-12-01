import type {
  ApiResponse,
  CreateUserResponse,
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
};
