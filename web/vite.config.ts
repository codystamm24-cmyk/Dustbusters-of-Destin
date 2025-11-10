import { defineConfig } from 'vite'

// Minimal Vite config without ESM-only plugin to avoid environment issues in the container.
export default defineConfig({
  server: { port: 3000 },
})
