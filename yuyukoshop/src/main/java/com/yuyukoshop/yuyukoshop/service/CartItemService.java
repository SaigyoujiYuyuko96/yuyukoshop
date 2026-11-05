package com.yuyukoshop.yuyukoshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuyukoshop.yuyukoshop.dto.CartItemDto;
import com.yuyukoshop.yuyukoshop.entity.CartItem;
import com.yuyukoshop.yuyukoshop.entity.Product;
import com.yuyukoshop.yuyukoshop.exception.BusinessException;
import com.yuyukoshop.yuyukoshop.mapper.CartItemMapper;
import com.yuyukoshop.yuyukoshop.mapper.ProductMapper;
import com.yuyukoshop.yuyukoshop.vo.CartItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartItemService {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Transactional
    public void addCartItem(Long userId, CartItemDto dto) {
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }
        if (product.getStock() < dto.getQuantity()) {
            throw new BusinessException("商品库存不足");
        }

        CartItem existingItem = cartItemMapper.selectOne(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .eq(CartItem::getProductId, dto.getProductId())
        );

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
            existingItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.updateById(existingItem);
        } else {
            CartItem cartItem = CartItem.builder()
                    .userId(userId)
                    .productId(dto.getProductId())
                    .quantity(dto.getQuantity())
                    .price(product.getPrice())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            cartItemMapper.insert(cartItem);
        }
    }

    public void removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemMapper.selectById(cartItemId);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }
        cartItemMapper.deleteById(cartItemId);
    }

    public List<CartItemVo> getCartItems(Long userId) {
        List<CartItem> cartItems = cartItemMapper.selectList(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .orderByDesc(CartItem::getCreateTime)
        );

        return cartItems.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    public void clearCartByUserId(Long userId) {
        cartItemMapper.delete(
                new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId)
        );
    }

    private CartItemVo convertToVo(CartItem cartItem) {
        Product product = productMapper.selectById(cartItem.getProductId());
        return CartItemVo.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProductId())
                .productName(product != null ? product.getName() : "")
                .productThumbnail(product != null ? product.getThumbnail() : "")
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .subtotal(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .createTime(cartItem.getCreateTime())
                .build();
    }
}
