import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import type { LoginRequest, RegisterRequest, User } from '../types'
import { authApi, getToken, apiFetch } from '../api/client'

interface AuthContextValue {
  user: User | null
  isAuthenticated: boolean
  login: (data: LoginRequest) => Promise<void>
  register: (data: RegisterRequest) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

const USER_KEY = 'eazyplan_user'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(() => {
    const stored = localStorage.getItem(USER_KEY)
    return stored ? (JSON.parse(stored) as User) : null
  })

  // Verifica que el token siga siendo válido al montar.
  useEffect(() => {
    const token = getToken()
    if (token && !user) {
      // Si hay token pero no user, intentar obtener el usuario actual.
      // Por ahora simplemente limpia si el token no es válido.
      apiFetch<User>('/api/auth/me').catch(() => localStorage.removeItem('eazyplan_token'))
    }
  }, []) // eslint-disable-line react-hooks/exhaustive-deps

  const login = useCallback(async (data: LoginRequest) => {
    const response = await authApi.login(data)
    localStorage.setItem('eazyplan_token', response.token)
    localStorage.setItem(USER_KEY, JSON.stringify(response.user))
    setUser(response.user)
  }, [])

  const register = useCallback(async (data: RegisterRequest) => {
    await authApi.register(data)
    // Tras el registro, hacer login automático para obtener el token.
    await login({ username: data.username, password: data.password })
  }, [login])

  const logout = useCallback(() => {
    localStorage.removeItem('eazyplan_token')
    localStorage.removeItem(USER_KEY)
    setUser(null)
  }, [])

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      isAuthenticated: user !== null,
      login,
      register,
      logout,
    }),
    [user, login, register, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

/** Hook para acceder al contexto de autenticación. */
export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth debe usarse dentro de <AuthProvider>')
  return ctx
}
