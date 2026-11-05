package com.yuyukoshop.yuyukoshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuyukoshop.yuyukoshop.entity.Banner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 轮播图Mapper
 */
@Mapper
public interface BannerMapper extends BaseMapper<Banner> {

    /**
     * 查询启用的轮播图，按排序字段排序
     */
    @Select("SELECT * FROM banner WHERE status = 1 ORDER BY sort_order ASC")
    List<Banner> selectActiveBanners();
}
