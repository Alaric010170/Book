<script setup lang="ts">
import { ref, computed } from 'vue';
import { useStore } from '../store';
import type { Reader } from '../types';
import { 
  Plus, 
  Users, 
  Star, 
  BookOpen, 
  TrendingUp, 
  Inbox, 
  Trash2, 
  Edit3, 
  ChevronLeft, 
  ChevronRight, 
  Eye, 
  Download, 
  Info,
  X 
} from 'lucide-vue-next';

const store = useStore();

// UI Filters State
const filterType = ref('所有会员类型');
const sortBy = ref('排序方式：最新');
const currentPage = ref(1);
const itemsPerPage = 5;

// Modal States for Add / Edit
const showModal = ref(false);
const isEditing = ref(false);
const selectedReaderId = ref<string | null>(null);

const formName = ref('');
const formEmail = ref('');
const formLevel = ref<'premium' | 'regular'>('regular');

// Open Modal to Create
const openAddModal = () => {
  isEditing.value = false;
  selectedReaderId.value = null;
  formName.value = '';
  formEmail.value = '';
  formLevel.value = 'regular';
  showModal.value = true;
};

// Open Modal to Edit
const openEditModal = (reader: Reader) => {
  isEditing.value = true;
  selectedReaderId.value = reader.id;
  formName.value = reader.name;
  formEmail.value = reader.email;
  formLevel.value = reader.level;
  showModal.value = true;
};

// Handle Form Submit
const handleFormSubmit = async () => {
  if (!formName.value || !formEmail.value) return;

  if (isEditing.value && selectedReaderId.value) {
    await store.updateReader(selectedReaderId.value, {
      name: formName.value,
      email: formEmail.value,
      level: formLevel.value,
    });
  } else {
    const randomSeed = Math.floor(Math.random() * 100);
    const avatarUrl = `https://images.unsplash.com/photo-${1500000000000 + randomSeed * 1000}?w=150&h=150&fit=crop&crop=faces`;
    await store.addReader({
      name: formName.value,
      email: formEmail.value,
      level: formLevel.value,
      borrows: 0,
      avatar: avatarUrl,
    });
  }
  showModal.value = false;
};

// Actions
const handleDeleteReader = async (id: string) => {
  if (confirm(`确认注销或删除读者 ${id} 吗？`)) {
    await store.deleteReader(id);
  }
};

const handleExport = () => {
  alert('读者名录正在导出：已生成 CSV / PDF 格式备份。');
};

// Processing Filters & Searches
const processedReaders = computed(() => {
  let list = [...store.state.readers];

  // Global search match (from Topbar input)
  const q = store.state.globalSearch.trim().toLowerCase();
  if (q) {
    list = list.filter(r => 
      r.name.toLowerCase().includes(q) || 
      r.email.toLowerCase().includes(q) || 
      r.id.toLowerCase().includes(q)
    );
  }

  // Dropdown Filter type match
  if (filterType.value === '普通会员') {
    list = list.filter(r => r.level === 'regular');
  } else if (filterType.value === '高级会员') {
    list = list.filter(r => r.level === 'premium');
  }

  // Sorting logics
  if (sortBy.value === '排序方式：最新') {
    // by ID descending or newly added on top
    list.sort((a, b) => b.id.localeCompare(a.id));
  } else if (sortBy.value === '排序方式：最高借阅') {
    list.sort((a, b) => b.borrows - a.borrows);
  } else if (sortBy.value === '排序方式：ID 升序') {
    list.sort((a, b) => a.id.localeCompare(b.id));
  }

  return list;
});

// Pagination Calculations
const paginatedReaders = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return processedReaders.value.slice(start, start + itemsPerPage);
});

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(processedReaders.value.length / itemsPerPage));
});

const setPage = (num: number) => {
  currentPage.value = num;
};
</script>

<template>
  <div class="space-y-6">
    <!-- Page Header -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h2 class="text-3xl font-bold text-[#00236F] tracking-tight">读者管理</h2>
        <p class="text-sm text-[#444651] mt-1 text-opacity-80">监控、更新及管理所有图书馆读者及其会员凭证。</p>
      </div>
      <button 
        @click="openAddModal"
        class="flex items-center gap-2 bg-[#00236F] text-white px-6 py-3 rounded-lg text-sm font-semibold hover:opacity-90 transition-all shadow-sm active:scale-95 cursor-pointer"
      >
        <Plus class="h-4 w-4" />
        新增读者
      </button>
    </div>

    <!-- Stats Bento grid derived dynamically from actual readers list -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div class="bg-white p-5 border border-[#E0E3E5] rounded-xl shadow-sm hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-2">
          <span class="text-[#444651] text-xs font-semibold uppercase tracking-wider">读者总数</span>
          <Users class="h-5 w-5 text-[#00236F]" />
        </div>
        <p class="text-4xl font-extrabold text-[#00236F] leading-none">{{ store.readersStats.value.total }}</p>
        <p class="text-emerald-700 text-xs mt-3 flex items-center gap-1 font-semibold">
          <TrendingUp class="h-3 w-3" /> 较上月增长 12%
        </p>
      </div>

      <div class="bg-white p-5 border border-[#E0E3E5] rounded-xl shadow-sm hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-2">
          <span class="text-[#444651] text-xs font-semibold uppercase tracking-wider">高级会员</span>
          <Star class="h-5 w-5 text-[#36B6FB] fill-[#36B6FB]" />
        </div>
        <p class="text-4xl font-extrabold text-[#191C1E] leading-none">{{ store.readersStats.value.premium }}</p>
        <p class="text-[#444651] text-xs mt-3">占总读者人数的 {{ store.readersStats.value.premiumRatio }}%</p>
      </div>

      <div class="bg-white p-5 border border-[#E0E3E5] rounded-xl shadow-sm hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-2">
          <span class="text-[#444651] text-xs font-semibold uppercase tracking-wider">活跃借阅</span>
          <BookOpen class="h-5 w-5 text-[#515F74]" />
        </div>
        <p class="text-4xl font-extrabold text-[#191C1E] leading-none">{{ store.borrowingsStats.value.total }}</p>
        <p class="text-[#444651] text-xs mt-3">当前在途借阅</p>
      </div>

      <div class="bg-white p-5 border border-[#E0E3E5] rounded-xl shadow-sm hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-2">
          <span class="text-[#444651] text-xs font-semibold uppercase tracking-wider">逾期借阅</span>
          <Plus class="h-5 w-5 text-[#BA1A1A]" />
        </div>
        <p class="text-4xl font-extrabold text-[#191C1E] leading-none">{{ store.borrowingsStats.value.overdue }}</p>
        <p class="text-[#444651] text-xs mt-3">等待催还处理</p>
      </div>
    </div>

    <!-- Filters and Table container board -->
    <div class="bg-white border border-[#E0E3E5] rounded-xl shadow-sm overflow-hidden">
      <!-- Controls Panel -->
      <div class="p-4 border-b border-[#E0E3E5] bg-[#F2F4F6] flex flex-col lg:flex-row lg:items-center justify-between gap-4">
        <div class="flex flex-wrap items-center gap-4">
          <!-- Member types -->
          <div class="relative">
            <select 
              v-model="filterType"
              class="pl-3 pr-8 py-2 bg-white border border-[#C5C5D3] rounded-lg text-sm text-[#191C1E] focus:outline-none focus:border-[#00236F] appearance-none cursor-pointer"
            >
              <option>所有会员类型</option>
              <option>普通会员</option>
              <option>高级会员</option>
            </select>
          </div>
          
          <!-- Sorter -->
          <div class="relative">
            <select 
              v-model="sortBy"
              class="pl-3 pr-8 py-2 bg-white border border-[#C5C5D3] rounded-lg text-sm text-[#191C1E] focus:outline-none focus:border-[#00236F] appearance-none cursor-pointer"
            >
              <option>排序方式：最新</option>
              <option>排序方式：最高借阅</option>
              <option>排序方式：ID 升序</option>
            </select>
          </div>
        </div>

        <!-- Inline Pagination Header status -->
        <div class="flex items-center gap-4 text-xs font-semibold text-[#444651]">
          <span>显示自 {{ processedReaders.length === 0 ? 0 : (currentPage - 1) * itemsPerPage + 1 }} 至 {{ Math.min(currentPage * itemsPerPage, processedReaders.length) }} 条，共 {{ processedReaders.length }} 名读者</span>
          <div class="flex border border-[#C5C5D3] rounded overflow-hidden">
            <button 
              :disabled="currentPage === 1"
              @click="currentPage--"
              class="p-1 hover:bg-[#ECEEF0] transition-colors disabled:opacity-30 cursor-pointer"
            >
              <ChevronLeft class="h-4 w-4" />
            </button>
            <button 
              :disabled="currentPage === totalPages"
              @click="currentPage++"
              class="p-1 hover:bg-[#ECEEF0] transition-colors border-l border-[#C5C5D3] disabled:opacity-30 cursor-pointer"
            >
              <ChevronRight class="h-4 w-4" />
            </button>
          </div>
        </div>
      </div>

      <!-- Member Lists Table -->
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-[#F2F4F6] border-b border-[#E0E3E5]">
              <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-[#444651]">读者ID</th>
              <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-[#444651]">头像</th>
              <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-[#444651]">姓名</th>
              <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-[#444651]">电子邮箱</th>
              <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-[#444651]">会员级别</th>
              <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-[#444651] text-center">累计借阅</th>
              <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-[#444651] text-center">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-[#E0E3E5]">
            <!-- Empty state check -->
            <tr v-if="paginatedReaders.length === 0">
              <td colspan="7" class="text-center py-12 text-[#757682]">
                <Inbox class="h-10 w-10 mx-auto opacity-30 mb-2" />
                没有找到符合筛选条件的读者
              </td>
            </tr>

            <!-- Render Rows -->
            <tr 
              v-for="(reader, index) in paginatedReaders" 
              :key="reader.id"
              class="hover:bg-[#ECEEF0] transition-colors cursor-default"
              :class="index % 2 === 1 ? 'bg-[#F2F4F6]/20' : ''"
            >
              <td class="px-6 py-4 text-xs font-mono text-[#191C1E] font-medium">{{ reader.id }}</td>
              <td class="px-6 py-4">
                <img :src="reader.avatar" :alt="reader.name" class="w-10 h-10 rounded-full border border-[#C5C5D3] object-cover" />
              </td>
              <td class="px-6 py-4 text-sm font-semibold text-[#191C1E]">{{ reader.name }}</td>
              <td class="px-6 py-4 text-sm text-[#444651]">{{ reader.email }}</td>
              <td class="px-6 py-4">
                <span 
                  v-if="reader.level === 'premium'"
                  class="inline-flex items-center gap-1 px-2 py-0.5 bg-[#C9E6FF] text-[#004C6E] rounded text-xs font-bold"
                >
                  <Star class="h-3 w-3 fill-[#004C6E]" />
                  高级会员
                </span>
                <span 
                  v-else
                  class="inline-flex items-center px-2 py-0.5 bg-[#D5E3FC] text-[#57657A] rounded text-xs font-bold"
                >
                  普通会员
                </span>
              </td>
              <td class="px-6 py-4 text-sm font-bold text-[#191C1E] text-center">{{ reader.borrows }}</td>
              <td class="px-6 py-4">
                <div class="flex items-center justify-center gap-2">
                  <button 
                    @click="openEditModal(reader)"
                    class="p-2 text-[#757682] hover:text-[#00236F] transition-colors cursor-pointer"
                    title="编辑资料"
                  >
                    <Edit3 class="h-4.5 w-4.5" />
                  </button>
                  <button 
                    @click="handleDeleteReader(reader.id)"
                    class="p-2 text-[#757682] hover:text-[#BA1A1A] transition-colors cursor-pointer"
                    title="删除读者"
                  >
                    <Trash2 class="h-4.5 w-4.5" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination Panel -->
      <div v-if="totalPages > 1" class="p-4 bg-[#F2F4F6] border-t border-[#E0E3E5] flex items-center justify-center gap-2">
        <button 
          :disabled="currentPage === 1"
          @click="currentPage = 1"
          class="w-8 h-8 rounded flex items-center justify-center border border-[#C5C5D3] enabled:hover:bg-[#ECEEF0] transition-colors text-[#57657A] disabled:opacity-30 cursor-pointer"
        >
          &laquo;
        </button>
        <button 
          v-for="page in totalPages" 
          :key="page"
          @click="setPage(page)"
          :class="[
            'w-8 h-8 rounded text-sm font-semibold transition-all cursor-pointer',
            page === currentPage 
              ? 'bg-[#00236F] text-white shadow' 
              : 'border border-[#C5C5D3] text-[#191C1E] hover:bg-[#ECEEF0]'
          ]"
        >
          {{ page }}
        </button>
        <button 
          :disabled="currentPage === totalPages"
          @click="currentPage = totalPages"
          class="w-8 h-8 rounded flex items-center justify-center border border-[#C5C5D3] enabled:hover:bg-[#ECEEF0] transition-colors text-[#57657A] disabled:opacity-30 cursor-pointer"
        >
          &raquo;
        </button>
      </div>
    </div>

    <!-- Help & Policies Block section -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 pt-4">
      <div class="bg-blue-50/40 p-6 rounded-xl border border-blue-100 flex items-start gap-4">
        <div class="p-3 bg-blue-50 rounded-lg text-[#00236F]">
          <Info class="h-6 w-6" />
        </div>
        <div>
          <h4 class="font-bold text-[#00236F] text-base">会员借阅最高限额政策</h4>
          <p class="text-sm text-[#444651] mt-2 leading-relaxed">
            高级会员最多可同时借阅 10 本图书并自由访问多媒体数字档案。普通会员每次限借 3 本纸质藏书，借期均为 14 天。
          </p>
        </div>
      </div>

      <div class="bg-white p-6 rounded-xl border border-[#E0E3E5] flex items-center justify-between shadow-sm">
        <div class="flex items-center gap-4">
          <div class="p-3 rounded-xl bg-[#D5E3FC] text-[#00236F]">
            <Download class="h-6 w-6" />
          </div>
          <div>
            <h4 class="font-bold text-[#191C1E] text-base">系统读者名录备份</h4>
            <p class="text-xs text-[#757682] mt-1">支持全站读者信息同步归档</p>
          </div>
        </div>
        <button 
          @click="handleExport"
          class="px-5 py-2.5 bg-white border border-[#757682] hover:bg-[#F2F4F6] text-[#191C1E] rounded-lg font-semibold text-sm transition-colors cursor-pointer"
        >
          导出名录
        </button>
      </div>
    </div>

    <!-- Add / Edit Modal Dialouge -->
    <div 
      v-if="showModal" 
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 transition-opacity p-4"
    >
      <div class="bg-white rounded-xl shadow-2xl border border-[#E0E3E5] max-w-md w-full overflow-hidden">
        <div class="px-6 py-4 border-b border-[#E0E3E5] bg-[#F2F4F6] flex items-center justify-between">
          <h3 class="font-bold text-[#00236F] text-lg">
            {{ isEditing ? '编辑读者账户信息' : '创建新读者账户凭证' }}
          </h3>
          <button @click="showModal = false" class="p-1 rounded-full hover:bg-[#E0E3E5] transition-colors cursor-pointer">
            <X class="h-5 w-5 text-[#757682]" />
          </button>
        </div>

        <form @submit.prevent="handleFormSubmit" class="p-6 space-y-4">
          <div class="space-y-1">
            <label class="text-xs font-semibold uppercase text-[#444651]">全名 / Full Name</label>
            <input 
              v-model="formName"
              type="text"
              required
              class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] focus:ring-1 focus:ring-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
              placeholder="例如: Julian Thorne"
            />
          </div>

          <div class="space-y-1">
            <label class="text-xs font-semibold uppercase text-[#444651]">电子邮箱 / Email Address</label>
            <input 
              v-model="formEmail"
              type="email"
              required
              class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] focus:ring-1 focus:ring-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
              placeholder="例如: j.thorne@university.edu"
            />
          </div>

          <div class="space-y-1">
            <label class="text-xs font-semibold uppercase text-[#444651] block mb-1">会员等级 / Status</label>
            <div class="flex gap-4">
              <label class="flex items-center gap-1 text-sm font-semibold text-[#191C1E] cursor-pointer">
                <input 
                  type="radio" 
                  value="regular"
                  v-model="formLevel"
                  class="text-[#00236F] focus:ring-[#00236F]"
                />
                普通会员
              </label>

              <label class="flex items-center gap-1 text-sm font-semibold text-[#191C1E] cursor-pointer">
                <input 
                  type="radio" 
                  value="premium"
                  v-model="formLevel"
                  class="text-[#00236F] focus:ring-[#00236F]"
                />
                高级会员
              </label>
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
              保存资料
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
