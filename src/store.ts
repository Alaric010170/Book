import { reactive, computed } from 'vue';
import type { Reader, Book, Borrowing, Notification } from './types';
import * as api from './api';

const state = reactive({
  isLoggedIn: !!api.getToken(),
  adminName: localStorage.getItem('lms_adminName') || '管理员',
  adminRole: localStorage.getItem('lms_adminRole') || '馆长',
  adminAvatar: localStorage.getItem('lms_adminAvatar') || '',
  activeTab: 'dashboard' as 'dashboard' | 'books' | 'borrowing' | 'readers' | 'help' | 'settings',
  globalSearch: '',
  readers: [] as Reader[],
  books: [] as Book[],
  borrowings: [] as Borrowing[],
  notifications: [] as Notification[],
  loading: false,
});

// --- Auth ---
async function login(username: string, password: string) {
  const res = await api.login(username, password);
  api.setToken(res.token);
  state.adminName = res.name;
  state.adminRole = res.role;
  state.adminAvatar = res.avatar;
  localStorage.setItem('lms_adminName', res.name);
  localStorage.setItem('lms_adminRole', res.role);
  localStorage.setItem('lms_adminAvatar', res.avatar);
  state.isLoggedIn = true;
  // 登录后加载所有数据
  await Promise.all([loadReaders(), loadBooks(), loadBorrowings(), loadNotifications()]);
}

function logout() {
  api.clearToken();
  state.isLoggedIn = false;
  state.readers = [];
  state.books = [];
  state.borrowings = [];
  state.notifications = [];
}

function setTab(tab: 'dashboard' | 'books' | 'borrowing' | 'readers' | 'help' | 'settings') {
  state.activeTab = tab;
}

function setGlobalSearch(val: string) {
  state.globalSearch = val;
}

// --- Data Loaders ---
async function loadReaders(keyword?: string, level?: string) {
  state.readers = await api.getReaders(keyword, level);
}

async function loadBooks(keyword?: string, category?: string, status?: string) {
  state.books = await api.getBooks(keyword, category, status);
}

async function loadBorrowings(keyword?: string, status?: string) {
  state.borrowings = await api.getBorrowings(keyword, status);
}

async function loadNotifications() {
  state.notifications = await api.getNotifications();
}

// --- READERS ACTIONS ---
async function addReader(reader: Omit<Reader, 'id'>) {
  const created = await api.createReader(reader);
  state.readers.unshift(created);
}

async function updateReader(id: string, updated: Partial<Omit<Reader, 'id'>>) {
  const result = await api.updateReader(id, updated);
  const index = state.readers.findIndex(r => r.id === id);
  if (index !== -1) {
    state.readers[index] = result;
  }
}

async function deleteReader(id: string) {
  await api.deleteReader(id);
  state.readers = state.readers.filter(r => r.id !== id);
}

// --- BOOKS ACTIONS ---
async function addBook(book: Omit<Book, 'id'>) {
  const created = await api.createBook(book);
  state.books.unshift(created);
}

async function updateBook(id: string, updated: Partial<Omit<Book, 'id'>>) {
  const result = await api.updateBook(id, updated);
  const index = state.books.findIndex(b => b.id === id);
  if (index !== -1) {
    state.books[index] = result;
  }
}

async function deleteBook(id: string) {
  await api.deleteBook(id);
  state.books = state.books.filter(b => b.id !== id);
}

// --- BORROWING ACTIONS ---
async function createBorrowing(borrowing: Omit<Borrowing, 'id'>) {
  const created = await api.createBorrowing(borrowing);
  state.borrowings.unshift(created);
  // 刷新图书列表以更新状态
  await loadBooks();
}

async function returnBorrowing(id: string) {
  await api.returnBorrowing(id);
  state.borrowings = state.borrowings.filter(b => b.id !== id);
  // 刷新图书列表以更新状态
  await loadBooks();
}

async function renewBorrowing(id: string) {
  const result = await api.renewBorrowing(id);
  const index = state.borrowings.findIndex(b => b.id === id);
  if (index !== -1) {
    state.borrowings[index] = result;
  }
}

// --- NOTIFICATION ACTIONS ---
async function addNotification(notif: Omit<Notification, 'id' | 'isRead' | 'time'>) {
  const created = await api.createNotification(notif);
  state.notifications.unshift(created);
}

async function markNotificationAsRead(id: string) {
  await api.markNotificationRead(id);
  const notif = state.notifications.find(n => n.id === id);
  if (notif) {
    notif.isRead = true;
  }
}

async function markAllNotificationsAsRead() {
  await api.markAllNotificationsRead();
  state.notifications.forEach(n => n.isRead = true);
}

async function clearNotification(id: string) {
  await api.deleteNotification(id);
  state.notifications = state.notifications.filter(n => n.id !== id);
}

async function clearAllNotifications() {
  await api.deleteAllNotifications();
  state.notifications = [];
}

// --- Computed Statistics ---
const readersStats = computed(() => {
  const total = state.readers.length;
  const premium = state.readers.filter(r => r.level === 'premium').length;
  const premiumRatio = total > 0 ? Math.round((premium / total) * 100) : 0;
  return { total, premium, premiumRatio };
});

const booksStats = computed(() => {
  const total = state.books.length;
  const inLibrary = state.books.filter(b => b.status === 'available').length;
  const overdue = state.books.filter(b => b.status === 'overdue').length;
  return { total, inLibrary, overdue };
});

const borrowingsStats = computed(() => {
  const total = state.borrowings.length;
  const overdue = state.borrowings.filter(b => b.status === 'overdue').length;
  const dueToday = state.borrowings.filter(b => b.status === 'due_today').length;
  return { total, overdue, dueToday };
});

export const useStore = () => {
  return {
    state,
    login,
    logout,
    setTab,
    setGlobalSearch,
    loadReaders,
    loadBooks,
    loadBorrowings,
    loadNotifications,
    addReader,
    updateReader,
    deleteReader,
    addBook,
    updateBook,
    deleteBook,
    createBorrowing,
    returnBorrowing,
    renewBorrowing,
    addNotification,
    markNotificationAsRead,
    markAllNotificationsAsRead,
    clearNotification,
    clearAllNotifications,
    readersStats,
    booksStats,
    borrowingsStats,
  };
};
