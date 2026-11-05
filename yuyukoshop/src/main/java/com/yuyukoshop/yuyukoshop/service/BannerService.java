package com.yuyukoshop.yuyukoshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuyukoshop.yuyukoshop.entity.Banner;
import com.yuyukoshop.yuyukoshop.mapper.BannerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 轮播图服务
 */
@Slf4j
@Service
public class BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    /**
     * 创建轮播图
     */
    public void createBanner(Banner banner) {
        banner.setCreateTime(LocalDateTime.now());
        banner.setUpdateTime(LocalDateTime.now());
        if (banner.getSortOrder() == null) {
            banner.setSortOrder(0);
        }
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        bannerMapper.insert(banner);
    }

    /**
     * 更新轮播图
     */
    public void updateBanner(Long id, Banner banner) {
        Banner existing = bannerMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("轮播图不存在");
        }
        banner.setId(id);
        banner.setUpdateTime(LocalDateTime.now());
        bannerMapper.updateById(banner);
    }

    /**
     * 删除轮播图
     */
    public void deleteBanner(Long id) {
        bannerMapper.deleteById(id);
    }

    /**
     * 获取轮播图详情
     */
    public Banner getBannerById(Long id) {
        return bannerMapper.selectById(id);
    }

    /**
     * 分页查询轮播图（管理后台）
     */
    public IPage<Banner> getBannerPage(Long page, Long size, Integer status) {
        Page<Banner> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Banner::getStatus, status);
        }
        wrapper.orderByAsc(Banner::getSortOrder);
        return bannerMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 获取启用的轮播图列表（首页展示）
     */
    public List<Banner> getActiveBanners() {
        return bannerMapper.selectActiveBanners();
    }

    /**
     * 更新轮播图状态
     */
    public void updateStatus(Long id, Integer status) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setStatus(status);
        banner.setUpdateTime(LocalDateTime.now());
        bannerMapper.updateById(banner);
    }
}
