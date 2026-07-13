import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'

// Mock del módulo de API para interceptar login/register
vi.mock('../api/client', () => ({
  authApi: {
    login: vi.fn(),
    register: vi.fn(),
  },
  getToken: vi.fn(() => null),
  apiFetch: vi.fn(),
}))

import LoginPage from '../pages/LoginPage'
import { AuthProvider } from '../context/AuthContext'
import { authApi } from '../api/client'

function renderLoginPage() {
  return render(
    <MemoryRouter initialEntries={['/login']}>
      <AuthProvider>
        <LoginPage />
      </AuthProvider>
    </MemoryRouter>,
  )
}

describe('LoginPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('renders the login form with username, password and submit button', () => {
    renderLoginPage()
    expect(screen.getByPlaceholderText('tu_usuario')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('••••••••')).toBeInTheDocument()
    expect(screen.getByText('Iniciar sesión')).toBeInTheDocument()
  })

  it('shows error message on failed login', async () => {
    vi.mocked(authApi.login).mockRejectedValueOnce(new Error('Invalid credentials'))

    renderLoginPage()
    await userEvent.type(screen.getByPlaceholderText('tu_usuario'), 'baduser')
    await userEvent.type(screen.getByPlaceholderText('••••••••'), 'badpass')
    await userEvent.click(screen.getByText('Iniciar sesión'))

    expect(await screen.findByText('Invalid credentials')).toBeInTheDocument()
  })

  it('navigates to /register via the link', async () => {
    renderLoginPage()
    const link = screen.getByText('Regístrate')
    expect(link).toHaveAttribute('href', '/register')
  })
})
