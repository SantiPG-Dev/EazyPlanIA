import { apiFetch } from './client'

export interface AiStatus {
  available: boolean
  message: string
}

export const aiApi = {
  status: () => apiFetch<AiStatus>('/api/ai/status'),

  generateDiet: (goals: string) =>
    apiFetch<any>('/api/ai/generate-diet', {
      method: 'POST',
      body: JSON.stringify({ goals }),
    }),

  generateWorkout: (goals: string) =>
    apiFetch<any>('/api/ai/generate-workout', {
      method: 'POST',
      body: JSON.stringify({ goals }),
    }),

  generateGrocery: (diet: any) =>
    apiFetch<any>('/api/ai/generate-grocery', {
      method: 'POST',
      body: JSON.stringify({ diet }),
    }),

  acceptDiet: (userId: number, diet: any) =>
    apiFetch<{ id: number; message: string }>('/api/ai/accept-diet', {
      method: 'POST',
      body: JSON.stringify({ userId, diet }),
    }),

  acceptWorkout: (userId: number, workout: any) =>
    apiFetch<{ id: number; message: string }>('/api/ai/accept-workout', {
      method: 'POST',
      body: JSON.stringify({ userId, workout }),
    }),

  acceptGrocery: (userId: number, grocery: any) =>
    apiFetch<{ id: number; message: string }>('/api/ai/accept-grocery', {
      method: 'POST',
      body: JSON.stringify({ userId, grocery }),
    }),
}
