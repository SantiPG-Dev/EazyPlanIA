import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // Proxy de /api al backend Spring Boot (puerto 8080) en desarrollo.
      // Evita problemas de CORS: el navegador habla con :5173 y Vite reenvía a :8080.
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
