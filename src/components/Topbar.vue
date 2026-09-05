<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useStore } from '../store';
import { 
  Search, 
  Bell, 
  Settings, 
  Book, 
  User, 
  RefreshCw, 
  ArrowRight, 
  X, 
  Check, 
  Trash2, 
  AlertCircle, 
  Info 
} from 'lucide-vue-next';

const store = useStore();
const { 
  state, 
  setGlobalSearch, 
  setTab,
  markNotificationAsRead,
  markAllNotificationsAsRead,
  clearNotification,
  clearAllNotifications
} = store;

const isFocused = ref(false);
const dropdownRef = ref<HTMLElement | null>(null);

const isNotificationOpen = ref(false);
const notificationRef = ref<HTMLElement | null>(null);

const unreadCount = computed(() => {
  return state.notifications.filter(n => !n.isRead).length;
});

// Direct binding or local computed to sync with state.globalSearch
const searchVal = computed({
  get: () => state.globalSearch,
  set: (val) => setGlobalSearch(val)
});

// Real-time matched results from store
const matchedBooks = computed(() => {
  const q = state.globalSearch.trim().toLowerCase();
  if (!q) return [];
  return state.books.filter(b => 
    b.title.toLowerCase().includes(q) || 
    b.author.toLowerCase().includes(q) || 
    b.isbn.toLowerCase().includes(q)
  ).slice(0, 3);
});

const matchedReaders = computed(() => {
  const q = state.globalSearch.trim().toLowerCase();
  if (!q) return [];
  return state.readers.filter(r => 
    r.name.toLowerCase().includes(q) || 
    r.email.toLowerCase().includes(q) || 
    r.id.toLowerCase().includes(q)
  ).slice(0, 3);
});

const matchedBorrowings = computed(() => {
  const q = state.globalSearch.trim().toLowerCase();
  if (!q) return [];
  return state.borrowings.filter(b => 
    b.readerName.toLowerCase().includes(q) || 
    b.readerId.toLowerCase().includes(q) || 
    b.bookTitle.toLowerCase().includes(q)
  ).slice(0, 3);
});

const hasAnyResults = computed(() => {
  return matchedBooks.value.length > 0 || matchedReaders.value.length > 0 || matchedBorrowings.value.length > 0;
});

// Navigate and scope search on specific tab
const goToResult = (tab: 'books' | 'borrowing' | 'readers', queryValue?: string) => {
  if (queryValue) {
    setGlobalSearch(queryValue);
  }
  setTab(tab);
  isFocused.value = false;
};

// Clear active search query
const clearSearch = () => {
  setGlobalSearch('');
};

// Click outside to close dropdown handler
const handleClickOutside = (event: MouseEvent) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
    isFocused.value = false;
  }
  if (notificationRef.value && !notificationRef.value.contains(event.target as Node)) {
    isNotificationOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener('mousedown', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('mousedown', handleClickOutside);
});
</script>

<template>
  <header class="fixed top-0 right-0 left-0 ml-[280px] h-16 bg-[#F7F9FB] border-b border-[#E0E3E5] flex justify-between items-center px-6 z-40">
    <!-- Left: Global Search Input with Instant Results Dropdown -->
    <div class="flex items-center gap-4 flex-1 relative" ref="dropdownRef">
      <div class="relative w-full max-w-md">
        <span class="absolute left-3 top-1/2 -translate-y-1/2 text-[#757682]">
          <Search class="h-4 w-4" />
        </span>
        <input 
          v-model="searchVal"
          @focus="isFocused = true"
          type="text" 
          class="w-full pl-10 pr-10 py-2 bg-[#F2F4F6] border border-[#C5C5D3] rounded-lg focus:outline-none focus:border-[#00236F] focus:bg-white focus:ring-2 focus:ring-[#00236F]/15 transition-all text-sm placeholder-[#444651]" 
          placeholder="搜索目录、作者、读者、借阅..." 
        />
        <button 
          v-if="searchVal.trim()" 
          @click="clearSearch"
          class="absolute right-3 top-1/2 -translate-y-1/2 text-[#757682] hover:text-[#191C1E] p-1 rounded-full hover:bg-slate-200 transition-colors"
        >
          <X class="h-3.5 w-3.5" />
        </button>
      </div>

      <!-- Real-time Dropdown Results overlay -->
      <div 
        v-if="isFocused && searchVal.trim()"
        class="absolute top-full left-0 w-full max-w-md bg-white border border-[#E0E3E5] shadow-2xl rounded-xl mt-1.5 p-4 z-50 overflow-hidden max-h-[420px] overflow-y-auto"
      >
        <!-- No results layout -->
        <div v-if="!hasAnyResults" class="text-center py-6 text-[#757682]">
          <p class="text-sm">没有找到与 <strong class="text-[#00236F]">"{{ searchVal }}"</strong> 匹配的数据</p>
          <p class="text-[11px] mt-1 text-opacity-80">试试搜索其它书名、作者、ISBN 或读者姓名</p>
        </div>

        <div v-else class="space-y-4">
          <!-- Matching Books section -->
          <div v-if="matchedBooks.length > 0">
            <div class="flex items-center justify-between border-b border-[#ECEEF0] pb-1 mb-2">
              <span class="text-[10px] font-extrabold uppercase tracking-widest text-[#57657A] flex items-center gap-1">
                <Book class="h-3 w-3" />
                图书检索 ({{ matchedBooks.length }})
              </span>
              <button 
                @click="goToResult('books')" 
                class="text-[10px] text-[#00236F] font-bold hover:underline flex items-center gap-0.5"
              >
                查看全部
                <ArrowRight class="h-2.5 w-2.5" />
              </button>
            </div>
            <div class="space-y-1.5">
              <div 
                v-for="book in matchedBooks" 
                :key="book.id"
                @click="goToResult('books', book.title)"
                class="flex items-center gap-3 p-1.5 rounded-lg hover:bg-[#F2F4F6] transition-colors cursor-pointer text-left"
              >
                <img :src="book.cover" class="w-7 h-10 object-cover rounded border border-slate-200" />
                <div class="flex-1 min-w-0">
                  <p class="text-xs font-bold text-[#191C1E] truncate">{{ book.title }}</p>
                  <p class="text-[10px] text-[#57657A] truncate">{{ book.author }} · {{ book.category }}</p>
                </div>
                <span 
                  class="text-[9px] px-1.5 py-0.5 rounded font-extrabold"
                  :class="book.status === 'available' ? 'bg-emerald-50 text-emerald-800' : 'bg-blue-50 text-blue-800'"
                >
                  {{ book.status === 'available' ? '在馆' : '借出' }}
                </span>
              </div>
            </div>
          </div>

          <!-- Matching Readers section -->
          <div v-if="matchedReaders.length > 0">
            <div class="flex items-center justify-between border-b border-[#ECEEF0] pb-1 mb-2">
              <span class="text-[10px] font-extrabold uppercase tracking-widest text-[#57657A] flex items-center gap-1">
                <User class="h-3 w-3" />
                读者检索 ({{ matchedReaders.length }})
              </span>
              <button 
                @click="goToResult('readers')" 
                class="text-[10px] text-[#00236F] font-bold hover:underline flex items-center gap-0.5"
              >
                查看全部
                <ArrowRight class="h-2.5 w-2.5" />
              </button>
            </div>
            <div class="space-y-1.5">
              <div 
                v-for="reader in matchedReaders" 
                :key="reader.id"
                @click="goToResult('readers', reader.name)"
                class="flex items-center gap-3 p-1.5 rounded-lg hover:bg-[#F2F4F6] transition-colors cursor-pointer text-left"
              >
                <img :src="reader.avatar" class="w-7 h-7 rounded-full object-cover border border-slate-200" />
                <div class="flex-1 min-w-0">
                  <p class="text-xs font-bold text-[#191C1E] truncate">{{ reader.name }}</p>
                  <p class="text-[10px] text-[#57657A] truncate">{{ reader.email }}</p>
                </div>
                <span 
                  class="text-[9px] px-1.5 py-0.5 rounded font-extrabold"
                  :class="reader.level === 'premium' ? 'bg-yellow-100 text-yellow-800' : 'bg-slate-100 text-slate-800'"
                >
                  {{ reader.level === 'premium' ? 'VIP' : '普通' }}
                </span>
              </div>
            </div>
          </div>

          <!-- Matching Borrowings section -->
          <div v-if="matchedBorrowings.length > 0">
            <div class="flex items-center justify-between border-b border-[#ECEEF0] pb-1 mb-2">
              <span class="text-[10px] font-extrabold uppercase tracking-widest text-[#57657A] flex items-center gap-1">
                <RefreshCw class="h-3 w-3" />
                流转交易检索 ({{ matchedBorrowings.length }})
              </span>
              <button 
                @click="goToResult('borrowing')" 
                class="text-[10px] text-[#00236F] font-bold hover:underline flex items-center gap-0.5"
              >
                查看全部
                <ArrowRight class="h-2.5 w-2.5" />
              </button>
            </div>
            <div class="space-y-1.5">
              <div 
                v-for="bor in matchedBorrowings" 
                :key="bor.id"
                @click="goToResult('borrowing', bor.readerName)"
                class="p-2 rounded-lg hover:bg-[#F2F4F6] transition-colors cursor-pointer text-left"
              >
                <div class="flex items-center justify-between">
                  <p class="text-xs font-bold text-[#191C1E] truncate">{{ bor.bookTitle }}</p>
                  <span 
                    class="text-[9px] px-1.5 py-0.5 rounded font-extrabold"
                    :class="bor.status === 'overdue' ? 'bg-red-50 text-red-800' : 'bg-emerald-50 text-emerald-800'"
                  >
                    {{ bor.status === 'overdue' ? '逾期' : '借出中' }}
                  </span>
                </div>
                <p class="text-[10px] text-[#57657A] mt-0.5 truncate flex items-center gap-1">
                  <span>借阅人: <strong>{{ bor.readerName }}</strong></span>
                  <span>·</span>
                  <span>应还日: {{ bor.dueDate }}</span>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Right: Notifications, Settings & Admin ID -->
    <div class="flex items-center gap-6">
      <div class="flex items-center gap-2">
        <!-- Notification Dropdown Container -->
        <div class="relative" ref="notificationRef">
          <button 
            @click="isNotificationOpen = !isNotificationOpen"
            :class="[
              'relative p-2 rounded-full hover:bg-[#ECEEF0] transition-colors cursor-pointer',
              isNotificationOpen ? 'text-[#00236F] bg-[#D5E3FC]' : 'text-[#444651]'
            ]"
            title="通知中心"
          >
            <Bell class="h-5 w-5" />
            <span 
              v-if="unreadCount > 0" 
              class="absolute top-1.5 right-1.5 min-w-4 h-4 px-1 bg-[#BA1A1A] text-white text-[9px] font-extrabold flex items-center justify-center rounded-full border border-[#F7F9FB]"
            >
              {{ unreadCount }}
            </span>
          </button>

          <!-- Dropdown Overlay Menu -->
          <div 
            v-if="isNotificationOpen"
            class="absolute right-0 top-full mt-2 w-96 bg-white border border-[#E0E3E5] shadow-2xl rounded-xl z-55 overflow-hidden"
          >
            <!-- Dropdown Header -->
            <div class="p-4 border-b border-[#ECEEF0] flex items-center justify-between bg-slate-50">
              <div class="flex items-center gap-2">
                <span class="font-bold text-sm text-[#191C1E]">实时校务通知</span>
                <span 
                  v-if="unreadCount > 0" 
                  class="bg-[#00236F] text-white text-[10px] font-extrabold px-1.5 py-0.5 rounded-full"
                >
                  {{ unreadCount }} 条未读
                </span>
              </div>
              <div class="flex gap-2.5">
                <button 
                  v-if="unreadCount > 0"
                  @click="markAllNotificationsAsRead"
                  class="text-[11px] text-[#00236F] font-bold hover:underline cursor-pointer"
                >
                  全部已读
                </button>
                <button 
                  v-if="state.notifications.length > 0"
                  @click="clearAllNotifications"
                  class="text-[11px] text-[#BA1A1A] font-bold hover:underline cursor-pointer"
                >
                  清空
                </button>
              </div>
            </div>

            <!-- Notification Items Scroll area -->
            <div class="max-h-[350px] overflow-y-auto divide-y divide-[#ECEEF0]">
              <!-- Empty state -->
              <div v-if="state.notifications.length === 0" class="text-center py-10 px-4 text-[#757682]">
                <Info class="h-8 w-8 mx-auto text-[#C5C5D3] mb-2" />
                <p class="text-xs">暂无任何历史校务消息与系统通告</p>
              </div>

              <!-- List -->
              <div 
                v-for="notif in state.notifications" 
                :key="notif.id"
                :class="[
                  'p-4 transition-all hover:bg-[#F2F4F6] text-left flex gap-3 relative',
                  notif.isRead ? 'bg-white' : 'bg-[#00236F]/5'
                ]"
              >
                <!-- Unread Blue Dot -->
                <span 
                  v-if="!notif.isRead" 
                  class="absolute top-4 left-2 w-1.5 h-1.5 bg-[#00236F] rounded-full"
                ></span>

                <!-- Left icon indicator depending on severity -->
                <div class="shrink-0 mt-0.5">
                  <span 
                    class="w-7 h-7 rounded-lg flex items-center justify-center text-xs font-semibold"
                    :class="{
                      'bg-red-50 text-red-700 border border-red-100': notif.type === 'alert',
                      'bg-amber-50 text-amber-700 border border-amber-100': notif.type === 'warning',
                      'bg-emerald-50 text-emerald-700 border border-emerald-100': notif.type === 'success',
                      'bg-blue-50 text-blue-700 border border-[#D5E3FC]': notif.type === 'info'
                    }"
                  >
                    <AlertCircle v-if="notif.type === 'alert' || notif.type === 'warning'" class="h-4 w-4" />
                    <Info v-else class="h-4 w-4" />
                  </span>
                </div>

                <!-- Middle Content -->
                <div class="flex-1 min-w-0 pr-1">
                  <div class="flex items-center justify-between gap-1.5">
                    <p 
                      class="text-xs font-bold text-[#191C1E] truncate"
                      :class="{ 'text-slate-900 font-extrabold': !notif.isRead }"
                    >
                      {{ notif.title }}
                    </p>
                    <span class="text-[10px] text-[#757682] shrink-0 font-medium">{{ notif.time }}</span>
                  </div>
                  <p class="text-[11px] text-[#57657A] mt-1 pr-1 leading-relaxed break-words">
                    {{ notif.content }}
                  </p>
                  
                  <!-- Quick Interactive Inline actions -->
                  <div class="flex gap-2.5 mt-2">
                    <button 
                      v-if="!notif.isRead"
                      @click="markNotificationAsRead(notif.id)"
                      class="text-[10px] text-[#00236F] font-bold hover:underline flex items-center gap-0.5 cursor-pointer"
                    >
                      <Check class="h-3 w-3" /> 标记已读
                    </button>
                    <button 
                      @click="clearNotification(notif.id)"
                      class="text-[10px] text-[#BA1A1A] font-bold hover:underline flex items-center gap-0.5 ml-auto cursor-pointer"
                    >
                      <Trash2 class="h-3 w-3" /> 移除
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Footer view total logs -->
            <div class="p-3 bg-slate-50 border-t border-[#ECEEF0] text-center">
              <span class="text-[10px] text-[#757682] uppercase tracking-wider font-semibold">
                安全审计服务运作良好
              </span>
            </div>
          </div>
        </div>
        
        <button 
          @click="setTab('settings')"
          :class="[
            'p-2 rounded-full hover:bg-[#ECEEF0] transition-colors cursor-pointer',
            state.activeTab === 'settings' ? 'text-[#00236F] bg-[#D5E3FC]' : 'text-[#444651]'
          ]"
          title="系统设置"
        >
          <Settings class="h-5 w-5" />
        </button>
      </div>

      <!-- Divider -->
      <div class="h-8 w-px bg-[#C5C5D3]"></div>

      <!-- Profiler Card -->
      <div class="flex items-center gap-3">
        <div class="text-right hidden sm:block">
          <p class="font-bold text-sm text-[#191C1E] leading-tight">{{ state.adminName }}</p>
          <p class="text-[10px] text-[#444651] font-semibold uppercase tracking-wider opacity-75">
            {{ state.adminRole }}
          </p>
        </div>
        <img 
          alt="Librarian Avatar" 
          class="w-10 h-10 rounded-full object-cover border border-[#E0E3E5]" 
          :src="state.adminAvatar" 
        />
      </div>
    </div>
  </header>
</template>

