<script setup lang="ts">
import { ref } from 'vue';
import { useStore } from '../store';
import { Eye, EyeOff, Lock, User, Globe, HelpCircle, BookOpen, ArrowLeft, Mail, ShieldAlert, CheckCircle2 } from 'lucide-vue-next';

const { login } = useStore();

const username = ref('admin@library.org');
const password = ref('admin123');
const isSubmitting = ref(false);
const showPassword = ref(false);
const loginError = ref('');

const handleLoginSubmit = async () => {
  isSubmitting.value = true;
  loginError.value = '';
  try {
    await login(username.value, password.value);
  } catch (e: any) {
    loginError.value = e.message || '登录失败，请检查用户名和密码';
  } finally {
    isSubmitting.value = false;
  }
};

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};

// Forgot Password Workflow States
const isForgotMode = ref(false);
const forgotEmail = ref('admin@library.org');
const verificationCode = ref('');
const newPassword = ref('');
const showNewPassword = ref(false);
const forgotStep = ref(1); // 1: Email Request, 2: Verification & New Password, 3: Success Confirmation
const isSendingCode = ref(false);
const isResettingPassword = ref(false);
const codeErrorMsg = ref('');

const openForgotMode = () => {
  isForgotMode.value = true;
  forgotStep.value = 1;
  verificationCode.value = '';
  newPassword.value = '';
  codeErrorMsg.value = '';
};

const handleSendCode = () => {
  if (!forgotEmail.value.trim() || !forgotEmail.value.includes('@')) {
    codeErrorMsg.value = '请输入有效的管理员绑定邮箱地址！';
    return;
  }
  
  isSendingCode.value = true;
  codeErrorMsg.value = '';
  
  // Simulated API call latency
  setTimeout(() => {
    isSendingCode.value = false;
    forgotStep.value = 2; // Move to OTP input
  }, 1000);
};

const handleResetSubmit = () => {
  if (verificationCode.value !== '8888') {
    codeErrorMsg.value = '您输入的双因子验证码不正确，测试验证码请使用: 8888';
    return;
  }
  if (newPassword.value.length < 6) {
    codeErrorMsg.value = '为了系统安全，新密码长度不能少于 6 位。';
    return;
  }

  isResettingPassword.value = true;
  codeErrorMsg.value = '';

  setTimeout(() => {
    isResettingPassword.value = false;
    forgotStep.value = 3; // Success state
    // Automatically fill the login password field in the background for a frictionless flow
    password.value = newPassword.value;
    username.value = forgotEmail.value;
  }, 1200);
};

const finishRecoveryAndLogin = () => {
  isForgotMode.value = false;
  forgotStep.value = 1;
};
</script>

<template>
  <main class="flex min-h-screen bg-[#F7F9FB] text-[#191C1E] antialiased overflow-hidden">
    <!-- Left Side: Visual & Branding (60% Width on Desktop) -->
    <section class="hidden lg:flex lg:w-[60%] relative items-center justify-center overflow-hidden">
      <!-- Background Image with pristine library styling -->
      <div 
        class="absolute inset-0 z-0 h-full w-full bg-cover bg-center" 
        style="background-image: url('https://lh3.googleusercontent.com/aida-public/AB6AXuPhnQDiMf_7ykM7Mzh-MQUhiKQ4yXhJofPja8Y_ePnUOUMvDjAsiollkkWqOrGd-qBUtR0Rakgayi0XFBQgYTpCRJXod-MzONQL_H1FwXgqRuhtG0cdHsQmXg2Vw9Hg88UsgDO7i7-s9mZqcLciz8ww7DF9xevt7JP9pTGu_vN69CUzWwDuBu9rtQspuKRuYGMTnr3DNl7hPMynRH0U8ghQf1YgNfLBDCUxpRNy9VsYHqpsvNvpTBI0T-7NOmh53IFYYwwq2zy2qg')"
      ></div>
      <!-- Branding Overlay -->
      <div class="absolute inset-0 z-10 bg-gradient-to-br from-[#00236F]/90 via-[#00236F]/80 to-[#004565]/40"></div>
      
      <div class="relative z-20 px-12 text-white max-w-2xl">
        <div class="flex items-center gap-4 mb-8">
          <div class="bg-white/10 backdrop-blur-md p-3 rounded-xl border border-white/20">
            <BookOpen class="h-10 w-10 text-white" />
          </div>
          <div>
            <h1 class="text-3xl font-extrabold tracking-tight" id="main-brand-title">图书管理系统</h1>
            <p class="text-sm opacity-90 tracking-widest uppercase">LMS Institutional Control</p>
          </div>
        </div>
        
        <div class="space-y-4">
          <h2 class="text-5xl font-extrabold leading-tight tracking-tight">追求学术卓越</h2>
          <p class="text-lg text-white/80 max-w-lg leading-relaxed">
            为全球学术机构提供最先进、最高效的知识管理工具。我们的系统旨在通过精确的资源调度与精细化的资产管理，支持您的每一次学术研究。
          </p>
        </div>
        
        <div class="mt-12 flex items-center gap-8 opacity-75">
          <div class="flex flex-col">
            <span class="text-3xl font-extrabold">500k+</span>
            <span class="text-xs uppercase tracking-wider font-semibold">藏书管理</span>
          </div>
          <div class="h-12 w-[1px] bg-white/20"></div>
          <div class="flex flex-col">
            <span class="text-3xl font-extrabold">120+</span>
            <span class="text-xs uppercase tracking-wider font-semibold">合作院校</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Right Side: Login & Account Recovery Form -->
    <section class="w-full lg:w-[40%] bg-white flex flex-col items-center justify-center p-8 md:p-12 relative shadow-2xl">
      <!-- Mobile Header Branding (Shown only on small screens) -->
      <div class="lg:hidden absolute top-8 left-8 flex items-center gap-2">
        <BookOpen class="h-6 w-6 text-[#00236F]" />
        <span class="text-xl font-bold text-[#00236F]">图书管理系统</span>
      </div>

      <div class="w-full max-w-md">
        <!-- 1. Forgot password mode state -->
        <div v-if="isForgotMode" class="space-y-6">
          <button 
            @click="isForgotMode = false" 
            class="flex items-center gap-2 text-xs font-semibold text-[#57657A] hover:text-[#00236F] transition-colors cursor-pointer group"
          >
            <ArrowLeft class="h-4 w-4 transition-transform group-hover:-translate-x-1" />
            返回管理员登录页面
          </button>

          <!-- Step 1: Input registered mail -->
          <div v-if="forgotStep === 1" class="space-y-6">
            <div class="space-y-1">
              <h2 class="text-2xl font-black text-[#00236F] tracking-tight">找回密码安全通道</h2>
              <p class="text-xs text-[#57657A]">请输入您在学校图书馆注册时绑定的超级管理员电子邮箱</p>
            </div>

            <form @submit.prevent="handleSendCode" id="forgot-step1-form" class="space-y-4">
              <div class="space-y-2">
                <label class="block text-xs font-semibold uppercase tracking-wider text-[#444651]" for="forgot-email">绑定管理员邮箱</label>
                <div class="relative">
                  <span class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-[#757682]">
                    <Mail class="h-5 w-5" />
                  </span>
                  <input 
                    v-model="forgotEmail"
                    type="email" 
                    id="forgot-email"
                    required
                    class="block w-full h-[48px] pl-10 pr-4 bg-[#F2F4F6] border border-[#C5C5D3] rounded-lg text-sm focus:ring-2 focus:ring-[#00236F]/20 focus:border-[#00236F] transition-all outline-none" 
                    placeholder="请输入管理员电子邮箱" 
                  />
                </div>
              </div>

              <!-- General informative block -->
              <div class="p-3.5 bg-blue-50/50 rounded-xl border border-blue-100 flex gap-2.5 text-xs text-[#00236F]">
                <HelpCircle class="h-4 w-4 shrink-0 mt-0.5" />
                <p class="leading-relaxed text-slate-600">
                  我们将会向密保邮箱发送一串一次性令牌指令 (OTP)，通过此令牌您可以快速对管理面板进行重新编排加锁。默认测试邮箱预设为 <strong class="text-[#00236F]">admin@library.org</strong>。
                </p>
              </div>

              <button 
                type="submit"
                id="btn-send-verification"
                :disabled="isSendingCode"
                class="w-full h-[48px] bg-[#00236F] text-white font-semibold rounded-lg hover:bg-[#1E3A8A] transition-all flex items-center justify-center gap-2 cursor-pointer"
              >
                <span v-if="isSendingCode" class="flex items-center gap-2">
                  <svg class="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  极速下发令牌中...
                </span>
                <span v-else>获取双因子数字验证码</span>
              </button>
            </form>
          </div>

          <!-- Step 2: Verification code and password override -->
          <div v-else-if="forgotStep === 2" class="space-y-6">
            <div class="space-y-1">
              <h2 class="text-2xl font-black text-[#00236F] tracking-tight">安全鉴权与重置超级管理员密码</h2>
              <p class="text-xs text-[#57657A]">请输入从电子邮箱获取的四位特设验证码及设定新密码</p>
            </div>

            <!-- Testing help banner on layout -->
            <div class="px-4 py-3 bg-[#E0F2FE]/40 border border-sky-100 rounded-xl text-xs flex gap-2 text-sky-800 font-medium">
              <CheckCircle2 class="h-4.5 w-4.5 text-sky-600 shrink-0 mt-0.5" />
              <div>
                <p>演示环境密保信使通知：</p>
                <p class="text-[#00236F] mt-0.5 font-bold">重置数字验证码已安全发送！本次演示测试验证码为：8888</p>
              </div>
            </div>

            <form @submit.prevent="handleResetSubmit" id="forgot-step2-form" class="space-y-4">
              <!-- Verification code input -->
              <div class="space-y-1.5">
                <label class="block text-xs font-semibold uppercase tracking-wider text-[#444651]" for="verification-code">四位数字安全确认验证码</label>
                <input 
                  v-model="verificationCode"
                  type="text" 
                  id="verification-code"
                  required
                  maxlength="4"
                  class="block w-full h-[46px] px-3.5 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] rounded-lg text-sm outline-none font-mono tracking-widest text-center text-lg font-bold" 
                  placeholder="请输入四位验证码，例如: 8888" 
                />
              </div>

              <!-- New Password input -->
              <div class="space-y-1.5">
                <label class="block text-xs font-semibold uppercase tracking-wider text-[#444651]" for="new-password">设置超级管理员新密码</label>
                <div class="relative">
                  <span class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-[#757682]">
                    <Lock class="h-5 w-5" />
                  </span>
                  <input 
                    v-model="newPassword"
                    :type="showNewPassword ? 'text' : 'password'" 
                    id="new-password"
                    required
                    class="block w-full h-[48px] pl-10 pr-10 bg-[#F2F4F6] border border-[#C5C5D3] focus:border-[#00236F] rounded-lg text-sm outline-none" 
                    placeholder="请输入新管理员密码" 
                  />
                  <button 
                    type="button"
                    id="btn-toggle-new-password"
                    @click="showNewPassword = !showNewPassword"
                    class="absolute inset-y-0 right-0 pr-3 flex items-center text-[#757682] hover:text-[#00236F]"
                  >
                    <Eye v-if="!showNewPassword" class="h-4 w-4" />
                    <EyeOff v-else class="h-4 w-4" />
                  </button>
                </div>
              </div>

              <!-- Form error prompt if any -->
              <div v-if="codeErrorMsg" class="p-3 bg-red-50 text-red-800 text-xs rounded-xl border border-red-100 flex gap-2">
                <ShieldAlert class="h-4 w-4 shrink-0" />
                <span>{{ codeErrorMsg }}</span>
              </div>

              <button 
                type="submit"
                id="btn-confirm-reset"
                :disabled="isResettingPassword"
                class="w-full h-[48px] bg-[#00236F] text-white font-semibold rounded-lg hover:bg-[#1E3A8A] transition-all flex items-center justify-center gap-2 cursor-pointer"
              >
                <span v-if="isResettingPassword" class="flex items-center gap-2">
                  <svg class="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  应用新口令中...
                </span>
                <span v-else>确认重置密码并复核</span>
              </button>
            </form>
          </div>

          <!-- Step 3: Password recovery success visual cue -->
          <div v-else-if="forgotStep === 3" id="forgot-success-section" class="space-y-6 text-center py-4">
            <div class="inline-flex p-3.5 bg-emerald-50 text-emerald-800 border border-emerald-100 rounded-full mb-1">
              <CheckCircle2 class="h-10 w-10 text-emerald-600 animate-pulse" />
            </div>

            <div class="space-y-2">
              <h3 class="text-2xl font-black text-emerald-950 tracking-tight">重置密码成功！</h3>
              <p class="text-xs text-[#57657A] px-2 leading-relaxed">
                您的新密码已成功应用至超级管理员中控。新密码及密保邮箱已准备就绪。
              </p>
            </div>

            <button 
              @click="finishRecoveryAndLogin"
              id="btn-recovery-login-return"
              class="w-full h-[48px] bg-emerald-800 text-white font-semibold rounded-lg hover:bg-emerald-900 transition-all flex items-center justify-center gap-2 shadow-md shadow-emerald-800/10 cursor-pointer"
            >
              立刻返回主界面登录
            </button>
          </div>
        </div>

        <!-- 2. Normal Login Mode State -->
        <div v-else class="space-y-8">
          <!-- Header -->
          <div class="text-center lg:text-left">
            <h2 class="text-3xl font-bold text-[#00236F] mb-2 tracking-tight">欢迎回来</h2>
            <p class="text-sm text-[#444651]">请登录您的管理员账户以访问系统</p>
          </div>

          <!-- Form -->
          <form @submit.prevent="handleLoginSubmit" class="space-y-6">
            <!-- Username Field -->
            <div class="space-y-2">
              <label class="block text-xs font-semibold uppercase tracking-wider text-[#444651]" for="username">
                用户名 / ID
              </label>
              <div class="relative group">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-[#757682]">
                  <User class="h-5 w-5" />
                </div>
                <input 
                  v-model="username"
                  type="text" 
                  id="username"
                  required
                  class="block w-full h-[48px] pl-10 pr-4 bg-[#F2F4F6] border border-[#C5C5D3] rounded-lg text-sm focus:ring-2 focus:ring-[#00236F]/20 focus:border-[#00236F] transition-all outline-none" 
                  placeholder="请输入管理员ID或邮箱" 
                />
              </div>
            </div>

            <!-- Password Field -->
            <div class="space-y-2">
              <div class="flex justify-between items-end">
                <label class="block text-xs font-semibold uppercase tracking-wider text-[#444651]" for="password">
                  密码
                </label>
                <button 
                  type="button"
                  class="text-xs text-[#00236F] font-bold hover:underline cursor-pointer" 
                  @click="openForgotMode"
                >
                  忘记密码？
                </button>
              </div>
              <div class="relative group">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-[#757682]">
                  <Lock class="h-5 w-5" />
                </div>
                <input 
                  v-model="password"
                  :type="showPassword ? 'text' : 'password'" 
                  id="password"
                  required
                  class="block w-full h-[48px] pl-10 pr-10 bg-[#F2F4F6] border border-[#C5C5D3] rounded-lg text-sm focus:ring-2 focus:ring-[#00236F]/20 focus:border-[#00236F] transition-all outline-none" 
                  placeholder="请输入您的密码" 
                />
                <button 
                  type="button"
                  @click="togglePasswordVisibility"
                  class="absolute inset-y-0 right-0 pr-3 flex items-center text-[#757682] hover:text-[#00236F]"
                >
                  <Eye v-if="!showPassword" class="h-4 w-4" />
                  <EyeOff v-else class="h-4 w-4" />
                </button>
              </div>
            </div>

            <!-- Remember Me -->
            <div class="flex items-center">
              <input 
                id="remember-me" 
                type="checkbox"
                checked
                class="w-4 h-4 text-[#00236F] border-[#C5C5D3] rounded focus:ring-[#00236F]"
              />
              <label class="ml-2 text-sm text-[#444651] cursor-pointer" for="remember-me">记住我</label>
            </div>

            <!-- Login Error -->
            <div v-if="loginError" class="p-3 bg-red-50 text-red-800 text-xs rounded-xl border border-red-100 flex gap-2">
              <ShieldAlert class="h-4 w-4 shrink-0" />
              <span>{{ loginError }}</span>
            </div>

            <!-- Action Button -->
            <button 
              type="submit"
              :disabled="isSubmitting"
              class="w-full h-[48px] bg-[#00236F] text-white font-semibold rounded-lg hover:bg-[#1E3A8A] active:scale-[0.98] transition-all flex items-center justify-center gap-2 shadow-lg shadow-[#00236F]/10 disabled:opacity-80"
            >
              <span v-if="isSubmitting" class="flex items-center gap-2">
                <svg class="animate-spin h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                验证中...
              </span>
              <span v-else class="flex items-center gap-2">
                登录系统
              </span>
            </button>
          </form>

          <!-- Footer Links -->
          <div class="pt-6 border-t border-[#C5C5D3]/30 flex flex-col items-center gap-4">
            <p class="text-sm text-[#444651]">
              需要帮助？联系 <a class="text-[#00236F] font-semibold hover:underline" href="#" @click.prevent="openForgotMode">系统管理员</a>
            </p>
            <div class="flex gap-4">
              <button class="flex items-center gap-1 text-xs text-[#757682] hover:text-[#00236F] transition-colors">
                <Globe class="h-3.5 w-3.5" />
                简体中文
              </button>
              <button class="flex items-center gap-1 text-xs text-[#757682] hover:text-[#00236F] transition-colors">
                <HelpCircle class="h-3.5 w-3.5" />
                技术支持
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer Compliance (Bottom of right side) -->
      <div class="absolute bottom-4 text-center">
        <p class="text-xs text-[#757682]">
          © 2026 图书管理系统 - 版权所有
        </p>
      </div>
    </section>
  </main>
</template>
