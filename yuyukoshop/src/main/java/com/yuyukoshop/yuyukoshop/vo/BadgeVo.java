package com.yuyukoshop.yuyukoshop.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeVo {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private String type;
    private LocalDateTime earnedTime;
    private Boolean earned;
}
