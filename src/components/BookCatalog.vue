<script setup lang="ts">
import { ref, computed } from 'vue';
import { useStore } from '../store';
import type { Book } from '../types';
import { 
  Plus, 
  Search, 
  BookMarked, 
  CheckCircle, 
  AlertCircle, 
  Edit3, 
  Info, 
  ChevronLeft, 
  ChevronRight, 
  Inbox, 
  Trash2, 
  X, 
  Printer, 
  Filter 
} from 'lucide-vue-next';

const store = useStore();

// UI Filters
const filterCategory = ref('所有类别');
const statusFilter = ref<'all' | 'available' | 'borrowed' | 'overdue'>('all');
const currentPage = ref(1);
const itemsPerPage = 4;

// Modal triggers for additions/edits
const showModal = ref(false);
const isEditing = ref(false);
const selectedBookId = ref<string | null>(null);

const formTitle = ref('');
const formAuthor = ref('');
const formIsbn = ref('');
const formCategory = ref('科学');
const formStatus = ref<'available' | 'borrowed' | 'overdue'>('available');

const openAddModal = () => {
  isEditing.value = false;
  selectedBookId.value = null;
  formTitle.value = '';
  formAuthor.value = '';
  formIsbn.value = '';
  formCategory.value = '科学';
  formStatus.value = 'available';
  showModal.value = true;
};

const openEditModal = (book: Book) => {
  isEditing.value = true;
  selectedBookId.value = book.id;
  formTitle.value = book.title;
  formAuthor.value = book.author;
  formIsbn.value = book.isbn;
  formCategory.value = book.category;
  formStatus.value = book.status;
  showModal.value = true;
};

const handleFormSubmit = async () => {
  if (!formTitle.value || !formAuthor.value || !formIsbn.value) return;

  if (isEditing.value && selectedBookId.value) {
    await store.updateBook(selectedBookId.value, {
      title: formTitle.value,
      author: formAuthor.value,
      isbn: formIsbn.value,
      category: formCategory.value,
      status: formStatus.value,
    });
  } else {
    let coverId = '1544716278-ca5e3f4abd8c';
    if (formCategory.value === '科学') coverId = '1507668077129-56e32842fceb';
    if (formCategory.value === '技术') coverId = '1515879218367-8466d910aaa4';
    if (formCategory.value === '历史') coverId = '1447069387593-a5de0862481e';

    const coverUrl = `https://images.unsplash.com/photo-${coverId}?w=150&h=200&fit=crop`;
    
    await store.addBook({
      title: formTitle.value,
      author: formAuthor.value,
      isbn: formIsbn.value,
      category: formCategory.value,
      status: formStatus.value,
      cover: coverUrl,
    });
  }
  showModal.value = false;
};

const handleDeleteBook = async (id: string, name: string) => {
  if (confirm(`确认从资源目录和库存列表中永续移去藏书 "${name}" 吗？`)) {
    await store.deleteBook(id);
  }
};

const handlePrint = () => {
  alert('系统正在连接至打印终端，全站图书目录编目清册就绪中...');
};

// Filter Chain
const processedBooks = computed(() => {
  let list = [...store.state.books];

  // Dynamic global search
  const q = store.state.globalSearch.trim().toLowerCase();
  if (q) {
    list = list.filter(b => 
      b.title.toLowerCase().includes(q) || 
      b.author.toLowerCase().includes(q) || 
      b.isbn.toLowerCase().includes(q)
    );
  }

  // Category filter dropdown
  if (filterCategory.value !== '所有类别') {
    list = list.filter(b => b.category === filterCategory.value);
  }

  // Active sub tab status selections
  if (statusFilter.value !== 'all') {
    list = list.filter(b => b.status === statusFilter.value);
  }

  return list;
});

// Paginated indices
const paginatedBooks = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return processedBooks.value.slice(start, start + itemsPerPage);
});

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(processedBooks.value.length / itemsPerPage));
});
</script>

<template>
  <div class="space-y-6">
    <!-- Page Header -->
    <div class="flex justify-between items-end">
      <div>
        <h2 class="text-3xl font-bold text-[#00236F] tracking-tight">图书目录</h2>
        <p class="text-sm text-[#444651] mt-1 text-opacity-80">管理您的馆藏资源与库存资产。</p>
      </div>
      <button 
        @click="openAddModal"
        class="bg-[#00236F] hover:bg-[#1E3A8A] text-white px-6 py-2.5 rounded-lg text-sm font-semibold flex items-center gap-2 transition-all shadow-sm active:scale-95 cursor-pointer"
      >
        <Plus class="h-4 w-4" />
        新增图书
      </button>
    </div>

    <!-- Filter Bento card layout -->
    <div class="bg-white p-5 rounded-xl border border-[#E0E3E5] shadow-sm grid grid-cols-1 md:grid-cols-4 gap-4 items-center">
      <div class="col-span-1 md:col-span-2">
        <label class="block text-xs font-semibold text-[#444651] mb-2 uppercase tracking-wide">快速搜索过滤</label>
        <div class="relative">
          <input 
            v-model="store.state.globalSearch"
            class="w-full h-10 border border-[#C5C5D3] rounded-lg px-3 focus:border-[#00236F] focus:ring-1 focus:ring-[#00236F] text-sm text-[#191C1E] outline-none" 
            placeholder="通过书名、作者或 ISBN 过滤..." 
          />
          <span class="absolute right-3 top-1/2 -translate-y-1/2 text-[#757682]">
            <Search class="h-4 w-4" />
          </span>
        </div>
      </div>

      <div>
        <label class="block text-xs font-semibold text-[#444651] mb-2 uppercase tracking-wide">类别</label>
        <select 
          v-model="filterCategory"
          class="w-full h-10 border border-[#C5C5D3] rounded-lg px-3 focus:border-[#00236F] focus:ring-1 focus:ring-[#00236F] text-sm text-[#191C1E] outline-none bg-transparent cursor-pointer"
        >
          <option>所有类别</option>
          <option>科学</option>
          <option>历史</option>
          <option>小说</option>
          <option>技术</option>
          <option>哲学</option>
        </select>
      </div>

      <div class="h-full flex flex-col justify-end pt-5 md:pt-0">
        <label class="block text-xs font-semibold text-[#444651] mb-2 uppercase tracking-wide hidden md:block">状态检索</label>
        <div class="bg-[#F2F4F6] p-1 rounded-lg flex gap-1 w-full">
          <button 
            @click="statusFilter = 'all'"
            :class="[
              'flex-1 py-1.5 text-xs font-bold rounded transition-colors cursor-pointer',
              statusFilter === 'all' ? 'bg-white text-[#191C1E] shadow-sm' : 'text-[#444651] hover:text-[#191C1E]'
            ]"
          >
            全部
          </button>
          <button 
            @click="statusFilter = 'available'"
            :class="[
              'flex-1 py-1.5 text-xs font-bold rounded transition-colors cursor-pointer',
              statusFilter === 'available' ? 'bg-white text-emerald-800 shadow-sm' : 'text-[#444651] hover:text-[#191C1E]'
            ]"
          >
            在馆
          </button>
          <button 
            @click="statusFilter = 'borrowed'"
            :class="[
              'flex-1 py-1.5 text-xs font-bold rounded transition-colors cursor-pointer',
              statusFilter === 'borrowed' ? 'bg-white text-blue-800 shadow-sm' : 'text-[#444651] hover:text-[#191C1E]'
            ]"
          >
            借出
          </button>
        </div>
      </div>
    </div>

    <!-- Main lists dashboard -->
    <div class="bg-white rounded-xl border border-[#E0E3E5] shadow-sm overflow-hidden text-[#191C1E]">
      <table class="w-full text-left border-collapse">
        <thead class="bg-[#F2F4F6] text-[#444651] font-semibold text-xs uppercase border-b border-[#E0E3E5]">
          <tr>
            <th class="px-6 py-4">封面</th>
            <th class="px-6 py-4">书名</th>
            <th class="px-6 py-4">作者</th>
            <th class="px-6 py-4">ISBN</th>
            <th class="px-6 py-4">类别</th>
            <th class="px-6 py-4">状态</th>
            <th class="px-6 py-4 text-center">操作</th>
          </tr>
        </thead>
        <tbody class="text-sm divide-y divide-[#E0E3E5]">
          <!-- Empty State -->
          <tr v-if="paginatedBooks.length === 0">
            <td colspan="7" class="text-center py-12 text-[#757682]">
              <Inbox class="h-10 w-10 mx-auto opacity-30 mb-2" />
              无可匹配的图书资源藏书
            </td>
          </tr>

          <!-- Dynamic Render -->
          <tr 
            v-for="(book, index) in paginatedBooks" 
            :key="book.id"
            class="hover:bg-[#ECEEF0] transition-colors"
            :class="index % 2 === 1 ? 'bg-[#F2F4F6]/20' : ''"
          >
            <td class="px-6 py-3">
              <img :src="book.cover" :alt="book.title" class="w-10 h-14 rounded-sm object-cover border border-[#C5C5D3] shadow-sm hover:scale-105 transition-all" />
            </td>
            <td class="px-6 py-4 font-bold text-[#00236F]">{{ book.title }}</td>
            <td class="px-6 py-4 text-[#191C1E] font-medium">{{ book.author }}</td>
            <td class="px-6 py-4 font-mono text-xs text-[#444651]">{{ book.isbn }}</td>
            <td class="px-6 py-4">
              <span class="bg-[#D5E3FC] text-[#57657A] px-2.5 py-1 rounded-full text-xs font-semibold">
                {{ book.category }}
              </span>
            </td>
            <td class="px-6 py-4">
              <span 
                v-if="book.status === 'available'"
                class="bg-emerald-100 text-emerald-800 px-2.5 py-1 rounded-full text-xs font-bold"
              >
                在馆
              </span>
              <span 
                v-else-if="book.status === 'borrowed'"
                class="bg-blue-100 text-blue-800 px-2.5 py-1 rounded-full text-xs font-bold"
              >
                已借出
              </span>
              <span 
                v-else
                class="bg-[#FFDAD6] text-[#BA1A1A] px-2.5 py-1 rounded-full text-xs font-bold"
              >
                已逾期
              </span>
            </td>
            <td class="px-6 py-4">
              <div class="flex justify-center items-center gap-2">
                <button 
                  @click="openEditModal(book)"
                  class="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-[#ECEEF0] text-[#444651] hover:text-[#00236F] transition-colors cursor-pointer"
                  title="编辑图书"
                >
                  <Edit3 class="h-4 w-4" />
                </button>
                <button 
                  @click="handleDeleteBook(book.id, book.title)"
                  class="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-[#FFDAD6] text-[#757682] hover:text-[#BA1A1A] transition-colors cursor-pointer"
                  title="注销馆藏"
                >
                  <Trash2 class="h-4 w-4" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Footer navigation -->
      <div v-if="totalPages > 1" class="px-6 py-3 bg-[#F2F4F6] border-t border-[#E0E3E5] flex items-center justify-between text-[#444651]">
        <span class="text-xs">显示第 {{ (currentPage - 1) * itemsPerPage + 1 }} 至 {{ Math.min(currentPage * itemsPerPage, processedBooks.length) }} 条，共 {{ processedBooks.length }} 条记录</span>
        <div class="flex items-center gap-1">
          <button 
            :disabled="currentPage === 1"
            @click="currentPage--"
            class="w-8 h-8 flex items-center justify-center rounded hover:bg-[#ECEEF0] text-[#444651] disabled:opacity-30 cursor-pointer"
          >
            <ChevronLeft class="h-4 w-4" />
          </button>
          <button 
            v-for="p in totalPages" 
            :key="p"
            @click="currentPage = p"
            :class="[
              'w-8 h-8 flex items-center justify-center rounded text-xs font-bold cursor-pointer',
              p === currentPage ? 'bg-[#00236F] text-white' : 'hover:bg-[#ECEEF0]'
            ]"
          >
            {{ p }}
          </button>
          <button 
            :disabled="currentPage === totalPages"
            @click="currentPage++"
            class="w-8 h-8 flex items-center justify-center rounded hover:bg-[#ECEEF0] text-[#444651] disabled:opacity-30 cursor-pointer"
          >
            <ChevronRight class="h-4 w-4" />
          </button>
        </div>
      </div>
    </div>

    <!-- Secondary Bento layer statistics bottom -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="bg-white p-5 rounded-xl border border-[#E0E3E5] flex items-center gap-4 hover:shadow-md transition-shadow">
        <div class="w-12 h-12 rounded-full bg-blue-50 flex items-center justify-center text-[#00236F]">
          <BookMarked class="h-6 w-6" />
        </div>
        <div>
          <p class="text-[#444651] text-xs font-semibold uppercase tracking-wider">馆藏总量</p>
          <h4 class="text-2xl font-bold text-[#191C1E]">{{ store.booksStats.value.total }}</h4>
        </div>
      </div>

      <div class="bg-white p-5 rounded-xl border border-[#E0E3E5] flex items-center gap-4 hover:shadow-md transition-shadow">
        <div class="w-12 h-12 rounded-full bg-emerald-50 flex items-center justify-center text-emerald-700">
          <CheckCircle class="h-6 w-6" />
        </div>
        <div>
          <p class="text-[#444651] text-xs font-semibold uppercase tracking-wider">当前在馆</p>
          <h4 class="text-2xl font-bold text-[#191C1E]">{{ store.booksStats.value.inLibrary }}</h4>
        </div>
      </div>

      <div class="bg-white p-5 rounded-xl border border-[#E0E3E5] flex items-center gap-4 hover:shadow-md transition-shadow animate-pulse">
        <div class="w-12 h-12 rounded-full bg-[#FFDAD6] flex items-center justify-center text-[#BA1A1A]">
          <AlertCircle class="h-6 w-6" />
        </div>
        <div>
          <p class="text-[#444651] text-xs font-semibold uppercase tracking-wider">逾期警示</p>
          <h4 class="text-2xl font-bold text-[#BA1A1A]">{{ store.booksStats.value.overdue }}</h4>
        </div>
      </div>
    </div>

    <!-- Floating Action terminal button context -->
    <button 
      @click="handlePrint"
      class="fixed bottom-6 right-6 w-14 h-14 bg-[#00236F] text-white rounded-full shadow-2xl flex items-center justify-center hover:scale-105 active:scale-95 transition-all z-50 group cursor-pointer"
    >
      <Printer class="h-6 w-6" />
      <span class="absolute right-full mr-4 bg-[#2D3133] text-white px-3 py-1.5 rounded text-xs whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity font-semibold">
        导出/打印目录
      </span>
    </button>

    <!-- Form modal additions / edits definition -->
    <div 
      v-if="showModal" 
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 transition-opacity p-4"
    >
      <div class="bg-white rounded-xl shadow-2xl border border-[#E0E3E5] max-w-md w-full overflow-hidden">
        <div class="px-6 py-4 border-b border-[#E0E3E5] bg-[#F2F4F6] flex items-center justify-between">
          <h3 class="font-bold text-[#00236F] text-lg">
            {{ isEditing ? '编辑图书编目信息' : '采编登记新藏书' }}
          </h3>
          <button @click="showModal = false" class="p-1 rounded-full hover:bg-[#E0E3E5] transition-colors cursor-pointer">
            <X class="h-5 w-5 text-[#757682]" />
          </button>
        </div>

        <form @submit.prevent="handleFormSubmit" class="p-6 space-y-4">
          <div class="space-y-1">
            <label class="text-xs font-semibold uppercase text-[#444651]">图书书名 / Title</label>
            <input 
              v-model="formTitle"
              type="text"
              required
              class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] focus:ring-1 focus:ring-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
              placeholder="例如: 量子力学原理"
            />
          </div>

          <div class="space-y-1">
            <label class="text-xs font-semibold uppercase text-[#444651]">主编作者 / Author</label>
            <input 
              v-model="formAuthor"
              type="text"
              required
              class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] focus:ring-1 focus:ring-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
              placeholder="例如: R. Shankar"
            />
          </div>

          <div class="space-y-1">
            <label class="text-xs font-semibold uppercase text-[#444651]">国际标准书号 / ISBN</label>
            <input 
              v-model="formIsbn"
              type="text"
              required
              class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] focus:ring-1 focus:ring-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
              placeholder="例如: 978-0306447907"
            />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1">
              <label class="text-xs font-semibold uppercase text-[#444651]">所属类别</label>
              <select 
                v-model="formCategory"
                class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] focus:ring-1 focus:ring-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
              >
                <option>科学</option>
                <option>历史</option>
                <option>小说</option>
                <option>技术</option>
                <option>哲学</option>
              </select>
            </div>

            <div class="space-y-1">
              <label class="text-xs font-semibold uppercase text-[#444651]">在库状态</label>
              <select 
                v-model="formStatus"
                class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] focus:ring-1 focus:ring-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
              >
                <option value="available">在馆</option>
                <option value="borrowed">已借出</option>
                <option value="overdue">已逾期</option>
              </select>
            </div>
          </div>

          <div class="pt-4 border-t border-[#E0E3E5] flex justify-end gap-3">
            <button 
              type="button" 
              @click="showModal = false"
              class="px-4 py-2 hover:bg-[#F2F4F6] text-[#757682] border border-[#C5C5D3] rounded-lg text-sm font-semibold cursor-pointer"
            >
              取消
            </button>
            <button 
              type="submit"
              class="px-4 py-2 bg-[#00236F] hover:bg-[#1E3A8A] text-white rounded-lg text-sm font-semibold shadow cursor-pointer"
            >
              提交采编
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
