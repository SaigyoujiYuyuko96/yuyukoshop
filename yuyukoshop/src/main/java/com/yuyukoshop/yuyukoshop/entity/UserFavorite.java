package com.yuyukoshop.yuyukoshop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_favorite")
public class UserFavorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long productId;

    private LocalDateTime createTime;
}
