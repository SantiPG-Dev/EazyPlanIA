import type { Exercise, Workout } from '../types'
import { apiFetch } from './client'

export interface WorkoutRequest {
  workoutType?: string
  startTime?: string
  notes?: string
}

export interface ExerciseRequest {
  name?: string
  muscleGroup?: string
  sets?: number
  reps?: number
  weight?: number
}

export const workoutApi = {
  list: (userId: number) =>
    apiFetch<Workout[]>(`/api/users/${userId}/workouts`),

  create: (userId: number, data: WorkoutRequest) =>
    apiFetch<Workout>(`/api/users/${userId}/workouts`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  addExercise: (workoutId: number, data: ExerciseRequest) =>
    apiFetch<Exercise>(`/api/workouts/${workoutId}/exercises`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  completeExercise: (exerciseId: number) =>
    apiFetch<Exercise>(`/api/exercises/${exerciseId}/complete`, {
      method: 'PATCH',
    }),

  endWorkout: (workoutId: number) =>
    apiFetch<Workout>(`/api/workouts/${workoutId}/end`, {
      method: 'PATCH',
    }),

  delete: (workoutId: number) =>
    apiFetch<void>(`/api/workouts/${workoutId}`, { method: 'DELETE' }),
}
