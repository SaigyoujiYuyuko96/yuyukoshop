package com.yuyukoshop.yuyukoshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuyukoshop.yuyukoshop.entity.Product;
import com.yuyukoshop.yuyukoshop.entity.UserFavorite;
import com.yuyukoshop.yuyukoshop.exception.BusinessException;
import com.yuyukoshop.yuyukoshop.mapper.ProductMapper;
import com.yuyukoshop.yuyukoshop.mapper.UserFavoriteMapper;
import com.yuyukoshop.yuyukoshop.vo.FavoriteVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserFavoriteService {

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Autowired
    private ProductMapper productMapper;

    public void addFavorite(Long userId, Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在");
        }

        Long count = userFavoriteMapper.selectCount(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getProductId, productId)
        );
        if (count > 0) {
            throw new BusinessException("已收藏该商品");
        }

        UserFavorite favorite = UserFavorite.builder()
                .userId(userId)
                .productId(productId)
                .createTime(LocalDateTime.now())
                .build();
        userFavoriteMapper.insert(favorite);
    }

    public void removeFavorite(Long userId, Long productId) {
        userFavoriteMapper.delete(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getProductId, productId)
        );
    }

    public List<FavoriteVo> getFavorites(Long userId) {
        List<UserFavorite> favorites = userFavoriteMapper.selectList(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .orderByDesc(UserFavorite::getCreateTime)
        );

        if (favorites.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = favorites.stream()
                .map(UserFavorite::getProductId)
                .collect(Collectors.toList());

        List<Product> products = productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .in(Product::getId, productIds)
                        .eq(Product::getStatus, 1)
        );

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        return favorites.stream()
                .filter(f -> productMap.containsKey(f.getProductId()))
                .map(f -> {
                    Product p = productMap.get(f.getProductId());
                    return FavoriteVo.builder()
                            .id(f.getId())
                            .productId(p.getId())
                            .productName(p.getName())
                            .productImage(p.getThumbnail())
                            .productPrice(p.getPrice())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
