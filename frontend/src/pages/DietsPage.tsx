import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { dietApi } from '../api/diets'
import type { Diet, DietRequest } from '../types'

const DIET_TYPES = ['BALANCED', 'LOW_CARBS', 'HIGH_PROTEIN', 'VEGAN', 'KETO', 'CUSTOM']

const emptyForm: DietRequest = {
  name: '', dietType: 'BALANCED', startDate: new Date().toISOString().slice(0, 10),
  dailyCalories: 2000, dailyProtein: 0, dailyCarbs: 0, dailyFats: 0, dailyWater: 2,
}

export default function DietsPage() {
  const { user } = useAuth()
  const [diets, setDiets] = useState<Diet[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState<DietRequest>(emptyForm)

  const load = async () => {
    if (!user) return
    try {
      setDiets(await dietApi.list(user.id))
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, []) // eslint-disable-line react-hooks/exhaustive-deps

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await dietApi.create(user!.id, form)
      setForm(emptyForm)
      setShowForm(false)
      await load()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al crear')
    }
  }

  const handleDelete = async (id: number) => {
    try {
      await dietApi.delete(id)
      setDiets(diets.filter((d) => d.id !== id))
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al borrar')
    }
  }

  const handleCalculate = async (type: string) => {
    try {
      const result = await dietApi.calculate(type, form.dailyCalories ?? 2000)
      setForm({
        ...emptyForm,
        dietType: type,
        dailyCalories: result.dailyCalories,
        dailyProtein: Math.round(result.dailyProtein),
        dailyCarbs: Math.round(result.dailyCarbs),
        dailyFats: Math.round(result.dailyFats),
      })
      setShowForm(true)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error')
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>🥗 Dietas</h1>
        <button className="btn-primary" onClick={() => setShowForm(!showForm)}>
          {showForm ? 'Cancelar' : '+ Nueva dieta'}
        </button>
      </div>

      {error && <div className="alert alert-error" onClick={() => setError('')}>{error}</div>}

      {showForm && (
        <form className="card create-form" onSubmit={handleCreate}>
          <h3>Crear dieta</h3>
          <div className="form-row">
            <label>Nombre <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required /></label>
            <label>Tipo
              <select value={form.dietType} onChange={(e) => setForm({ ...form, dietType: e.target.value })}>
                {DIET_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </label>
            <label>Inicio <input type="date" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} required /></label>
          </div>
          <div className="form-row">
            <label>Calorías <input type="number" value={form.dailyCalories} onChange={(e) => setForm({ ...form, dailyCalories: +e.target.value })} /></label>
            <label>Proteína (g) <input type="number" value={form.dailyProtein} onChange={(e) => setForm({ ...form, dailyProtein: +e.target.value })} /></label>
            <label>Carbos (g) <input type="number" value={form.dailyCarbs} onChange={(e) => setForm({ ...form, dailyCarbs: +e.target.value })} /></label>
            <label>Grasas (g) <input type="number" value={form.dailyFats} onChange={(e) => setForm({ ...form, dailyFats: +e.target.value })} /></label>
            <label>Agua (L) <input type="number" step="0.1" value={form.dailyWater} onChange={(e) => setForm({ ...form, dailyWater: +e.target.value })} /></label>
          </div>
          <div className="calc-buttons">
            <span className="calc-label">Auto-calcular macros:</span>
            {DIET_TYPES.slice(0, 5).map((t) => (
              <button key={t} type="button" className="btn-small" onClick={() => handleCalculate(t)}>{t}</button>
            ))}
          </div>
          <button type="submit" className="btn-primary">Guardar dieta</button>
        </form>
      )}

      {loading ? (
        <p className="muted">Cargando…</p>
      ) : diets.length === 0 ? (
        <p className="muted">No tienes dietas todavía. ¡Crea la primera!</p>
      ) : (
        <div className="item-grid">
          {diets.map((diet) => (
            <div key={diet.id} className="card item-card">
              <div className="item-header">
                <h3>{diet.name}</h3>
                <span className="badge">{diet.dietType}</span>
              </div>
              <div className="macro-row">
                <span>🔥 {diet.dailyCalories} kcal</span>
                <span>🥩 {diet.dailyProtein}g</span>
                <span>🍞 {diet.dailyCarbs}g</span>
                <span>🥑 {diet.dailyFats}g</span>
              </div>
              <p className="item-date">Desde {diet.startDate}</p>
              <button className="btn-danger-sm" onClick={() => handleDelete(diet.id)}>Eliminar</button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
