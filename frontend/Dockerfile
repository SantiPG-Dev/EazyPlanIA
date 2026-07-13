# ============================================================✉
# Dockerfile — EazyPlanIA Frontend (React + Vite)
# Multi-stage: Node build → nginx serve
# ============================================================

# ---- Stage 1: Build ----
FROM node:22-alpine AS builder

RUN corepack enable && corepack prepare pnpm@latest --activate
WORKDIR /build

COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY . .
RUN pnpm build

# ---- Stage 2: nginx ----
FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /build/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
