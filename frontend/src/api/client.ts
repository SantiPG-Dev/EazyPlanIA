import type { AuthResponse, LoginRequest, RegisterRequest, User } from '../types'

const TOKEN_KEY = 'eazyplan_token'

/** Recupera el JWT de localStorage. */
export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * Wrapper sobre fetch que inyecta el JWT y lanza en errores HTTP.
 *
 * - Añade `Authorization: Bearer <token>` si hay token.
 * - Añade `Content-Type: application/json` si hay body.
 * - Devuelve el JSON parseado o `null` en 204.
 * - Lanza un Error con el mensaje del backend en respuestas no-2xx.
 */
export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = getToken()
  const headers: Record<string, string> = {
    ...(options.headers as Record<string, string>),
  }
  if (token) headers['Authorization'] = `Bearer ${token}`
  if (options.body) headers['Content-Type'] = 'application/json'

  const res = await fetch(path, { ...options, headers })

  if (res.status === 204) return null as T
  if (!res.ok) {
    const errorBody = await res.json().catch(() => ({}))
    const message = errorBody.detail || errorBody.message || `Error ${res.status}`
    throw new Error(message)
  }

  return res.json()
}

// ── Endpoints de autenticación ──────────────────────────────────────────

export const authApi = {
  login: (data: LoginRequest) =>
    apiFetch<AuthResponse>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  register: (data: RegisterRequest) =>
    apiFetch<User>('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
}
