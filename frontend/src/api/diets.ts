import type { Diet, DietRequest } from '../types'
import { apiFetch } from './client'

export const dietApi = {
  list: (userId: number) =>
    apiFetch<Diet[]>(`/api/users/${userId}/diets`),

  create: (userId: number, data: DietRequest) =>
    apiFetch<Diet>(`/api/users/${userId}/diets`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  delete: (dietId: number) =>
    apiFetch<void>(`/api/diets/${dietId}`, { method: 'DELETE' }),

  calculate: (type: string, targetCalories: number) =>
    apiFetch<Diet>(`/api/diets/calculate?type=${type}&targetCalories=${targetCalories}`, {
      method: 'POST',
    }),
}
