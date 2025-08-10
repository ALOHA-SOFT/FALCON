package com.falcon.shop.mapper.products;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.products.Category;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    public List<Category> list(Map<String, Object> params);

    public Category select(Long no);
    public Category selectById(String id);
  
  
}
