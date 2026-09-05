package com.library.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private String avatar;
}
