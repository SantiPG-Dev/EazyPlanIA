import type { GroceryItem, GroceryList } from '../types'
import { apiFetch } from './client'

export interface GroceryItemRequest {
  name?: string
  category?: string
  quantity?: number
  unit?: string
  organic?: boolean
}

export const groceryApi = {
  list: (userId: number) =>
    apiFetch<GroceryList[]>(`/api/users/${userId}/grocery-lists`),

  createList: (userId: number) =>
    apiFetch<GroceryList>(`/api/users/${userId}/grocery-lists`, { method: 'POST' }),

  getItems: (listId: number) =>
    apiFetch<GroceryItem[]>(`/api/grocery-lists/${listId}/items`),

  addItem: (listId: number, data: GroceryItemRequest) =>
    apiFetch<GroceryItem>(`/api/grocery-lists/${listId}/items`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  markPurchased: (listId: number) =>
    apiFetch<GroceryList>(`/api/grocery-lists/${listId}/purchase`, { method: 'PATCH' }),

  deleteList: (listId: number) =>
    apiFetch<void>(`/api/grocery-lists/${listId}`, { method: 'DELETE' }),
}
