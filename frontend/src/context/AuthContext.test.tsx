import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import type { ReactNode } from 'react'

// Mock del módulo de API antes de importar AuthContext
vi.mock('../api/client', () => ({
  authApi: {
    login: vi.fn(),
    register: vi.fn(),
  },
  getToken: vi.fn(() => null),
  apiFetch: vi.fn(),
}))

import { AuthProvider, useAuth } from './AuthContext'
import { authApi } from '../api/client'
import type { AuthResponse } from '../types'

/** Componente consumidor del contexto para poder testear useAuth(). */
function Consumer() {
  const { user, isAuthenticated, logout } = useAuth()
  return (
    <div>
      <span data-testid="auth-state">{isAuthenticated ? 'authed' : 'guest'}</span>
      {user && <span data-testid="user-name">{user.name}</span>}
      <button onClick={logout}>Logout</button>
    </div>
  )
}

function renderWithProvider(ui: ReactNode) {
  return render(<AuthProvider>{ui}</AuthProvider>)
}

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('starts as guest when no stored session', () => {
    renderWithProvider(<Consumer />)
    expect(screen.getByTestId('auth-state')).toHaveTextContent('guest')
  })

  it('login stores user and sets authenticated', async () => {
    const mockResponse: AuthResponse = {
      token: 'fake-jwt',
      tokenType: 'Bearer',
      user: { id: 1, username: 'demo', name: 'Demo', email: 'demo@test.com', role: 'USER' },
    }
    vi.mocked(authApi.login).mockResolvedValueOnce(mockResponse)

    /** Componente que combina el consumer + botón de login. */
    function ConsumerWithLogin() {
      const { user, isAuthenticated, login } = useAuth()
      return (
        <div>
          <span data-testid="auth-state">{isAuthenticated ? 'authed' : 'guest'}</span>
          {user && <span data-testid="user-name">{user.name}</span>}
          <button onClick={() => login({ username: 'demo', password: 'pass' })}>Login</button>
        </div>
      )
    }

    renderWithProvider(<ConsumerWithLogin />)
    expect(screen.getByTestId('auth-state')).toHaveTextContent('guest')

    await userEvent.click(screen.getByText('Login'))

    await waitFor(() => {
      expect(screen.getByTestId('auth-state')).toHaveTextContent('authed')
      expect(screen.getByTestId('user-name')).toHaveTextContent('Demo')
    })
    expect(localStorage.getItem('eazyplan_token')).toBe('fake-jwt')
  })

  it('logout clears state and localStorage', async () => {
    // Preestablecer sesión en localStorage
    localStorage.setItem('eazyplan_token', 'stored-token')
    localStorage.setItem('eazyplan_user', JSON.stringify({
      id: 1, username: 'demo', name: 'Demo', email: 'demo@test.com', role: 'USER',
    }))

    renderWithProvider(<Consumer />)
    expect(screen.getByTestId('auth-state')).toHaveTextContent('authed')

    await userEvent.click(screen.getByText('Logout'))

    expect(screen.getByTestId('auth-state')).toHaveTextContent('guest')
    expect(localStorage.getItem('eazyplan_token')).toBeNull()
    expect(localStorage.getItem('eazyplan_user')).toBeNull()
  })

  it('restores session from localStorage on mount', () => {
    localStorage.setItem('eazyplan_user', JSON.stringify({
      id: 1, username: 'demo', name: 'Demo', email: 'demo@test.com', role: 'USER',
    }))

    renderWithProvider(<Consumer />)
    expect(screen.getByTestId('auth-state')).toHaveTextContent('authed')
    expect(screen.getByTestId('user-name')).toHaveTextContent('Demo')
  })
})
