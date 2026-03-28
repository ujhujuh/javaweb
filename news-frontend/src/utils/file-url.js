const FILE_BASE_URL = (import.meta.env.VITE_FILE_BASE_URL || 'http://110.40.136.241:9000').replace(/\/$/, '')

export function toFileUrl(url) {
  if (!url) return ''
  if (/^https?:\/\//i.test(url)) return url
  if (url.startsWith('/')) return `${FILE_BASE_URL}${url}`
  return `${FILE_BASE_URL}/${url}`
}

export function normalizeHtmlFileUrls(html) {
  if (!html) return html
  return html
    .replace(/src=(['"])(\/[^'"]+)\1/gi, (m, q, p) => `src=${q}${toFileUrl(p)}${q}`)
    .replace(/href=(['"])(\/[^'"]+)\1/gi, (m, q, p) => `href=${q}${toFileUrl(p)}${q}`)
}
