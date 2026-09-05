<script setup lang="ts">
import { useStore } from '../store';
import { 
  LayoutDashboard, 
  BookOpen, 
  RefreshCw, 
  Users, 
  HelpCircle, 
  Settings,
  LogOut 
} from 'lucide-vue-next';

const { state, setTab, logout } = useStore();

const navItems = [
  { id: 'dashboard', label: '仪表盘', icon: LayoutDashboard },
  { id: 'books', label: '图书目录', icon: BookOpen },
  { id: 'borrowing', label: '借阅管理', icon: RefreshCw },
  { id: 'readers', label: '读者管理', icon: Users },
] as const;
</script>

<template>
  <aside class="fixed left-0 top-0 h-full w-[280px] bg-white border-r border-[#E0E3E5] flex flex-col py-6 px-4 z-50">
    <!-- Brand Title Section -->
    <div class="flex items-center gap-3 px-2 mb-8">
      <div class="w-10 h-10 rounded-lg bg-[#00236F] flex items-center justify-center text-white">
        <BookOpen class="h-6 w-6" />
      </div>
      <div>
        <h1 class="text-lg font-extrabold text-[#00236F] leading-tight">图书馆系统</h1>
        <p class="text-[10px] text-[#444651] tracking-wider uppercase opacity-75">追求学术卓越</p>
      </div>
    </div>

    <!-- Navigation Menu -->
    <nav class="flex-1 space-y-1">
      <button 
        v-for="item in navItems" 
        :key="item.id"
        @click="setTab(item.id)"
        :class="[
          'w-full flex items-center gap-3 px-4 py-3 rounded-lg text-sm transition-all duration-200 cursor-pointer',
          state.activeTab === item.id 
            ? 'text-[#00236F] font-bold border-l-4 border-[#00236F] bg-[#D5E3FC]' 
            : 'text-[#57657A] hover:bg-[#ECEEF0]'
        ]"
      >
        <component :is="item.icon" class="h-5 w-5" :class="state.activeTab === item.id ? 'text-[#00236F]' : 'text-[#757682]'" />
        <span>{{ item.label }}</span>
      </button>
    </nav>

    <!-- Support and Logout Action Panel -->
    <div class="mt-auto space-y-1 pt-6 border-t border-[#C5C5D3]">
      <button 
        @click="setTab('settings')"
        :class="[
          'w-full flex items-center gap-3 px-4 py-3 rounded-lg text-sm transition-all duration-200 cursor-pointer',
          state.activeTab === 'settings' 
            ? 'text-[#00236F] font-bold border-l-4 border-[#00236F] bg-[#D5E3FC]' 
            : 'text-[#57657A] hover:bg-[#ECEEF0]'
        ]"
      >
        <Settings class="h-5 w-5" :class="state.activeTab === 'settings' ? 'text-[#00236F]' : 'text-[#757682]'" />
        <span>系统设置</span>
      </button>

      <button 
        @click="setTab('help')"
        :class="[
          'w-full flex items-center gap-3 px-4 py-3 rounded-lg text-sm transition-all duration-200 cursor-pointer',
          state.activeTab === 'help' 
            ? 'text-[#00236F] font-bold border-l-4 border-[#00236F] bg-[#D5E3FC]' 
            : 'text-[#57657A] hover:bg-[#ECEEF0]'
        ]"
      >
        <HelpCircle class="h-5 w-5" :class="state.activeTab === 'help' ? 'text-[#00236F]' : 'text-[#757682]'" />
        <span>帮助中心</span>
      </button>

      <button 
        @click="logout"
        class="w-full flex items-center gap-3 px-4 py-3 rounded-lg text-sm text-[#BA1A1A] hover:bg-[#FFDAD6] transition-all duration-200 cursor-pointer font-medium"
      >
        <LogOut class="h-5 w-5 text-[#BA1A1A]" />
        <span>退出登录</span>
      </button>
    </div>
  </aside>
</template>
