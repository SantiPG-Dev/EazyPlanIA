import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import { aiApi } from '../api/ai'

type Mode = 'diet' | 'workout' | 'grocery' | null

export default function AiPage() {
  const { user } = useAuth()
  const [available, setAvailable] = useState<boolean | null>(null)
  const [mode, setMode] = useState<Mode>(null)
  const [prompt, setPrompt] = useState('')
  const [result, setResult] = useState<any>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [saved, setSaved] = useState('')

  useEffect(() => {
    aiApi.status().then((s) => setAvailable(s.available)).catch(() => setAvailable(false))
  }, [])

  const getPlaceholder = () => {
    switch (mode) {
      case 'diet': return 'Ej: Dieta keto de 1800 kcal para definición muscular'
      case 'workout': return 'Ej: Rutina PULL para hipertrofia, nivel avanzado'
      case 'grocery': return 'Describe los alimentos y planes para generar la compra'
      default: return ''
    }
  }

  const getPromptLabel = () => {
    switch (mode) {
      case 'diet': return 'Objetivos de la dieta'
      case 'workout': return 'Objetivos del entrenamiento'
      case 'grocery': return 'Dieta o alimentos de referencia'
      default: return ''
    }
  }

  const handleGenerate = async () => {
    if (!mode || !prompt.trim()) return
    setLoading(true)
    setError('')
    setResult(null)
    setSaved('')
    try {
      let res
      switch (mode) {
        case 'diet':
          res = await aiApi.generateDiet(prompt)
          break
        case 'workout':
          res = await aiApi.generateWorkout(prompt)
          break
        case 'grocery':
          res = await aiApi.generateGrocery({ meals: [{ foods: prompt.split(',') }] })
          break
      }
      setResult(res)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al generar')
    } finally {
      setLoading(false)
    }
  }

  const handleSave = async () => {
    if (!result || !user) return
    setSaved('')
    try {
      let res
      switch (mode) {
        case 'diet':
          res = await aiApi.acceptDiet(user.id, result)
          break
        case 'workout':
          res = await aiApi.acceptWorkout(user.id, result)
          break
        case 'grocery':
          res = await aiApi.acceptGrocery(user.id, result)
          break
      }
      setSaved(res?.message || 'Guardado correctamente')
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al guardar')
    }
  }

  if (available === null) return <p className="muted">Conectando con IA…</p>

  return (
    <div>
      <div className="page-header">
        <h1>🤖 Asistente IA</h1>
        <span className={`badge ${available ? 'badge-done' : 'badge-active'}`}>
          {available ? '🟢 Conectado' : '🔴 No disponible'}
        </span>
      </div>

      {!available && (
        <div className="alert alert-error">
          LM Studio no está corriendo. Arranca LM Studio y asegúrate de que el modelo
          esté cargado en <code>localhost:1234</code>.
        </div>
      )}

      {available && (
        <>
          <div className="card create-form">
            <h3>¿Qué quieres generar?</h3>
            <div className="mode-selector">
              <button
                className={`btn-mode ${mode === 'diet' ? 'active' : ''}`}
                onClick={() => { setMode('diet'); setResult(null); setSaved('') }}
              >🥗 Dieta</button>
              <button
                className={`btn-mode ${mode === 'workout' ? 'active' : ''}`}
                onClick={() => { setMode('workout'); setResult(null); setSaved('') }}
              >🏋️ Entreno</button>
              <button
                className={`btn-mode ${mode === 'grocery' ? 'active' : ''}`}
                onClick={() => { setMode('grocery'); setResult(null); setSaved('') }}
              >🛒 Compra</button>
            </div>

            {mode && (
              <>
                <div className="form-row">
                  <label>
                    {getPromptLabel()}
                    <textarea
                      value={prompt}
                      onChange={(e) => setPrompt(e.target.value)}
                      placeholder={getPlaceholder()}
                      rows={3}
                    />
                  </label>
                </div>
                <button className="btn-primary" onClick={handleGenerate} disabled={loading || !prompt.trim()}>
                  {loading ? 'Generando…' : '✨ Generar con IA'}
                </button>
              </>
            )}
          </div>

          {error && <div className="alert alert-error" onClick={() => setError('')}>{error}</div>}
          {saved && <div className="alert alert-success" onClick={() => setSaved('')}>{saved}</div>}

          {result && (
            <div className="card result-card">
              <div className="ai-result">
                {renderResult(mode, result)}
              </div>
              <button className="btn-primary" onClick={handleSave} style={{ marginTop: '1rem' }}>
                💾 Guardar en mi cuenta
              </button>
            </div>
          )}
        </>
      )}
    </div>
  )
}

function renderResult(mode: Mode, data: any) {
  switch (mode) {
    case 'diet':
      return (
        <>
          <h2>{data.name}</h2>
          <p className="muted">{data.description}</p>
          <div className="macro-row" style={{ margin: '1rem 0' }}>
            <span>🔥 {data.dailyCalories} kcal</span>
            <span>🥩 {data.dailyProtein}g</span>
            <span>🍞 {data.dailyCarbs}g</span>
            <span>🥑 {data.dailyFats}g</span>
            <span>💧 {data.dailyWater}L</span>
          </div>
          <span className="badge">{data.dietType}</span>
          {data.meals?.map((meal: any, i: number) => (
            <div key={i} className="meal-block">
              <h3>{meal.name} — {meal.calories} kcal</h3>
              <p className="muted">P:{meal.protein}g · C:{meal.carbs}g · G:{meal.fats}g</p>
              <ul className="food-list">
                {meal.foods?.map((f: string, j: number) => <li key={j}>{f}</li>)}
              </ul>
            </div>
          ))}
        </>
      )

    case 'workout':
      return (
        <>
          <h2>{data.workoutType}</h2>
          <p className="muted">{data.notes}</p>
          <table className="exercise-table" style={{ marginTop: '1rem' }}>
            <thead>
              <tr><th>Ejercicio</th><th>Grupo</th><th>Series</th><th>Reps</th><th>Peso</th></tr>
            </thead>
            <tbody>
              {data.exercises?.map((ex: any, i: number) => (
                <tr key={i}>
                  <td>{ex.name}</td>
                  <td>{ex.muscleGroup}</td>
                  <td>{ex.sets}</td>
                  <td>{ex.reps}</td>
                  <td>{ex.weight} kg</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )

    case 'grocery':
      return (
        <>
          <h2>🛒 Lista de la Compra</h2>
          <ul className="item-list">
            {data.items?.map((item: any, i: number) => (
              <li key={i}>
                <span>{item.quantity} {item.unit}</span>
                <span className="item-name">{item.name}{item.organic ? ' 🌿' : ''}</span>
                <span className="muted">{item.category}</span>
              </li>
            ))}
          </ul>
        </>
      )

    default:
      return <p className="muted">Resultado no disponible</p>
  }
}
