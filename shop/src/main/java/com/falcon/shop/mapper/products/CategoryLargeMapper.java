package com.falcon.shop.mapper.products;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.products.CategoryLarge;

@Mapper
public interface CategoryLargeMapper extends BaseMapper<CategoryLarge> {

    public List<CategoryLarge> listByCategory(Map<String, Object> params);
  
}
