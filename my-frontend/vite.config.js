import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    host: true,  // needed for Docker
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://backend:8080',  // ← use service name not localhost
        changeOrigin: true,
        secure: false,
      }
    }
  }
})