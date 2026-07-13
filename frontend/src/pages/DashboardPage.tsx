import { Link } from 'react-router-dom'

/**
 * Dashboard: vista principal con navegación a las 3 áreas funcionales.
 */
export default function DashboardPage() {
  return (
    <div>
      <div className="welcome">
        <h2>¡Bienvenido! 👋</h2>
        <p>Tu nutrición, entrenamiento y compra en un solo lugar.</p>
      </div>

      <div className="card-grid">
        <Link to="/diets" className="feature-card">
          <span className="emoji">🥗</span>
          <h3>Dietas</h3>
          <p>Crea y gestiona tus planes nutricionales con reparto automático de macros.</p>
        </Link>
        <Link to="/workouts" className="feature-card">
          <span className="emoji">🏋️</span>
          <h3>Entrenamientos</h3>
          <p>Registra sesiones y ejercicios con seguimiento de series, reps y peso.</p>
        </Link>
        <Link to="/grocery" className="feature-card">
          <span className="emoji">🛒</span>
          <h3>Compra</h3>
          <p>Organiza tu lista de la compra vinculada a tu plan de alimentación.</p>
        </Link>
      </div>
    </div>
  )
}
