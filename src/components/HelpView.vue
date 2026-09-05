<script setup lang="ts">
import { BookOpen, Award, ShieldAlert, Sparkles, HelpCircle } from 'lucide-vue-next';
</script>

<template>
  <div class="space-y-6 text-[#191C1E]">
    <!-- Page Header -->
    <div>
      <h2 class="text-3xl font-bold text-[#00236F] tracking-tight">帮助中心 & 知识手册</h2>
      <p class="text-sm text-[#444651] mt-1 text-opacity-80">在这里了解系统规章守则、会员账户差异以及图书智能管理的标准流程。</p>
    </div>

    <!-- Quick Manual Cards Bento -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 pt-2">
      <!-- Digital Database Rules -->
      <div class="bg-white p-6 border border-[#E0E3E5] rounded-xl shadow-sm space-y-4">
        <div class="flex items-center gap-3">
          <div class="p-2 bg-[#D5E3FC] text-[#00236F] rounded-lg">
            <BookOpen class="h-5 w-5" />
          </div>
          <h3 class="font-bold text-base text-[#191C1E]">全馆电子文献与纸质藏书流转条例</h3>
        </div>
        <p class="text-xs text-[#444651] leading-relaxed">
          图书流转是图书馆系统的核心。对于每一本被借记出库的书籍，其预计借记时间为：普通标准借记14天，长线宽限借记30天。借记期间，系统将每隔7天自动进行线上推送或排队，并在到期前3天发送温和的书面通知至对应读者。
        </p>
      </div>

      <!-- Membership Differences -->
      <div class="bg-white p-6 border border-[#E0E3E5] rounded-xl shadow-sm space-y-4">
        <div class="flex items-center gap-3">
          <div class="p-2 bg-[#D5E3FC] text-[#00236F] rounded-lg">
            <Award class="h-5 w-5" />
          </div>
          <h3 class="font-bold text-base text-[#191C1E]">高级会籍与普通会籍的差异</h3>
        </div>
        <p class="text-xs text-[#444651] leading-relaxed">
          我们的会籍分为 **普通会员** 与 **高级会员**：
          <br />• <strong>普通会员</strong>：每次借记限额 <strong>3 本纸质藏书</strong>，不提供远程专属多媒体数据库的连接许可。
          <br />• <strong>高级会员</strong>：每次最高限额 <strong>10 本纸质藏书</strong>，且可以无限量查询和下载学术专属文献库中的特约数字资源库。
        </p>
      </div>

      <!-- Overdue Penalties -->
      <div class="bg-white p-6 border border-[#E0E3E5] rounded-xl shadow-sm space-y-4">
        <div class="flex items-center gap-3">
          <div class="p-2 bg-[#FFDAD6] text-[#BA1A1A] rounded-lg">
            <ShieldAlert class="h-5 w-5" />
          </div>
          <h3 class="font-bold text-base text-[#191C1E]">逾期未还的处理与超期罚息制度</h3>
        </div>
        <p class="text-xs text-[#444651] leading-relaxed">
          当任意读者的书籍出现逾期时，该读者的会籍凭证将被进入 **系统管控期**。借阅人会收到自动化催还提醒通知。凡超期未还超过60天以上的书籍，系统将认定其会籍资产已被侵占，并默认该资产进入“确认为遗失”核销状态。
        </p>
      </div>

      <!-- Custom Micro-Controls -->
      <div class="bg-white p-6 border border-[#E0E3E5] rounded-xl shadow-sm space-y-4">
        <div class="flex items-center gap-3">
          <div class="p-2 bg-[#C9E6FF] text-[#004C6E] rounded-lg">
            <Sparkles class="h-5 w-5" />
          </div>
          <h3 class="font-bold text-base text-[#191C1E]">管理员核心调度指令流</h3>
        </div>
        <p class="text-xs text-[#444651] leading-relaxed">
          馆员管理员可以通过图书目录、借阅管理与读者管理等系统板块进行交互：
          <br />• 极速检索：顶端的全文索引输入框支持对全站任意书名、ISBN、作者、读者编号进行模糊比对过滤。
          <br />• 临时新增：对新入库书籍、新报到读者开户、以及新生借阅单，均可通过各页面的 **[新增...]** 进行一键保存录入。
        </p>
      </div>
    </div>
  </div>
</template>
