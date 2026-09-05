<script setup lang="ts">
import { useStore } from './store';
import LoginView from './components/LoginView.vue';
import Sidebar from './components/Sidebar.vue';
import Topbar from './components/Topbar.vue';
import DashboardView from './components/DashboardView.vue';
import BookCatalog from './components/BookCatalog.vue';
import BorrowingManagement from './components/BorrowingManagement.vue';
import ReaderManagement from './components/ReaderManagement.vue';
import HelpView from './components/HelpView.vue';
import SettingsView from './components/SettingsView.vue';

const { state } = useStore();
</script>

<template>
  <div>
    <!-- Render login screen if user is not logged in -->
    <LoginView v-if="!state.isLoggedIn" />

    <!-- Main System Layout -->
    <div v-else class="min-h-screen bg-[#F7F9FB]">
      <!-- Fixed left side navigation menu -->
      <Sidebar />

      <!-- Main container wrapper page context -->
      <div class="ml-[280px] min-h-screen flex flex-col pt-16">
        <!-- Sticky top navigation search & profiler bar -->
        <Topbar />

        <!-- Subpage content loading section -->
        <main class="flex-1 p-6 overflow-y-auto">
          <div class="max-w-[1440px] mx-auto scale-98 md:scale-100 transition-all">
            
            <transition name="fade" mode="out-in">
              <component v-if="state.activeTab === 'dashboard'" :is="DashboardView" />
              <component v-else-if="state.activeTab === 'books'" :is="BookCatalog" />
              <component v-else-if="state.activeTab === 'borrowing'" :is="BorrowingManagement" />
              <component v-else-if="state.activeTab === 'readers'" :is="ReaderManagement" />
              <component v-else-if="state.activeTab === 'help'" :is="HelpView" />
              <component v-else-if="state.activeTab === 'settings'" :is="SettingsView" />
            </transition>

          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<style>
/* Clean micro fade animations for tab transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.22s cubic-bezier(0.4, 0, 0.2, 1), transform 0.22s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(4px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

/* Ensure nice default font selections and antialias filters */
html, body {
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
  scroll-behavior: smooth;
  margin: 0;
  padding: 0;
}
</style>
