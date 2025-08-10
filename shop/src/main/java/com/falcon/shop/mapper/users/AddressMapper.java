package com.falcon.shop.mapper.users;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.users.Address;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {

}
