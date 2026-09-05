package com.library.config;

import com.library.entity.*;
import com.library.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AdminMapper adminMapper;
    private final BookMapper bookMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (adminMapper.count() > 0) {
            log.info("数据已初始化，跳过...");
            return;
        }

        log.info("开始初始化数据...");

        // Admin
        Admin admin = Admin.builder()
                .username("admin@library.org")
                .password(passwordEncoder.encode("admin123"))
                .name("管理员")
                .role("馆长")
                .avatar("https://lh3.googleusercontent.com/aida-public/AB6AXuC1aWyzzBdkEa3OitasgWmAgmyslrGHUbUBzfEYEFxtT4MBIOnSL0kOvZa-N2iGsNQcBINJczbYG0k8n1kbSd8Tlb5kpaSq32_OjVyt05_HHnrmt7PFC_IIAt_Awly5EmujX60PC03c-kcKjwCa69PqbGYnlphPYujKNcD81iTdYAIPZK7GPr6z_rijyPOF-YceJPR1CbjSe2iJcY33ejC_6kD-hLuRkX72FhXhCVmP_29IbuimQsfA64mT2UwVZryQPbN7I5yX4BE")
                .build();
        adminMapper.insert(admin);

        // Readers - 初始为空，不预置数据

        // Books
        bookMapper.insert(Book.builder()
                .id("b1").title("量子力学原理").author("R. Shankar").isbn("978-0306447907")
                .category("科学").status("available")
                .cover("https://lh3.googleusercontent.com/aida-public/AB6AXuCrEK_T0EejfxwDC_Zm5vQUlxpYcyy4aGZwS-r1l7_t9Ff-Ox0XER31vwfXVWi6i5GMrLg0VEE2LgERuY_I3CthEZNKdnYjaQ6G8kd65sd0fxha_hlFCZ_uTyZzo3txwFI6h8T4F1aIUX-hdhLa3THOG4UgyNKeJ_xSzkAy7DTJmMv9AVVpOEoOvbl2zQluczenvDTkH51YYRwQlaWqnY_3ireIjdj6J77e5uFNB05VRkRejcJV3XTjwu69ytvnVQ5KGcFvDd2SyoY")
                .build());
        bookMapper.insert(Book.builder()
                .id("b2").title("伟大的帝国：全球通史").author("Jane Doe").isbn("978-0141397856")
                .category("历史").status("borrowed")
                .cover("https://lh3.googleusercontent.com/aida-public/AB6AXuGOwexxKGJ7znd9cz3YqZwMVmnz3D9wSDi2PmI7_xcNZE9qKFMxB5dS4l9yE7pNgvN_zMLlqoc8syIavY0L22vOIqgTFMN08aYtByO9DKmKVvUlZHoIwDL-bFYAIvJi7svWlmiWtGlZukauZkpb6dFB5Nw4i7R7IxEJp1hSPoYoHZ1SQwx8CDDxbR9p3Fxc60iCaJCb0OS5LJgDjVaG36vHkxMreARkpzLYf1ZtZ4pQ73gCYskiwoSBhDVo67PgpKLhthAMtB1Jas")
                .build());
        bookMapper.insert(Book.builder()
                .id("b3").title("霓虹回响：现代寓言").author("Arthur P. Clark").isbn("978-0062457714")
                .category("小说").status("available")
                .cover("https://lh3.googleusercontent.com/aida-public/AB6AXuB3cDFa_wMWrUkeFGMt7Zzn-ZyEmKH8ha4xmTkA0enELkNCQ5KrpMJ1qSvlZLYRR7OLq2fU3f7SWDRfXOYpTliRSKz49SStObhg9hbBaPPlXJ3Qd8PzZ7ZlONbjDeafhdavM3UDVrcZUiPoTgj4xfKIbVnvmdnId_5gUALVrUObUq0I1h65zholNPqjbCBMtJmEt07xdCTzS8cAbDxSiNNnCBYnGOwfBB_zhUQEG_d8EnZ1-4UW9_Ga0lXPZesxB5jXfabTp3vhCpk")
                .build());
        bookMapper.insert(Book.builder()
                .id("b4").title("数据结构与算法").author("Thomas H. Cormen").isbn("978-0262033848")
                .category("技术").status("overdue")
                .cover("https://lh3.googleusercontent.com/aida-public/AB6AXuD2mPjSFaMag3Wm7RHugr4R11IhLghDhQjMWbG7Lf9YwnruJtBLo4QMu1qUSwKCRD9-jZERO1PKLW2zg-yldqcuq3unBEcC3hySgINr0uz6eOXYcRv3y2HRoiwsYc_c8RR5UI3FJhl9yjTJQA3Zk4zHUZ990qIxcxgpq4ntXVT3nFVyW32-eg3Pn1CaUvs2GIBAd5SCdV8_2hvdlVOPa8WCInh7QEblBm41Z0KfZ9m_8e4uGbmK3fjkpsz9EqgZYvHYGxNyxSxrbSk")
                .build());

        // Borrowings - 初始为空，不预置数据

        // Notifications - 初始为空，不预置数据

        log.info("数据初始化完成！");
    }
}
