package com.falcon.shop.mapper.users;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.users.Carts;

@Mapper
public interface CartMapper extends BaseMapper<Carts> {

    List<Carts> listByUser(Map<String, Object> params);

    Carts selectById(String id);

}
