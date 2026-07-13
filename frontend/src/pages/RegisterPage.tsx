import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()

  const [form, setForm] = useState({
    username: '',
    name: '',
    email: '',
    password: '',
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const update = (field: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm({ ...form, [field]: e.target.value })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await register(form)
      navigate('/', { replace: true })
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al registrarse')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-card">
      <h1>EazyPlanIA</h1>
      <p className="subtitle">Crea tu cuenta gratis</p>

      {error && <div className="alert alert-error">{error}</div>}

      <form onSubmit={handleSubmit}>
        <label>
          Usuario
          <input type="text" value={form.username} onChange={update('username')}
            placeholder="mínimo 3 caracteres" autoComplete="username" required minLength={3} maxLength={30} />
        </label>
        <label>
          Nombre
          <input type="text" value={form.name} onChange={update('name')} required />
        </label>
        <label>
          Email
          <input type="email" value={form.email} onChange={update('email')} autoComplete="email" required />
        </label>
        <label>
          Contraseña
          <input type="password" value={form.password} onChange={update('password')}
            placeholder="mínimo 6 caracteres" autoComplete="new-password" required minLength={6} />
        </label>
        <button type="submit" disabled={loading}>
          {loading ? 'Creando…' : 'Registrarme'}
        </button>
      </form>

      <p className="auth-link">
        ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
      </p>
    </div>
  )
}
