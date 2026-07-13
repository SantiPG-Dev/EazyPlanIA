import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { workoutApi } from '../api/workouts'
import type { ExerciseRequest, WorkoutRequest } from '../api/workouts'
import type { Workout as WorkoutType } from '../types'

const WORKOUT_TYPES = ['PUSH', 'PULL', 'LEGS', 'CARDIO', 'FULL_BODY', 'CUSTOM']

export default function WorkoutsPage() {
  const { user } = useAuth()
  const [workouts, setWorkouts] = useState<WorkoutType[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [newType, setNewType] = useState('PUSH')
  const [newNotes, setNewNotes] = useState('')

  // ejercicio para añadir a un workout concreto
  const [exerciseFor, setExerciseFor] = useState<number | null>(null)
  const [exForm, setExForm] = useState<ExerciseRequest>({ name: '', sets: 3, reps: 10, weight: 0 })

  const load = async () => {
    if (!user) return
    try {
      setWorkouts(await workoutApi.list(user.id))
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, []) // eslint-disable-line react-hooks/exhaustive-deps

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault()
    const data: WorkoutRequest = { workoutType: newType, notes: newNotes }
    await workoutApi.create(user!.id, data)
    setNewNotes('')
    setShowForm(false)
    await load()
  }

  const handleAddExercise = async (workoutId: number) => {
    try {
      await workoutApi.addExercise(workoutId, exForm)
      setExerciseFor(null)
      setExForm({ name: '', sets: 3, reps: 10, weight: 0 })
      await load()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error')
    }
  }

  const handleComplete = async (exerciseId: number) => {
    await workoutApi.completeExercise(exerciseId)
    await load()
  }

  const handleEnd = async (workoutId: number) => {
    await workoutApi.endWorkout(workoutId)
    await load()
  }

  const handleDelete = async (id: number) => {
    await workoutApi.delete(id)
    setWorkouts(workouts.filter((w) => w.id !== id))
  }

  return (
    <div>
      <div className="page-header">
        <h1>🏋️ Entrenamientos</h1>
        <button className="btn-primary" onClick={() => setShowForm(!showForm)}>
          {showForm ? 'Cancelar' : '+ Nuevo entreno'}
        </button>
      </div>

      {error && <div className="alert alert-error" onClick={() => setError('')}>{error}</div>}

      {showForm && (
        <form className="card create-form" onSubmit={handleCreate}>
          <div className="form-row">
            <label>Tipo
              <select value={newType} onChange={(e) => setNewType(e.target.value)}>
                {WORKOUT_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </label>
            <label>Notas <input value={newNotes} onChange={(e) => setNewNotes(e.target.value)} placeholder="Opcional" /></label>
          </div>
          <button type="submit" className="btn-primary">Empezar entreno</button>
        </form>
      )}

      {loading ? (
        <p className="muted">Cargando…</p>
      ) : workouts.length === 0 ? (
        <p className="muted">No hay entrenamientos. ¡Empieza el primero!</p>
      ) : (
        <div className="workout-list">
          {workouts.map((w) => (
            <div key={w.id} className="card workout-card">
              <div className="item-header">
                <h3>{w.workoutType}</h3>
                {!w.endTime ? (
                  <span className="badge badge-active">En curso</span>
                ) : (
                  <span className="badge badge-done">Completado</span>
                )}
              </div>
              <p className="item-date">
                {new Date(w.startTime).toLocaleString('es-ES', { dateStyle: 'medium', timeStyle: 'short' })}
                {w.notes && ` · ${w.notes}`}
              </p>

              {w.exercises.length > 0 && (
                <table className="exercise-table">
                  <thead>
                    <tr><th>Ejercicio</th><th>Series</th><th>Reps</th><th>Peso</th><th></th></tr>
                  </thead>
                  <tbody>
                    {w.exercises.map((ex) => (
                      <tr key={ex.id} className={ex.completed ? 'done' : ''}>
                        <td>{ex.name}{ex.muscleGroup && ` (${ex.muscleGroup})`}</td>
                        <td>{ex.sets}</td>
                        <td>{ex.reps}</td>
                        <td>{ex.weight} kg</td>
                        <td>
                          {!ex.completed && (
                            <button className="btn-small" onClick={() => handleComplete(ex.id)}>✓</button>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}

              <div className="workout-actions">
                {exerciseFor === w.id ? (
                  <div className="inline-form">
                    <input placeholder="Ejercicio" value={exForm.name}
                      onChange={(e) => setExForm({ ...exForm, name: e.target.value })} />
                    <input type="number" placeholder="S" value={exForm.sets}
                      onChange={(e) => setExForm({ ...exForm, sets: +e.target.value })} />
                    <input type="number" placeholder="R" value={exForm.reps}
                      onChange={(e) => setExForm({ ...exForm, reps: +e.target.value })} />
                    <input type="number" placeholder="kg" value={exForm.weight}
                      onChange={(e) => setExForm({ ...exForm, weight: +e.target.value })} />
                    <button className="btn-small" onClick={() => handleAddExercise(w.id)}>+</button>
                    <button className="btn-small" onClick={() => setExerciseFor(null)}>✕</button>
                  </div>
                ) : (
                  <>
                    {!w.endTime && (
                      <button className="btn-small" onClick={() => setExerciseFor(w.id)}>+ Ejercicio</button>
                    )}
                    {!w.endTime && (
                      <button className="btn-small" onClick={() => handleEnd(w.id)}>Finalizar</button>
                    )}
                    <button className="btn-danger-sm" onClick={() => handleDelete(w.id)}>Eliminar</button>
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
