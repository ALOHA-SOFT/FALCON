package com.falcon.shop.mapper.products;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.products.Options;

@Mapper
public interface OptionMapper extends BaseMapper<Options> {

    public List<Options> listWithParams(Map<String, Object> params);

}
