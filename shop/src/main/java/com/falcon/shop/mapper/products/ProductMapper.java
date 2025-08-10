package com.falcon.shop.mapper.products;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.products.Products;

@Mapper
public interface ProductMapper extends BaseMapper<Products> {

    public List<Products> listWithParams(Map<String, Object> params);

    public Products selectById(String id);

    public List<Products> relatedList(Long categoryNo);

    public List<Products> randomList();

}
