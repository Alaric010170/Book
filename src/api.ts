import type { Reader, Book, Borrowing, Notification } from './types';

// 相对路径：本地开发时靠 Vite dev server proxy，生产部署时靠 Nginx 反代
const BASE_URL = '/api';

let authToken: string | null = localStorage.getItem('lms_token');

function headers(): Record<string, string> {
  const h: Record<string, string> = { 'Content-Type': 'application/json' };
  if (authToken) {
    h['Authorization'] = `Bearer ${authToken}`;
  }
  return h;
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: { ...headers(), ...options.headers },
  });
  const json = await res.json();
  if (json.code !== 200) {
    throw new Error(json.message || '请求失败');
  }
  return json.data as T;
}

export function setToken(token: string) {
  authToken = token;
  localStorage.setItem('lms_token', token);
}

export function clearToken() {
  authToken = null;
  localStorage.removeItem('lms_token');
}

export function getToken(): string | null {
  return authToken;
}

// --- Auth ---
export interface LoginResponse {
  token: string;
  name: string;
  role: string;
  avatar: string;
}

export async function login(username: string, password: string): Promise<LoginResponse> {
  const res = await fetch(`${BASE_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });
  const json = await res.json();
  if (json.code !== 200) {
    throw new Error(json.message || '登录失败');
  }
  return json.data as LoginResponse;
}

// --- Dashboard ---
export interface DashboardStats {
  totalBooks: number;
  inLibrary: number;
  overdueBooks: number;
  totalReaders: number;
  premiumReaders: number;
  premiumRatio: number;
  activeBorrowings: number;
  overdueBorrowings: number;
  dueToday: number;
}

export async function getDashboardStats(): Promise<DashboardStats> {
  return request<DashboardStats>('/dashboard/stats');
}

// --- Readers ---
export async function getReaders(keyword?: string, level?: string): Promise<Reader[]> {
  const params = new URLSearchParams();
  if (keyword) params.set('keyword', keyword);
  if (level) params.set('level', level);
  const qs = params.toString();
  return request<Reader[]>(`/readers${qs ? '?' + qs : ''}`);
}

export async function createReader(reader: Omit<Reader, 'id'>): Promise<Reader> {
  return request<Reader>('/readers', {
    method: 'POST',
    body: JSON.stringify(reader),
  });
}

export async function updateReader(id: string, reader: Partial<Reader>): Promise<Reader> {
  return request<Reader>(`/readers/${id}`, {
    method: 'PUT',
    body: JSON.stringify(reader),
  });
}

export async function deleteReader(id: string): Promise<void> {
  return request<void>(`/readers/${id}`, { method: 'DELETE' });
}

// --- Books ---
export async function getBooks(keyword?: string, category?: string, status?: string): Promise<Book[]> {
  const params = new URLSearchParams();
  if (keyword) params.set('keyword', keyword);
  if (category) params.set('category', category);
  if (status) params.set('status', status);
  const qs = params.toString();
  return request<Book[]>(`/books${qs ? '?' + qs : ''}`);
}

export async function createBook(book: Omit<Book, 'id'>): Promise<Book> {
  return request<Book>('/books', {
    method: 'POST',
    body: JSON.stringify(book),
  });
}

export async function updateBook(id: string, book: Partial<Book>): Promise<Book> {
  return request<Book>(`/books/${id}`, {
    method: 'PUT',
    body: JSON.stringify(book),
  });
}

export async function deleteBook(id: string): Promise<void> {
  return request<void>(`/books/${id}`, { method: 'DELETE' });
}

// --- Borrowings ---
export async function getBorrowings(keyword?: string, status?: string): Promise<Borrowing[]> {
  const params = new URLSearchParams();
  if (keyword) params.set('keyword', keyword);
  if (status) params.set('status', status);
  const qs = params.toString();
  return request<Borrowing[]>(`/borrowings${qs ? '?' + qs : ''}`);
}

export async function createBorrowing(borrowing: Omit<Borrowing, 'id'>): Promise<Borrowing> {
  return request<Borrowing>('/borrowings', {
    method: 'POST',
    body: JSON.stringify(borrowing),
  });
}

export async function returnBorrowing(id: string): Promise<void> {
  return request<void>(`/borrowings/${id}/return`, { method: 'POST' });
}

export async function renewBorrowing(id: string): Promise<Borrowing> {
  return request<Borrowing>(`/borrowings/${id}/renew`, { method: 'POST' });
}

// --- Notifications ---
export async function getNotifications(): Promise<Notification[]> {
  return request<Notification[]>('/notifications');
}

export async function createNotification(notif: Omit<Notification, 'id' | 'isRead' | 'time'>): Promise<Notification> {
  return request<Notification>('/notifications', {
    method: 'POST',
    body: JSON.stringify(notif),
  });
}

export async function markNotificationRead(id: string): Promise<void> {
  return request<void>(`/notifications/${id}/read`, { method: 'PATCH' });
}

export async function markAllNotificationsRead(): Promise<void> {
  return request<void>('/notifications/read-all', { method: 'PATCH' });
}

export async function deleteNotification(id: string): Promise<void> {
  return request<void>(`/notifications/${id}`, { method: 'DELETE' });
}

export async function deleteAllNotifications(): Promise<void> {
  return request<void>('/notifications', { method: 'DELETE' });
}
