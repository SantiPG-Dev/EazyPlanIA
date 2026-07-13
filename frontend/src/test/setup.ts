import '@testing-library/jest-dom/vitest'
import { cleanup } from '@testing-library/react'
import { afterEach, vi } from 'vitest'

// Limpia el DOM entre tests
afterEach(() => {
  cleanup()
  localStorage.clear()
  vi.restoreAllMocks()
})
