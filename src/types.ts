export interface Reader {
  id: string;
  name: string;
  email: string;
  level: 'premium' | 'regular';
  borrows: number;
  avatar: string;
}

export interface Book {
  id: string;
  cover: string;
  title: string;
  author: string;
  isbn: string;
  category: string;
  status: 'available' | 'borrowed' | 'overdue';
}

export interface Borrowing {
  id: string;
  readerId: string;
  readerName: string;
  readerInitials: string;
  bookTitle: string;
  borrowDate: string;
  dueDate: string;
  status: 'normal' | 'overdue' | 'due_today';
  overdueDays?: number;
}

export interface Notification {
  id: string;
  title: string;
  content: string;
  time: string;
  type: 'info' | 'warning' | 'success' | 'alert';
  isRead: boolean;
}

