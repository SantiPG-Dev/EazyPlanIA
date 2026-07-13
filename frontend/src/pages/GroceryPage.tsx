import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { groceryApi } from '../api/grocery'
import type { GroceryItemRequest } from '../api/grocery'
import type { GroceryList } from '../types'

export default function GroceryPage() {
  const { user } = useAuth()
  const [lists, setLists] = useState<GroceryList[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [itemFor, setItemFor] = useState<number | null>(null)
  const [itemForm, setItemForm] = useState<GroceryItemRequest>({
    name: '', category: '', quantity: 1, unit: 'ud', organic: false,
  })

  const load = async () => {
    if (!user) return
    try {
      setLists(await groceryApi.list(user.id))
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, []) // eslint-disable-line react-hooks/exhaustive-deps

  const handleCreate = async () => {
    await groceryApi.createList(user!.id)
    await load()
  }

  const handleAddItem = async (listId: number) => {
    try {
      await groceryApi.addItem(listId, itemForm)
      setItemFor(null)
      setItemForm({ name: '', category: '', quantity: 1, unit: 'ud', organic: false })
      await load()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error')
    }
  }

  const handlePurchase = async (listId: number) => {
    await groceryApi.markPurchased(listId)
    await load()
  }

  const handleDelete = async (listId: number) => {
    await groceryApi.deleteList(listId)
    setLists(lists.filter((l) => l.id !== listId))
  }

  return (
    <div>
      <div className="page-header">
        <h1>🛒 Lista de la Compra</h1>
        <button className="btn-primary" onClick={handleCreate}>+ Nueva lista</button>
      </div>

      {error && <div className="alert alert-error" onClick={() => setError('')}>{error}</div>}

      {loading ? (
        <p className="muted">Cargando…</p>
      ) : lists.length === 0 ? (
        <p className="muted">No tienes listas de compra. ¡Crea una!</p>
      ) : (
        <div className="item-grid">
          {lists.map((list) => (
            <div key={list.id} className="card item-card">
              <div className="item-header">
                <h3>Lista del {new Date(list.createdAt).toLocaleDateString('es-ES')}</h3>
                {list.purchased ? (
                  <span className="badge badge-done">✓ Comprada</span>
                ) : (
                  <span className="badge badge-active">Pendiente</span>
                )}
              </div>

              {list.items.length > 0 ? (
                <ul className="item-list">
                  {list.items.map((item) => (
                    <li key={item.id}>
                      <span>{item.quantity} {item.unit}</span>
                      <span className="item-name">{item.name}{item.organic && ' 🌿'}</span>
                      {item.category && <span className="muted">{item.category}</span>}
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="muted small">Sin items todavía</p>
              )}

              <div className="workout-actions">
                {itemFor === list.id ? (
                  <div className="inline-form">
                    <input placeholder="Producto" value={itemForm.name}
                      onChange={(e) => setItemForm({ ...itemForm, name: e.target.value })} />
                    <input type="number" placeholder="Cant." value={itemForm.quantity}
                      onChange={(e) => setItemForm({ ...itemForm, quantity: +e.target.value })} />
                    <input placeholder="Unidad" value={itemForm.unit}
                      onChange={(e) => setItemForm({ ...itemForm, unit: e.target.value })} />
                    <label className="checkbox">
                      <input type="checkbox" checked={itemForm.organic}
                        onChange={(e) => setItemForm({ ...itemForm, organic: e.target.checked })} /> 🌿
                    </label>
                    <button className="btn-small" onClick={() => handleAddItem(list.id)}>+</button>
                    <button className="btn-small" onClick={() => setItemFor(null)}>✕</button>
                  </div>
                ) : (
                  <>
                    <button className="btn-small" onClick={() => setItemFor(list.id)}>+ Item</button>
                    {!list.purchased && (
                      <button className="btn-small" onClick={() => handlePurchase(list.id)}>Marcar comprada</button>
                    )}
                    <button className="btn-danger-sm" onClick={() => handleDelete(list.id)}>Eliminar</button>
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
