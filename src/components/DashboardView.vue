<script setup lang="ts">
import { ref } from 'vue';
import { useStore } from '../store';
import { 
  Building, 
  BookMarked, 
  Users, 
  RefreshCw, 
  AlertCircle, 
  ChevronRight, 
  PlusCircle, 
  UserPlus, 
  Settings, 
  HelpCircle,
  TrendingUp 
} from 'lucide-vue-next';

const store = useStore();

const selectShortcut = (tab: 'books' | 'borrowing' | 'readers' | 'help') => {
  store.setTab(tab);
};
</script>

<template>
  <div class="space-y-6 text-[#191C1E]">
    <!-- Welcome Header -->
    <div class="bg-gradient-to-r from-[#00236F] to-[#1E3A8A] rounded-xl p-6 text-white shadow-sm flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
      <div>
        <h2 class="text-2xl font-bold tracking-tight">下午好，图书管理员！</h2>
        <p class="text-sm opacity-80 mt-1">
          追求学术卓越。欢迎返回图书馆数字化管理系统集成看板。
        </p>
      </div>
      <div class="px-4 py-2 bg-white/10 backdrop-blur-sm rounded-lg border border-white/20 text-xs font-semibold uppercase tracking-wider">
        当前日期：2026年6月21日
      </div>
    </div>

    <!-- Stats Grid Bento Board -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div 
        @click="selectShortcut('books')"
        class="bg-white p-5 border border-[#E0E3E5] rounded-xl hover:border-[#00236F] cursor-pointer transition-all shadow-sm flex items-center justify-between group"
      >
        <div class="space-y-1">
          <p class="text-xs text-[#757682] font-semibold uppercase tracking-wider">全馆藏书量</p>
          <p class="text-3xl font-extrabold text-[#191C1E] group-hover:text-[#00236F] transition-colors">
            {{ store.booksStats.value.total }}
          </p>
          <span class="text-[10px] text-emerald-700 font-bold flex items-center gap-0.5">
            <TrendingUp class="h-3 w-3" /> 较昨日新增 8 本
          </span>
        </div>
        <div class="w-10 h-10 bg-[#D5E3FC]/40 text-[#00236F] rounded-lg flex items-center justify-center">
          <BookMarked class="h-5 w-5" />
        </div>
      </div>

      <div 
        @click="selectShortcut('readers')"
        class="bg-white p-5 border border-[#E0E3E5] rounded-xl hover:border-[#00236F] cursor-pointer transition-all shadow-sm flex items-center justify-between group"
      >
        <div class="space-y-1">
          <p class="text-xs text-[#757682] font-semibold uppercase tracking-wider">累计登记读者</p>
          <p class="text-3xl font-extrabold text-[#191C1E] group-hover:text-[#00236F] transition-colors">
            {{ store.readersStats.value.total }}
          </p>
          <span class="text-[10px] text-emerald-700 font-bold flex items-center gap-0.5">
            <TrendingUp class="h-3 w-3" /> 本周新增 24 位
          </span>
        </div>
        <div class="w-10 h-10 bg-[#D5E3FC]/40 text-[#00236F] rounded-lg flex items-center justify-center">
          <Users class="h-5 w-5" />
        </div>
      </div>

      <div 
        @click="selectShortcut('borrowing')"
        class="bg-white p-5 border border-[#E0E3E5] rounded-xl hover:border-[#00236F] cursor-pointer transition-all shadow-sm flex items-center justify-between group"
      >
        <div class="space-y-1">
          <p class="text-xs text-[#757682] font-semibold uppercase tracking-wider">在途借出藏书</p>
          <p class="text-3xl font-extrabold text-[#191C1E] group-hover:text-[#00236F] transition-colors">
            {{ store.borrowingsStats.value.total }}
          </p>
          <span class="text-[10px] text-[#757682] font-semibold">今日待到期归还 {{ store.borrowingsStats.value.dueToday }} 本</span>
        </div>
        <div class="w-10 h-10 bg-[#D5E3FC]/40 text-[#00236F] rounded-lg flex items-center justify-center">
          <RefreshCw class="h-5 w-5" />
        </div>
      </div>

      <div 
        @click="selectShortcut('borrowing')"
        class="bg-white p-5 border border-[#E0E3E5] rounded-xl hover:border-[#BA1A1A] cursor-pointer transition-all shadow-sm flex items-center justify-between group"
      >
        <div class="space-y-1">
          <p class="text-xs text-[#757682] font-semibold uppercase tracking-wider text-[#BA1A1A]">逾期未还警示</p>
          <p class="text-3xl font-extrabold text-[#BA1A1A]">
            {{ store.borrowingsStats.value.overdue }}
          </p>
          <span class="text-[10px] text-[#BA1A1A]/90 font-bold">亟待派发催还通知</span>
        </div>
        <div class="w-10 h-10 bg-[#FFDAD6] text-[#BA1A1A] rounded-lg flex items-center justify-center">
          <AlertCircle class="h-5 w-5" />
        </div>
      </div>
    </div>

    <!-- Layout Split Sections: Shortcuts & Recent logs -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      
      <!-- Left Column: Quick Actions & Status -->
      <div class="lg:col-span-2 space-y-6">
        <!-- Shortcuts Bento Container Panel -->
        <div class="bg-white p-5 border border-[#E0E3E5] rounded-xl shadow-sm">
          <h3 class="font-bold text-base text-[#191C1E] mb-4">快捷智能业务控制通道</h3>
          
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <button 
              @click="selectShortcut('books')"
              class="p-4 border border-[#ECEEF0] hover:border-[#00236F] bg-[#F2F4F6]/40 hover:bg-[#D5E3FC]/20 rounded-xl text-left transition-all duration-200 cursor-pointer group"
            >
              <PlusCircle class="h-6 w-6 text-[#00236F] mb-3" />
              <h4 class="font-bold text-sm text-[#191C1E] group-hover:text-[#00236F] transition-colors">新购藏书入库</h4>
              <p class="text-xs text-[#757682] mt-1">新增、编目物理及流媒体电子书籍</p>
            </button>

            <button 
              @click="selectShortcut('borrowing')"
              class="p-4 border border-[#ECEEF0] hover:border-[#00236F] bg-[#F2F4F6]/40 hover:bg-[#D5E3FC]/20 rounded-xl text-left transition-all duration-200 cursor-pointer group"
            >
              <RefreshCw class="h-6 w-6 text-[#00236F] mb-3" />
              <h4 class="font-bold text-sm text-[#191C1E] group-hover:text-[#00236F] transition-colors">开具新借阅单</h4>
              <p class="text-xs text-[#757682] mt-1">快速登记借阅读者并分配图书出库</p>
            </button>

            <button 
              @click="selectShortcut('readers')"
              class="p-4 border border-[#ECEEF0] hover:border-[#00236F] bg-[#F2F4F6]/40 hover:bg-[#D5E3FC]/20 rounded-xl text-left transition-all duration-200 cursor-pointer group"
            >
              <UserPlus class="h-6 w-6 text-[#00236F] mb-3" />
              <h4 class="font-bold text-sm text-[#191C1E] group-hover:text-[#00236F] transition-colors">注册新读者</h4>
              <p class="text-xs text-[#757682] mt-1">注册开户、录入电子会籍并分配评级</p>
            </button>
          </div>
        </div>

        <!-- Academic institution branding guide card -->
        <div class="bg-[#D5E3FC]/20 p-6 rounded-xl border border-[#D5E3FC]/65 flex gap-4">
          <div class="p-3 bg-[#D5E3FC] text-[#00236F] rounded-xl self-start">
            <Building class="h-6 w-6" />
          </div>
          <div class="space-y-2">
            <h4 class="text-sm font-bold text-[#0d1c2e] uppercase tracking-wider">关于图书馆学术管理规范声明</h4>
            <p class="text-xs text-[#444651] leading-relaxed">
              数字化图书馆管理系统（LMS Institutional Control）旨在实现藏书采编一体化、在途流转实时掌控与读者画像精细治理。全馆人员均须严格依照《高校数字期刊资产管理守则》和《馆藏借阅逾期罚息制度》组织图书轮换，以确保学术资源合理配置。
            </p>
          </div>
        </div>
      </div>

      <!-- Right Column: Status Summary Panel -->
      <div class="space-y-6">
        <div class="bg-white p-5 border border-[#E0E3E5] rounded-xl shadow-sm space-y-4">
          <h3 class="font-bold text-base text-[#191C1E]">实时流通动态监督</h3>
          
          <div class="divide-y divide-[#ECEEF0]">
            <div class="py-3 flex items-center justify-between text-xs">
              <div>
                <p class="font-bold text-sm text-[#191C1E]">{{ store.borrowingsStats.value.dueToday }} 本书籍今日待收至在馆</p>
                <p class="text-[#757682] mt-0.5">普通回流计划</p>
              </div>
              <span class="px-2 py-1 bg-yellow-50 text-yellow-800 rounded font-semibold text-[10px]">在途催归</span>
            </div>

            <div class="py-3 flex items-center justify-between text-xs">
              <div>
                <p class="font-bold text-sm text-[#191C1E]">全站系统版本: v4.2.1-lms</p>
                <p class="text-[#757682] mt-0.5">数字微服务云更新模块编译通过</p>
              </div>
              <span class="px-2 py-1 bg-emerald-50 text-emerald-800 rounded font-semibold text-[10px]">运行强健</span>
            </div>

            <div class="py-3 flex items-center justify-between text-xs">
              <div>
                <p class="font-bold text-sm text-[#191C1E]">120 所合作高校学术馆</p>
                <p class="text-[#757682] mt-0.5">全天候通借通还专网就绪</p>
              </div>
              <span class="px-2 py-1 bg-blue-50 text-[#00236F] rounded font-semibold text-[10px]">互联就绪</span>
            </div>
          </div>

          <button 
            @click="selectShortcut('help')"
            class="w-full py-2 bg-[#F2F4F6] text-[#00236F] hover:bg-[#D5E3FC]/40 text-xs font-bold rounded-lg transition-colors flex items-center justify-center gap-1 cursor-pointer"
          >
            去阅读使用指南
            <ChevronRight class="h-3.5 w-3.5" />
          </button>
        </div>
      </div>
      
    </div>
  </div>
</template>
