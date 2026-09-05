package com.library.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reader {
    private String id;
    private String name;
    private String email;
    private String level;
    private Integer borrows;
    private String avatar;
}
