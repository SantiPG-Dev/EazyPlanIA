/** Tipos compartidos del frontend, alineados con los DTOs del backend. */

export interface User {
  id: number
  username: string
  name: string
  email: string
  role: 'ADMIN' | 'USER'
}

export interface AuthResponse {
  token: string
  tokenType: string
  user: User
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  name: string
  email: string
  password: string
}

// ── Diet ──────────────────────────────────────────────────

export interface Diet {
  id: number
  userId: number
  name: string
  description: string | null
  dietType: string
  startDate: string
  endDate: string | null
  dailyCalories: number
  dailyProtein: number
  dailyCarbs: number
  dailyFats: number
  dailyWater: number
}

export interface DietRequest {
  name?: string
  description?: string
  dietType?: string
  startDate?: string
  endDate?: string
  dailyCalories?: number
  dailyProtein?: number
  dailyCarbs?: number
  dailyFats?: number
  dailyWater?: number
}

// ── Workout + Exercise ────────────────────────────────────

export interface Exercise {
  id: number
  workoutId: number
  name: string
  muscleGroup: string | null
  sets: number
  reps: number
  weight: number
  completed: boolean
}

export interface Workout {
  id: number
  userId: number
  startTime: string
  endTime: string | null
  workoutType: string
  notes: string | null
  exercises: Exercise[]
}

// ── Grocery ───────────────────────────────────────────────

export interface GroceryItem {
  id: number
  groceryListId: number
  name: string
  category: string | null
  quantity: number
  unit: string | null
  purchased: boolean
  organic: boolean
}

export interface GroceryList {
  id: number
  userId: number
  createdAt: string
  purchased: boolean
  items: GroceryItem[]
}
