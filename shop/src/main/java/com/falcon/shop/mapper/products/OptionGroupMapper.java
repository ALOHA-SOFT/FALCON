package com.falcon.shop.mapper.products;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.products.OptionGroup;

@Mapper
public interface OptionGroupMapper extends BaseMapper<OptionGroup> {

    public List<OptionGroup> listWithParams(HashMap<String, Object> params);

}
