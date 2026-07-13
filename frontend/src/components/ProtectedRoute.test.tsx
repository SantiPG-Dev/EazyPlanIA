import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter, Routes, Route } from 'react-router-dom'

vi.mock('../api/client', () => ({
  authApi: { login: vi.fn(), register: vi.fn() },
  getToken: vi.fn(() => null),
  apiFetch: vi.fn(),
}))

import { AuthProvider } from '../context/AuthContext'
import ProtectedRoute from '../components/ProtectedRoute'

describe('ProtectedRoute', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('redirects to /login when not authenticated', () => {
    render(
      <MemoryRouter initialEntries={['/protected']}>
        <AuthProvider>
          <Routes>
            <Route path="/login" element={<div data-testid="login-page">Login</div>} />
            <Route
              path="/protected"
              element={
                <ProtectedRoute>
                  <div data-testid="protected-content">Secret</div>
                </ProtectedRoute>
              }
            />
          </Routes>
        </AuthProvider>
      </MemoryRouter>,
    )
    expect(screen.getByTestId('login-page')).toBeInTheDocument()
    expect(screen.queryByTestId('protected-content')).not.toBeInTheDocument()
  })

  it('renders children when authenticated', () => {
    localStorage.setItem('eazyplan_user', JSON.stringify({
      id: 1, username: 'demo', name: 'Demo', email: 'demo@test.com', role: 'USER',
    }))

    render(
      <MemoryRouter initialEntries={['/protected']}>
        <AuthProvider>
          <Routes>
            <Route path="/login" element={<div data-testid="login-page">Login</div>} />
            <Route
              path="/protected"
              element={
                <ProtectedRoute>
                  <div data-testid="protected-content">Secret</div>
                </ProtectedRoute>
              }
            />
          </Routes>
        </AuthProvider>
      </MemoryRouter>,
    )
    expect(screen.getByTestId('protected-content')).toBeInTheDocument()
    expect(screen.queryByTestId('login-page')).not.toBeInTheDocument()
  })
})
