<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '../store';
import { 
  User, 
  Shield, 
  Database, 
  Palette, 
  Save, 
  RefreshCw, 
  Sliders, 
  CheckCircle, 
  Image as ImageIcon, 
  Trash2, 
  Download, 
  Info 
} from 'lucide-vue-next';

const store = useStore();
const { state, updateAdminProfile } = store;

// active sub-tab for settings categorizations
const activeCategory = ref<'profile' | 'rules' | 'appearance' | 'database'>('profile');

// Profile form states
const nameField = ref(state.adminName);
const roleField = ref(state.adminRole);
const avatarField = ref(state.adminAvatar);

// Library config controls states (saved in localStorage just for aesthetic statefulness)
const defaultDuration = ref(14);
const maxBookLimitRegular = ref(3);
const maxBookLimitPremium = ref(10);
const overdueRate = ref(0.2);

// Preset Avatar Options for extremely rapid and delightful profile customization
const avatarPresets = [
  'https://lh3.googleusercontent.com/aida-public/AB6AXuC1aWyzzBdkEa3OitasgWmAgmyslrGHUbUBzfEYEFxtT4MBIOnSL0kOvZa-N2iGsNQcBINJczbYG0k8n1kbSd8Tlb5kpaSq32_OjVyt05_HHnrmt7PFC_IIAt_Awly5EmujX60PC03c-kcKjwCa69PqbGYnlphPYujKNcD81iTdYAIPZK7GPr6z_rijyPOF-YceJPR1CbjSe2iJcY33ejC_6kD-hLuRkX72FhXhCVmP_29IbuimQsfA64mT2UwVZryQPbN7I5yX4BE',
  'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&h=150&fit=crop&crop=faces',
  'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&h=150&fit=crop&crop=faces',
  'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=faces',
  'https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=150&h=150&fit=crop&crop=faces'
];

onMounted(() => {
  // Load configuration if already in storage
  defaultDuration.value = Number(localStorage.getItem('cfg_defaultDuration') || '14');
  maxBookLimitRegular.value = Number(localStorage.getItem('cfg_maxBookLimitRegular') || '3');
  maxBookLimitPremium.value = Number(localStorage.getItem('cfg_maxBookLimitPremium') || '10');
  overdueRate.value = Number(localStorage.getItem('cfg_overdueRate') || '0.2');
});

const selectAvatarPreset = (url: string) => {
  avatarField.value = url;
};

// Success Alert Toast configuration
const showToast = ref(false);
const toastMessage = ref('');

const triggerToast = (msg: string) => {
  toastMessage.value = msg;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3500);
};

// Actions
const handleSaveProfile = () => {
  if (!nameField.value.trim() || !roleField.value.trim()) {
    alert('请填写完整的管理员名称和职务！');
    return;
  }
  updateAdminProfile(nameField.value, roleField.value, avatarField.value);
  triggerToast('管理员个人账户资料已成功保存并实时生效。');
};

const handleSaveRules = () => {
  localStorage.setItem('cfg_defaultDuration', String(defaultDuration.value));
  localStorage.setItem('cfg_maxBookLimitRegular', String(maxBookLimitRegular.value));
  localStorage.setItem('cfg_maxBookLimitPremium', String(maxBookLimitPremium.value));
  localStorage.setItem('cfg_overdueRate', String(overdueRate.value));
  triggerToast('馆藏借阅标准及会籍限额规则已更新。');
};

const handleResetFactory = () => {
  if (confirm('警告：此操作将清除当前所有的图书目录、读者信息和借阅流水记录！确认执行吗？')) {
    // 清空所有本地数据
    state.readers = [];
    state.books = [];
    state.borrowings = [];
    state.notifications = [];
    triggerToast('数据已成功擦除。系统即将重载...');
    setTimeout(() => {
      window.location.reload();
    }, 1500);
  }
};

const handleBackupExport = () => {
  const fullBackup = {
    readers: state.readers,
    books: state.books,
    borrowings: state.borrowings,
    admin: {
      name: state.adminName,
      role: state.adminRole,
      avatar: state.adminAvatar
    },
    exportTime: new Date().toISOString()
  };
  
  const blob = new Blob([JSON.stringify(fullBackup, null, 2)], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `LMS_Backup_${new Date().toISOString().split('T')[0]}.json`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
  triggerToast('全站 JSON 格式数据库包已打包并自动开始下载。');
};

// For simulated customization settings 
const layoutComfort = ref('standard');
const handleSaveAppearance = () => {
  triggerToast('显示偏好已保存。布局偏好：' + (layoutComfort.value === 'standard' ? '标准舒适' : '极高密度极简'));
};
</script>

<template>
  <div class="space-y-6 text-[#191C1E]">
    <!-- Page Header -->
    <div>
      <h2 class="text-3xl font-bold text-[#00236F] tracking-tight">系统设置</h2>
      <p class="text-sm text-[#444651] mt-1 text-opacity-80">配置管理员认证、定义图书出借细则及备份馆藏核心信息。</p>
    </div>

    <!-- Main settings split layout -->
    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6 items-start">
      <!-- Left side controls menu -->
      <aside class="bg-white border border-[#E0E3E5] rounded-xl overflow-hidden shadow-sm">
        <ul class="divide-y divide-[#ECEEF0]">
          <!-- Profile tab -->
          <li>
            <button 
              @click="activeCategory = 'profile'"
              :class="[
                'w-full flex items-center gap-3 px-5 py-4 text-sm font-semibold transition-all text-left cursor-pointer',
                activeCategory === 'profile' 
                  ? 'bg-[#D5E3FC] text-[#00236F] border-l-4 border-[#00236F]' 
                  : 'text-[#57657A] hover:bg-[#F2F4F6] hover:text-[#191C1E]'
              ]"
            >
              <User class="h-4.5 w-4.5" />
              <span>管理员账户</span>
            </button>
          </li>

          <!-- Rules tab -->
          <li>
            <button 
              @click="activeCategory = 'rules'"
              :class="[
                'w-full flex items-center gap-3 px-5 py-4 text-sm font-semibold transition-all text-left cursor-pointer',
                activeCategory === 'rules' 
                  ? 'bg-[#D5E3FC] text-[#00236F] border-l-4 border-[#00236F]' 
                  : 'text-[#57657A] hover:bg-[#F2F4F6] hover:text-[#191C1E]'
              ]"
            >
              <Sliders class="h-4.5 w-4.5" />
              <span>借阅规则设定</span>
            </button>
          </li>

          <!-- Layout and appearances tab -->
          <li>
            <button 
              @click="activeCategory = 'appearance'"
              :class="[
                'w-full flex items-center gap-3 px-5 py-4 text-sm font-semibold transition-all text-left cursor-pointer',
                activeCategory === 'appearance' 
                  ? 'bg-[#D5E3FC] text-[#00236F] border-l-4 border-[#00236F]' 
                  : 'text-[#57657A] hover:bg-[#F2F4F6] hover:text-[#191C1E]'
              ]"
            >
              <Palette class="h-4.5 w-4.5" />
              <span>界面及显示偏好</span>
            </button>
          </li>

          <!-- Database management tab -->
          <li>
            <button 
              @click="activeCategory = 'database'"
              :class="[
                'w-full flex items-center gap-3 px-5 py-4 text-sm font-semibold transition-all text-left cursor-pointer',
                activeCategory === 'database' 
                  ? 'bg-[#D5E3FC] text-[#00236F] border-l-4 border-[#00236F]' 
                  : 'text-[#57657A] hover:bg-[#F2F4F6] hover:text-[#191C1E]'
              ]"
            >
              <Database class="h-4.5 w-4.5" />
              <span>数据库与灾备</span>
            </button>
          </li>
        </ul>
      </aside>

      <!-- Right side settings workspace -->
      <main class="lg:col-span-3 bg-white border border-[#E0E3E5] rounded-xl shadow-sm p-6 min-h-[460px]">
        <!-- 1. Administrator Profile Settings -->
        <div v-if="activeCategory === 'profile'" class="space-y-6">
          <div>
            <h3 class="text-lg font-bold text-[#191C1E]">管理员账户配置</h3>
            <p class="text-xs text-[#757682] mt-1">设置本校馆长/图书馆管理员的对外名片，更改后系统顶栏及活动痕迹将自动同步更新。</p>
          </div>

          <form @submit.prevent="handleSaveProfile" class="space-y-5 max-w-xl">
            <!-- Visual avatar editor -->
            <div class="space-y-2">
              <label class="text-xs font-semibold text-[#444651] uppercase tracking-wider block">管理员头像 / Avatar</label>
              <div class="flex flex-col sm:flex-row items-start sm:items-center gap-4">
                <img :src="avatarField" class="w-16 h-16 rounded-full object-cover border-2 border-[#00236F] shadow-sm bg-slate-50" alt="Current Avatar" />
                
                <div class="space-y-2 flex-1">
                  <p class="text-xs text-[#57657A]">点击快速选用高精度极简馆员头像 preset：</p>
                  <div class="flex flex-wrap gap-2">
                    <button 
                      v-for="(photo, idx) in avatarPresets" 
                      :key="idx"
                      type="button"
                      @click="selectAvatarPreset(photo)"
                      class="w-10 h-10 rounded-full overflow-hidden border-2 transition-transform hover:scale-105 cursor-pointer"
                      :class="avatarField === photo ? 'border-[#00236F] scale-105' : 'border-[#E0E3E5] hover:border-[#757682]'"
                    >
                      <img :src="photo" class="w-full h-full object-cover" />
                    </button>
                  </div>
                </div>
              </div>
              <div class="pt-2">
                <input 
                  v-model="avatarField"
                  type="text"
                  class="w-full h-9 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] rounded-lg outline-none text-xs text-[#191C1E]"
                  placeholder="或者贴入自定义头像外部图片链接 (HTTP/HTTPS)..."
                />
              </div>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <!-- Librarian Name -->
              <div class="space-y-1">
                <label class="text-xs font-semibold text-[#444651] uppercase tracking-wider">管理员名称</label>
                <input 
                  v-model="nameField"
                  type="text"
                  required
                  class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
                  placeholder="例如: 尤里安·索恩"
                />
              </div>

              <!-- Librarian Position -->
              <div class="space-y-1">
                <label class="text-xs font-semibold text-[#444651] uppercase tracking-wider">管理职责/职务</label>
                <input 
                  v-model="roleField"
                  type="text"
                  required
                  class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
                  placeholder="例如: 高校领衔馆长"
                />
              </div>
            </div>

            <!-- Role Permissions Mock info block -->
            <div class="p-4 bg-blue-50/40 rounded-xl border border-blue-100/50 flex gap-3 text-xs text-[#00236F]">
              <Shield class="h-4 w-4 shrink-0 mt-0.5" />
              <div>
                <p class="font-bold">您的系统权限：系统超级决策主控官 (Super-Admin)</p>
                <p class="mt-1 text-slate-600 leading-normal">
                  您享有对采编目录物理入库、注销历史账面、发放及冻结普通与高级读者凭卡、一键清流全馆逾期罚单的一切最高解释权及读写豁免。
                </p>
              </div>
            </div>

            <!-- Action Button -->
            <div class="pt-4 border-t border-[#ECEEF0] flex justify-end">
              <button 
                type="submit"
                class="bg-[#00236F] hover:bg-[#1E3A8A] text-white px-6 py-2.5 rounded-lg text-xs font-bold flex items-center gap-1.5 transition-all shadow-sm cursor-pointer"
              >
                <Save class="h-4 w-4" />
                保存资料变更
              </button>
            </div>
          </form>
        </div>

        <!-- 2. Library Outlent Rules Settings -->
        <div v-if="activeCategory === 'rules'" class="space-y-6">
          <div>
            <h3 class="text-lg font-bold text-[#191C1E]">出借与归还标准规章配置</h3>
            <p class="text-xs text-[#757682] mt-1">控制系统前台自动到期周期、借记本数天数、每日到期利息费率，这些数值将作为生成借阅条目时的缺省计算基底。</p>
          </div>

          <form @submit.prevent="handleSaveRules" class="space-y-5 max-w-xl">
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <!-- Normal Borrow Period Duration -->
              <div class="space-y-1">
                <label class="text-xs font-semibold text-[#444651] uppercase tracking-wider block">标准纸质图书借期 (天)</label>
                <select 
                  v-model="defaultDuration"
                  class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] rounded-lg outline-none text-sm text-[#191C1E] cursor-pointer"
                >
                  <option :value="7">7 天 (短线极速快借)</option>
                  <option :value="14">14 天 (标准推荐周期)</option>
                  <option :value="30">30 天 (高端研究长线)</option>
                  <option :value="60">60 天 (特许教学大周期)</option>
                </select>
              </div>

              <!-- Overdue daily fine fee -->
              <div class="space-y-1">
                <label class="text-xs font-semibold text-[#444651] uppercase tracking-wider block">逾期每日罚息利息 (元/天)</label>
                <input 
                  v-model="overdueRate"
                  type="number"
                  step="0.1"
                  required
                  class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
                />
              </div>

              <!-- General Regular Reader standard checkout volume -->
              <div class="space-y-1">
                <label class="text-xs font-semibold text-[#444651] uppercase tracking-wider block">普通会员最大出库借书上限</label>
                <input 
                  v-model="maxBookLimitRegular"
                  type="number"
                  required
                  class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
                />
              </div>

              <!-- Premium Reader default checkout volume -->
              <div class="space-y-1">
                <label class="text-xs font-semibold text-[#444651] uppercase tracking-wider block">高级VIP高级会员出借上限</label>
                <input 
                  v-model="maxBookLimitPremium"
                  type="number"
                  required
                  class="w-full h-10 px-3 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] rounded-lg outline-none text-sm text-[#191C1E]"
                />
              </div>
            </div>

            <!-- Action Button -->
            <div class="pt-4 border-t border-[#ECEEF0] flex justify-end">
              <button 
                type="submit"
                class="bg-[#00236F] hover:bg-[#1E3A8A] text-white px-6 py-2.5 rounded-lg text-xs font-bold flex items-center gap-1.5 transition-all shadow-sm cursor-pointer"
              >
                <Save class="h-4 w-4" />
                应用新流通细则
              </button>
            </div>
          </form>
        </div>

        <!-- 3. Appearances Layout Configurations -->
        <div v-if="activeCategory === 'appearance'" class="space-y-6">
          <div>
            <h3 class="text-lg font-bold text-[#191C1E]">界面风格与显示偏好</h3>
            <p class="text-xs text-[#757682] mt-1">设置图书馆控制台的视觉密度，让您以最舒适的状态专注管理学术资产。</p>
          </div>

          <form @submit.prevent="handleSaveAppearance" class="space-y-5 max-w-xl">
            <!-- Visual Density select -->
            <div class="space-y-2">
              <label class="text-xs font-semibold text-[#444651] block uppercase tracking-wider">系统显示间距密度</label>
              <div class="grid grid-cols-2 gap-4">
                <label class="border-2 rounded-xl p-4 flex flex-col justify-between h-24 cursor-pointer hover:border-[#00236F] transition-all" :class="layoutComfort === 'standard' ? 'border-[#00236F] bg-[#D5E3FC]/20' : 'border-[#E0E3E5]'">
                  <div class="flex justify-between items-center">
                    <span class="text-sm font-bold">标准学者风骨</span>
                    <input type="radio" value="standard" v-model="layoutComfort" class="text-[#00236F]" />
                  </div>
                  <span class="text-[11px] text-[#57657A] leading-tight">开阔雅致，采用 1.5rem 外间距，留白丰满舒适。</span>
                </label>

                <label class="border-2 rounded-xl p-4 flex flex-col justify-between h-24 cursor-pointer hover:border-[#00236F] transition-all" :class="layoutComfort === 'compact' ? 'border-[#00236F] bg-[#D5E3FC]/20' : 'border-[#E0E3E5]'">
                  <div class="flex justify-between items-center">
                    <span class="text-sm font-bold">高稠密极简</span>
                    <input type="radio" value="compact" v-model="layoutComfort" class="text-[#00236F]" />
                  </div>
                  <span class="text-[11px] text-[#57657A] leading-tight">紧固密集，削平 35% 内衬，一屏容纳海量清册。</span>
                </label>
              </div>
            </div>

            <!-- Simulated Primary Colors select -->
            <div class="space-y-1.5">
              <label class="text-xs font-semibold text-[#444651] block uppercase tracking-wider">系统强调色调 (Academic Accents Preset)</label>
              <div class="flex gap-3">
                <span class="w-8 h-8 rounded-full bg-[#00236F] border-2 border-white ring-2 ring-[#00236F] flex items-center justify-center text-white text-[10px] font-bold" title="深邃雅蓝 (Academic Deep Blue)">✔</span>
                <span class="w-8 h-8 rounded-full bg-emerald-800 hover:scale-105 transition-transform cursor-pointer" title="青石古绿 (Herbal Emerald)"></span>
                <span class="w-8 h-8 rounded-full bg-purple-900 hover:scale-105 transition-transform cursor-pointer" title="皇家仪紫 (Royal Amethyst)"></span>
                <span class="w-8 h-8 rounded-full bg-teal-800 hover:scale-105 transition-transform cursor-pointer" title="水洗藏青 (Chambray Navy)"></span>
              </div>
              <p class="text-[10px] text-[#757682] pt-1">全站已默认全局锁定 **深邃雅蓝 (Academic Deep Blue)** 纯度风骨，这是高校权威图书馆专用的色值配比。</p>
            </div>

            <!-- Action Button -->
            <div class="pt-4 border-t border-[#ECEEF0] flex justify-end">
              <button 
                type="submit"
                class="bg-[#00236F] hover:bg-[#1E3A8A] text-white px-6 py-2.5 rounded-lg text-xs font-bold flex items-center gap-1.5 transition-all shadow-sm cursor-pointer"
              >
                <Save class="h-4 w-4" />
                应用排版及偏好设置
              </button>
            </div>
          </form>
        </div>

        <!-- 4. Database Backups, Restores -->
        <div v-if="activeCategory === 'database'" class="space-y-6">
          <div>
            <h3 class="text-lg font-bold text-[#191C1E]">全功能数据库灾备与重载</h3>
            <p class="text-xs text-[#757682] mt-1">针对物理单机环境下做完整打包备灾，提供高压缩率 JSON 备份包打包一键下载。亦可进行安全重置。</p>
          </div>

          <div class="space-y-5 max-w-xl">
            <!-- Simulated Cloud Sync indicator -->
            <div class="p-5 border border-[#E0E3E5] rounded-xl flex items-center justify-between">
              <div class="flex gap-3">
                <div class="p-2.5 bg-emerald-50 rounded-lg text-emerald-800">
                  <RefreshCw class="h-6 w-6 animate-spin-slow" />
                </div>
                <div>
                  <h4 class="text-sm font-bold">全机房本地离线存储安全守护</h4>
                  <p class="text-xs text-[#757682] mt-0.5">本地存储健康，实时同步间隔：0.8ms</p>
                </div>
              </div>
              <span class="text-[10px] px-2 py-1 bg-emerald-50 text-emerald-800 rounded-full font-bold">
                健康度 100%
              </span>
            </div>

            <!-- Actions grid bento panel -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <!-- Export block -->
              <div class="p-4 border border-[#ECEEF0] rounded-xl flex flex-col justify-between space-y-3">
                <div>
                  <h5 class="text-xs font-bold uppercase tracking-wider text-[#444651]">全站图书及读者单包备份</h5>
                  <p class="text-[11px] text-[#757682] mt-1">将当前包含读者会籍、所有藏书书籍、当前在途借还等所有的 JSON 数据全部混装成一个单独的备份包，用作本地存储异地转移。</p>
                </div>
                <button 
                  @click="handleBackupExport"
                  class="w-full flex items-center justify-center gap-1.5 py-2.5 px-4 bg-white border border-[#00236F] hover:bg-[#D5E3FC]/20 text-[#00236F] font-bold text-xs rounded-lg transition-colors cursor-pointer"
                >
                  <Download class="h-4 w-4" />
                  下载全量导出包
                </button>
              </div>

              <!-- Reset factory block -->
              <div class="p-4 border border-[#ECEEF0] rounded-xl flex flex-col justify-between space-y-3">
                <div>
                  <h5 class="text-xs font-bold uppercase tracking-wider text-[#BA1A1A]">格式化出厂擦拭重载</h5>
                  <p class="text-[11px] text-[#757682] mt-1">当馆内数据发生重大错乱，希望推倒重来，可以彻底清除数据重载以回归到初始配给的新生状态，并抹平一切历史借书记录。</p>
                </div>
                <button 
                  @click="handleResetFactory"
                  class="w-full flex items-center justify-center gap-1.5 py-2.5 px-4 bg-white border border-[#BA1A1A] hover:bg-[#FFDAD6]/30 text-[#BA1A1A] font-bold text-xs rounded-lg transition-colors cursor-pointer"
                >
                  <Trash2 class="h-4 w-4" />
                  出厂数据总格式化
                </button>
              </div>
            </div>

            <!-- Help Tips -->
            <div class="flex gap-2 text-[11px] text-[#757682]">
              <Info class="h-4 w-4 shrink-0" />
              <span>注：此数据库直接承载于沙箱内的 LocalStorage，安全沙盒自动给每个浏览器客户端分配唯一的专署秘钥哈希，他人不可隔空窥视，充分捍卫读者隐私。</span>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- Alert Toast notification popup -->
    <transition name="toast-fade">
      <div 
        v-if="showToast" 
        class="fixed bottom-6 left-1/2 -translate-x-1/2 py-3 px-6 bg-[#00236F] text-white shadow-2xl rounded-xl flex items-center gap-3 border border-white/10 z-50 animate-bounce-short text-sm font-semibold"
      >
        <CheckCircle class="h-5 w-5 text-emerald-400 fill-emerald-800" />
        <span>{{ toastMessage }}</span>
      </div>
    </transition>
  </div>
</template>

<style>
/* Toast Fade Animation */
.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.toast-fade-enter-from,
.toast-fade-leave-to {
  opacity: 0;
  transform: translate(-50%, 10px);
}

.animate-spin-slow {
  animation: spin 8s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
