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
