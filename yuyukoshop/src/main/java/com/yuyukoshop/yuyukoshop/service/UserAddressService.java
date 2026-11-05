package com.yuyukoshop.yuyukoshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuyukoshop.yuyukoshop.dto.UserAddressDto;
import com.yuyukoshop.yuyukoshop.entity.UserAddress;
import com.yuyukoshop.yuyukoshop.exception.BusinessException;
import com.yuyukoshop.yuyukoshop.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Transactional
    public void addAddress(Long userId, UserAddressDto dto) {
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        }

        UserAddress address = UserAddress.builder()
                .userId(userId)
                .receiverName(dto.getReceiverName())
                .receiverPhone(dto.getReceiverPhone())
                .province(dto.getProvince())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .detailAddress(dto.getDetailAddress())
                .isDefault(dto.getIsDefault() != null ? dto.getIsDefault() : 0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        userAddressMapper.insert(address);
    }

    public List<UserAddress> getAddresses(Long userId) {
        return userAddressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .orderByDesc(UserAddress::getIsDefault)
                        .orderByDesc(UserAddress::getCreateTime)
        );
    }

    @Transactional
    public void updateAddress(Long userId, Long addressId, UserAddressDto dto) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        }

        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : address.getIsDefault());
        address.setUpdateTime(LocalDateTime.now());
        userAddressMapper.updateById(address);
    }

    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }
        userAddressMapper.deleteById(addressId);
    }

    private void clearDefaultAddress(Long userId) {
        List<UserAddress> addresses = userAddressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .eq(UserAddress::getIsDefault, 1)
        );
        for (UserAddress addr : addresses) {
            addr.setIsDefault(0);
            addr.setUpdateTime(LocalDateTime.now());
            userAddressMapper.updateById(addr);
        }
    }
}
