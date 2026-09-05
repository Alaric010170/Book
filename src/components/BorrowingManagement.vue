<script setup lang="ts">
import { ref, computed } from 'vue';
import { useStore } from '../store';
import type { Borrowing } from '../types';
import { 
  AlertTriangle, 
  Plus, 
  Search, 
  Calendar, 
  Clock, 
  Scan, 
  Mail, 
  History, 
  RotateCcw, 
  X, 
  Check, 
  Sparkles,
  Inbox
} from 'lucide-vue-next';

const store = useStore();

const activeSubTab = ref<'active' | 'history' | 'overdue'>('active');
const showModal = ref(false);

const formReaderId = ref('');
const formBookId = ref('');
const formBorrowDuration = ref(14); // default 14 days

// Options Derived from store collections
const readersList = computed(() => store.state.readers);
const availableBooks = computed(() => store.state.books.filter(b => b.status === 'available'));

// Register lending transaction
const handleNewBorrowingSubmit = async () => {
  if (!formReaderId.value || !formBookId.value) return;

  const reader = store.state.readers.find(r => r.id === formReaderId.value);
  const book = store.state.books.find(b => b.id === formBookId.value);

  if (!reader || !book) return;

  const today = new Date();
  const formatOptions: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'long', day: 'numeric' };
  
  const borrowDateStr = today.toLocaleDateString('zh-CN', formatOptions);
  
  const dueDateObj = new Date();
  dueDateObj.setDate(today.getDate() + Number(formBorrowDuration.value));
  const dueDateStr = dueDateObj.toLocaleDateString('zh-CN', formatOptions);

  // Initials
  const initials = reader.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();

  await store.createBorrowing({
    readerId: reader.id,
    readerName: reader.name,
    readerInitials: initials,
    bookTitle: book.title,
    borrowDate: borrowDateStr,
    dueDate: dueDateStr,
    status: 'normal',
  });

  // Close
  showModal.value = false;
};

// Dispatch overdue reminders
const triggerOverdueNotifications = () => {
  alert('系统正在群发邮件和短信提醒。');
};

const handleReturnBook = async (borrowing: Borrowing) => {
  if (confirm(`确认归还书籍 "${borrowing.bookTitle}" 吗？`)) {
    await store.returnBorrowing(borrowing.id);
  }
};

const handleRenewBook = async (borrowingId: string) => {
  await store.renewBorrowing(borrowingId);
  alert('续借成功：到期期限已向后顺延 14 天。');
};

const handleActionCard = (tech: string) => {
  alert(`触发借阅功能模块: ${tech}`);
};

// Search & SubTab filtering
const filteredBorrowings = computed(() => {
  let list = [...store.state.borrowings];

  // Active subtab filter
  if (activeSubTab.value === 'overdue') {
    list = list.filter(b => b.status === 'overdue');
  }

  // Search match query (From common query bar)
  const q = store.state.globalSearch.trim().toLowerCase();
  if (q) {
    list = list.filter(b => 
      b.readerName.toLowerCase().includes(q) || 
      b.readerId.toLowerCase().includes(q) || 
      b.bookTitle.toLowerCase().includes(q)
    );
  }

  return list;
});
</script>

<template>
  <div class="space-y-6 text-[#191C1E]">
    <!-- Page Header -->
    <div class="flex justify-between items-end">
      <div>
        <h2 class="text-3xl font-bold text-[#00236F] tracking-tight">借阅管理</h2>
        <p class="text-sm text-[#444651] mt-1 text-opacity-80">追踪流通情况，管理还书，并监控逾期资产。</p>
      </div>
      <button 
        @click="showModal = true"
        class="bg-[#00236F] hover:bg-[#1E3A8A] text-white px-6 py-2.5 rounded-lg text-sm font-semibold flex items-center gap-2 transition-all shadow-sm active:scale-95 cursor-pointer"
      >
        <Plus class="h-4 w-4" />
        新建借阅
      </button>
    </div>

    <!-- Active warnings banner section (Bento Layer) -->
    <section class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="lg:col-span-2 bg-[#FFDAD6]/35 border border-[#FFDAD6] rounded-xl p-6 flex flex-col md:flex-row justify-between gap-4 relative overflow-hidden">
        <div class="space-y-3 relative z-10 flex-1">
          <div class="flex items-center gap-2 text-[#BA1A1A]">
            <AlertTriangle class="h-5 w-5" />
            <span class="text-xs font-bold uppercase tracking-wider">业务警示</span>
          </div>
          <h3 class="text-2xl font-bold text-[#93000A]">{{ store.borrowingsStats.value.overdue }} 项逾期未还待处理</h3>
          
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
            <div class="bg-white/80 p-3 rounded-lg border border-[#FFDAD6]/60">
              <p class="text-xs text-[#757682] font-semibold">逾期最久书籍</p>
              <p class="text-sm font-bold text-[#BA1A1A]">了不起的盖茨比 (42 天)</p>
            </div>
            <div class="bg-white/80 p-3 rounded-lg border border-[#FFDAD6]/60">
              <p class="text-xs text-[#757682] font-semibold">关键违约读者</p>
              <p class="text-sm font-bold text-[#BA1A1A]">Sarah Jenkins (3 本)</p>
            </div>
          </div>
        </div>

        <div class="flex md:flex-col justify-end items-end relative z-10">
          <button 
            @click="triggerOverdueNotifications"
            class="bg-[#BA1A1A] hover:bg-[#93000A] text-white px-4 py-2.5 rounded-lg text-xs font-bold shadow-sm transition-all focus:ring-2 focus:ring-[#BA1A1A]/35 cursor-pointer"
          >
            群发催还通知
          </button>
        </div>
      </div>

      <!-- Live statistics progress indicator -->
      <div class="bg-white border border-[#E0E3E5] rounded-xl p-6 flex flex-col justify-center">
        <span class="text-xs font-bold text-[#757682] uppercase tracking-wider block mb-1">今日流还统计</span>
        <div class="flex items-baseline gap-2">
          <span class="text-4xl font-extrabold text-[#00236F] leading-none">{{ store.borrowingsStats.value.dueToday }}</span>
          <span class="text-sm text-[#444651] font-semibold">本待期归还</span>
        </div>
        <div class="mt-4 w-full bg-[#ECEEF0] rounded-full h-2 overflow-hidden">
          <div class="bg-[#00236F] h-full rounded-full" :style="{ width: store.borrowingsStats.value.total > 0 ? Math.round((store.borrowingsStats.value.total - store.borrowingsStats.value.dueToday) / store.borrowingsStats.value.total * 100) + '%' : '0%' }"></div>
        </div>
        <p class="mt-2 text-xs text-[#757682] tracking-wide font-medium italic">完成归还进度：{{ store.borrowingsStats.value.total - store.borrowingsStats.value.dueToday }} / {{ store.borrowingsStats.value.total }}</p>
      </div>
    </section>

    <!-- Sub Navigation Tab elements -->
    <div class="bg-[#F2F4F6] p-1 rounded-xl inline-flex gap-1 border border-[#E0E3E5]">
      <button 
        @click="activeSubTab = 'active'"
        :class="[
          'px-6 py-2 rounded-lg text-sm font-semibold transition-all cursor-pointer',
          activeSubTab === 'active' ? 'bg-white text-[#00236F] shadow-sm' : 'text-[#444651] hover:text-[#00236F]'
        ]"
      >
        当前借阅
      </button>
      <button 
        @click="activeSubTab = 'history'"
        :class="[
          'px-6 py-2 rounded-lg text-sm font-semibold transition-all cursor-pointer',
          activeSubTab === 'history' ? 'bg-white text-[#00236F] shadow-sm' : 'text-[#444651] hover:text-[#00236F]'
        ]"
      >
        归还历史
      </button>
      <button 
        @click="activeSubTab = 'overdue'"
        :class="[
          'px-6 py-2 rounded-lg text-sm font-semibold transition-all cursor-pointer',
          activeSubTab === 'overdue' ? 'bg-white text-[#00236F] shadow-sm' : 'text-[#444651] hover:text-[#00236F]'
        ]"
      >
        逾期列表
      </button>
    </div>

    <!-- Active Details table segment -->
    <div class="bg-white border border-[#E0E3E5] rounded-xl overflow-hidden shadow-sm">
      <div class="p-5 border-b border-[#E0E3E5] flex items-center justify-between">
        <h4 class="text-lg font-bold text-[#191C1E]">当前借阅明细数据</h4>
        <div class="text-xs font-semibold text-[#57657A] flex items-center gap-1">
          <span>共 {{ filteredBorrowings.length }} 项交易记录</span>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full text-left">
          <thead class="bg-[#F2F4F6] border-b border-[#E0E3E5]">
            <tr>
              <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-[#444651]">读者姓名 / 标识</th>
              <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-[#444651]">借记图书</th>
              <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-[#444651]">借出日期</th>
              <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-[#444651]">应还期限</th>
              <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-[#444651]">在途状态</th>
              <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-[#444651] text-right">流通操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-[#E0E3E5]">
            <!-- Empty state -->
            <tr v-if="filteredBorrowings.length === 0">
              <td colspan="6" class="text-center py-12 text-[#757682]">
                <Inbox class="h-10 w-10 mx-auto opacity-30 mb-2" />
                查无相关的在途流通记录
              </td>
            </tr>

            <!-- Loops -->
            <tr 
              v-for="b in filteredBorrowings" 
              :key="b.id"
              class="hover:bg-[#ECEEF0] transition-colors"
            >
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <div class="w-8 h-8 rounded-full bg-[#D5E3FC] flex items-center justify-center text-[#0d1c2e] font-extrabold text-xs">
                    {{ b.readerInitials }}
                  </div>
                  <div>
                    <span class="text-sm font-semibold block text-[#191C1E]">{{ b.readerName }}</span>
                    <span class="text-[10px] text-[#757682] font-mono whitespace-nowrap">{{ b.readerId }}</span>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 text-sm font-semibold text-[#191C1E]">{{ b.bookTitle }}</td>
              <td class="px-6 py-4 text-sm text-[#444651]">{{ b.borrowDate }}</td>
              <td class="px-6 py-4 text-sm" :class="b.status === 'overdue' ? 'text-[#BA1A1A] font-semibold' : 'text-[#444651]'">
                {{ b.dueDate }}
              </td>
              <td class="px-6 py-4">
                <span 
                  v-if="b.status === 'normal'"
                  class="bg-emerald-50 text-emerald-800 border border-emerald-100 px-2.5 py-0.5 rounded-full text-xs font-semibold"
                >
                  正常
                </span>
                <span 
                  v-else-if="b.status === 'overdue'"
                  class="bg-red-50 text-red-800 border border-red-100 px-2.5 py-0.5 rounded-full text-xs font-semibold animate-pulse"
                >
                  已逾期 ({{ b.overdueDays || 5 }}天)
                </span>
                <span 
                  v-else-if="b.status === 'due_today'"
                  class="bg-blue-50 text-[#00236F] border border-blue-100 px-2.5 py-0.5 rounded-full text-xs font-semibold"
                >
                  今日到期
                </span>
              </td>
              <td class="px-6 py-4 text-right space-x-4">
                <button 
                  @click="handleReturnBook(b)"
                  class="text-xs font-bold text-[#00236F] hover:underline cursor-pointer"
                >
                  还书
                </button>
                <button 
                  v-if="b.status !== 'overdue'"
                  @click="handleRenewBook(b.id)"
                  class="text-xs font-bold text-[#757682] hover:text-[#00236F] hover:underline cursor-pointer"
                >
                  续借
                </button>
                <button 
                  v-else
                  @click="triggerOverdueNotifications"
                  class="text-xs font-bold text-[#BA1A1A] hover:underline cursor-pointer"
                >
                  通知
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Quick action cards panel grids (Bento secondary Layer) -->
    <section class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div 
        @click="handleActionCard('扫描条码')"
        class="bg-white p-5 rounded-xl border border-[#E0E3E5] hover:border-[#00236F] transition-colors cursor-pointer group shadow-sm"
      >
        <div class="w-10 h-10 rounded-lg bg-[#ECEEF0] flex items-center justify-center mb-4 group-hover:bg-[#D5E3FC] group-hover:text-[#00236F] transition-colors text-[#57657A]">
          <Scan class="h-5 w-5" />
        </div>
        <h5 class="text-sm font-bold text-[#191C1E] mb-1">扫描物理条码</h5>
        <p class="text-xs text-[#757682] leading-relaxed">通过扫描物理资产配附条码，极速登记图书出库开单及流转闭环。</p>
      </div>

      <div 
        @click="handleActionCard('查看通知')"
        class="bg-white p-5 rounded-xl border border-[#E0E3E5] hover:border-[#00236F] transition-colors cursor-pointer group shadow-sm"
      >
        <div class="w-10 h-10 rounded-lg bg-[#ECEEF0] flex items-center justify-center mb-4 group-hover:bg-[#D5E3FC] group-hover:text-[#00236F] transition-colors text-[#57657A]">
          <Mail class="h-5 w-5" />
        </div>
        <h5 class="text-sm font-bold text-[#191C1E] mb-1">查看排期通知</h5>
        <p class="text-xs text-[#757682] leading-relaxed">查询系统后台催还、通知提醒历史短信与电邮投递队列表单详情。</p>
      </div>

      <div 
        @click="handleActionCard('批量历史')"
        class="bg-white p-5 rounded-xl border border-[#E0E3E5] hover:border-[#00236F] transition-colors cursor-pointer group shadow-sm"
      >
        <div class="w-10 h-10 rounded-lg bg-[#ECEEF0] flex items-center justify-center mb-4 group-hover:bg-[#D5E3FC] group-hover:text-[#00236F] transition-colors text-[#57657A]">
          <History class="h-5 w-5" />
        </div>
        <h5 class="text-sm font-bold text-[#191C1E] mb-1">批量归还历史</h5>
        <p class="text-xs text-[#757682] leading-relaxed">备份导出阶段性的清册、归集上报图书借还统计数据报表清单。</p>
      </div>

      <div 
        @click="handleActionCard('遗失处理')"
        class="bg-white p-5 rounded-xl border border-[#E0E3E5] hover:border-[#00236F] transition-colors cursor-pointer group shadow-sm"
      >
        <div class="w-10 h-10 rounded-lg bg-[#ECEEF0] flex items-center justify-center mb-4 group-hover:bg-[#D5E3FC] group-hover:text-[#00236F] transition-colors text-[#57657A]">
          <RotateCcw class="h-5 w-5" />
        </div>
        <h5 class="text-sm font-bold text-[#191C1E] mb-1">遗失理赔损毁声明</h5>
        <p class="text-xs text-[#757682] leading-relaxed">将长期逾期的死账藏书进行“折损注销”、“挂失赔偿”系统作废归档。</p>
      </div>
    </section>

    <!-- Create Borrow lending transaction Modal dialog -->
    <div 
      v-if="showModal" 
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 transition-opacity p-4"
    >
      <div class="bg-white rounded-xl shadow-2xl border border-[#E0E3E5] max-w-md w-full overflow-hidden text-[#191C1E]">
        <div class="px-6 py-4 border-b border-[#E0E3E5] bg-[#F2F4F6] flex items-center justify-between">
          <h3 class="font-bold text-[#00236F] text-lg flex items-center gap-2">
            <Sparkles class="h-5 w-5" />
            新建图书借阅在途登记
          </h3>
          <button @click="showModal = false" class="p-1 rounded-full hover:bg-[#E0E3E5] transition-colors cursor-pointer">
            <X class="h-5 w-5 text-[#757682]" />
          </button>
        </div>

        <form @submit.prevent="handleNewBorrowingSubmit" class="p-6 space-y-4">
          <!-- Reader select dropdown -->
          <div class="space-y-1">
            <label class="text-xs font-semibold uppercase text-[#444651]">选择借阅读者</label>
            <select 
              v-model="formReaderId"
              required
              class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
            >
              <option disabled value="">-- 请选择当前登记读者 --</option>
              <option v-for="r in readersList" :key="r.id" :value="r.id">
                [{{ r.id }}] - {{ r.name }} ({{ r.level === 'premium' ? '高级会员' : '普通会员' }})
              </option>
            </select>
          </div>

          <!-- Books select dropdown -->
          <div class="space-y-1">
            <label class="text-xs font-semibold uppercase text-[#444651]">选择配借在馆图书</label>
            <select 
              v-model="formBookId"
              required
              class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
            >
              <option disabled value="">-- 请选择在馆空闲流通图书 --</option>
              <option v-for="b in availableBooks" :key="b.id" :value="b.id">
                {{ b.title }} - ISBN: {{ b.isbn }} ({{ b.category }})
              </option>
              <option v-if="availableBooks.length === 0" disabled value="">(无空闲图书，可去图书目录新增在馆图书)</option>
            </select>
          </div>

          <!-- Duration lending limit -->
          <div class="space-y-1">
            <label class="text-xs font-semibold uppercase text-[#444651]">借期天数限制 / Limit</label>
            <select 
              v-model="formBorrowDuration"
              class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
            >
              <option :value="14">14 天 (普通标准借期)</option>
              <option :value="30">30 天 (高级学术宽期)</option>
              <option :value="7">7 天 (流媒体短线快阅)</option>
            </select>
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
              :disabled="availableBooks.length === 0"
              class="px-4 py-2 bg-[#00236F] hover:bg-[#1E3A8A] text-white rounded-lg text-sm font-semibold shadow disabled:opacity-50 cursor-pointer"
            >
              确认出库登记
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
