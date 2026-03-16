import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/doc.html': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/v2/api-docs': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/v3/api-docs': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/swagger-resources': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/webjars': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/favicon.ico': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/druid': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/javaweb': {
        target: 'http://110.40.136.241:9000',
        changeOrigin: true
      }
    }
  }
})