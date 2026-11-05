package com.yuyukoshop.yuyukoshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuyukoshop.yuyukoshop.entity.Product;
import com.yuyukoshop.yuyukoshop.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {

    public IPage<Product> getProductPage(Integer page, Integer size, Long categoryId, 
                                         String keyword, String sortType,
                                         Double minPrice, Double maxPrice) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        // 状态为上架
        wrapper.eq(Product::getStatus, 1);
        
        // 分类筛选
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword)
                   .or()
                   .like(Product::getDescription, keyword);
        }
        
        // 价格区间筛选
        if (minPrice != null) {
            wrapper.ge(Product::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Product::getPrice, maxPrice);
        }
        
        // 排序处理
        if (StringUtils.hasText(sortType)) {
            switch (sortType) {
                case "sales_desc":
                    // 销量从高到低
                    wrapper.orderByDesc(Product::getSales);
                    break;
                case "price_asc":
                    // 价格从低到高
                    wrapper.orderByAsc(Product::getPrice);
                    break;
                case "price_desc":
                    // 价格从高到低
                    wrapper.orderByDesc(Product::getPrice);
                    break;
                case "newest":
                    // 最新上架
                    wrapper.orderByDesc(Product::getCreateTime);
                    break;
                case "hot":
                    // 人气（销量+浏览量综合）
                    wrapper.orderByDesc(Product::getSales)
                           .orderByDesc(Product::getCreateTime);
                    break;
                default:
                    // 综合排序（默认：销量优先，时间次之）
                    wrapper.orderByDesc(Product::getSales)
                           .orderByDesc(Product::getCreateTime);
                    break;
            }
        } else {
            // 默认综合排序
            wrapper.orderByDesc(Product::getSales)
                   .orderByDesc(Product::getCreateTime);
        }
        
        return this.page(pageParam, wrapper);
    }
    
    public Product getProductDetail(Long id) {
        return this.getById(id);
    }
    
    public void increaseSales(Long productId, Integer quantity) {
        Product product = this.getById(productId);
        if (product != null) {
            product.setSales(product.getSales() + quantity);
            this.updateById(product);
        }
    }
}
