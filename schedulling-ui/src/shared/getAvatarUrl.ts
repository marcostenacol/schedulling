const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export function getAvatarUrl(avatar?: string | null): string | null {
  if (!avatar) return null;
  return `${API_BASE_URL}/uploads/${avatar}`;
}
