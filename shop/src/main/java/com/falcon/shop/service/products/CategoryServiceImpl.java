package com.falcon.shop.service.products;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.Category;
import com.falcon.shop.mapper.products.CategoryMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class CategoryServiceImpl extends BaseServiceImpl<Category, CategoryMapper> implements CategoryService {

    @Autowired private CategoryMapper categoryMapper;

    @Override
    public PageInfo<Category> page(QueryParams queryParams) {
        int page = (int) queryParams.getPage();
        int size = (int) queryParams.getSize();
        log.info("queryParams : {}", queryParams);
        PageHelper.startPage(page, size);
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        List<Category> list = categoryMapper.list(params);
        return new PageInfo<>(list);
    }

    @Override
    public Category select(Long no) {
        log.info("select no : {}", no);
        Category category = categoryMapper.select(no);
        if (category == null) {
            log.warn("Category not found for no: {}", no);
            return null;
        }
        return category;
    }

    @Override
    public Category selectById(String id) {
        log.info("selectById id : {}", id);
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            log.warn("Category not found for id: {}", id);
            return null;
        }
        return category;
    }

    @Override
    public PageInfo<Category> page(int page, int size) {
        log.info("page request - page: {}, size: {}", page, size);
        PageHelper.startPage(page, size);
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("seq").orderByDesc("no");
        List<Category> list = categoryMapper.selectList(queryWrapper);
        return new PageInfo<>(list);
    }

    


    

  
}
