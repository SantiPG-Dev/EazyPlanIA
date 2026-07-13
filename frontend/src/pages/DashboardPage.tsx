import { useAuth } from '../context/AuthContext'

/**
 * Dashboard placeholder: muestra al usuario autenticado.
 * Las páginas de Dietas/Entrenamientos/Compra llegan en el Paso 8.
 */
export default function DashboardPage() {
  const { user, logout } = useAuth()

  return (
    <div className="dashboard">
      <header>
        <h1>EazyPlanIA</h1>
        <button className="btn-logout" onClick={logout}>Cerrar sesión</button>
      </header>

      <div className="welcome">
        <h2>¡Hola, {user?.name}! 👋</h2>
        <p>Tu nutrición, entrenamiento y compra en un solo lugar.</p>
      </div>

      <div className="card-grid">
        <div className="feature-card">
          <span className="emoji">🥗</span>
          <h3>Dietas</h3>
          <p>Crea y gestiona tus planes nutricionales con reparto de macros.</p>
        </div>
        <div className="feature-card">
          <span className="emoji">🏋️</span>
          <h3>Entrenamientos</h3>
          <p>Registra tus sesiones y ejercicios con seguimiento de progreso.</p>
        </div>
        <div className="feature-card">
          <span className="emoji">🛒</span>
          <h3>Compra</h3>
          <p>Organiza tu lista de la compra vinculada a tu plan.</p>
        </div>
      </div>

      <p className="coming-soon">Próximamente: gestión completa de dietas, entrenamientos y compra.</p>
    </div>
  )
}
