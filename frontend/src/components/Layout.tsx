import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

/** Layout para páginas autenticadas: navbar superior + contenido. */
export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    isActive ? 'nav-link active' : 'nav-link'

  return (
    <div className="app-layout">
      <nav className="navbar">
        <div className="nav-brand">🏋️ EazyPlanIA</div>
        <div className="nav-links">
          <NavLink to="/" className={linkClass} end>Dashboard</NavLink>
          <NavLink to="/diets" className={linkClass}>Dietas</NavLink>
          <NavLink to="/workouts" className={linkClass}>Entrenamientos</NavLink>
          <NavLink to="/grocery" className={linkClass}>Compra</NavLink>
        </div>
        <div className="nav-user">
          <span>{user?.name}</span>
          <button className="btn-logout" onClick={handleLogout}>Salir</button>
        </div>
      </nav>
      <main className="page-content">
        <Outlet />
      </main>
    </div>
  )
}
