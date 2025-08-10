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
import com.falcon.shop.domain.products.CategoryLarge;
import com.falcon.shop.mapper.products.CategoryLargeMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CategoryLargeServiceImpl extends BaseServiceImpl<CategoryLarge, CategoryLargeMapper> implements CategoryLargeService {
    
    @Autowired CategoryLargeMapper categoryLargeMapper;

    @Override
    public PageInfo<CategoryLarge> page(QueryParams queryParams) {
        int page = (int) queryParams.getPage();
        int size = (int) queryParams.getSize();
        PageHelper.startPage(page, size);
        Map<String, Object> params = new HashMap<>();
        params.put("categoryNo", null); // No specific category filter
        params.put("queryParams", queryParams);
        List<CategoryLarge> list = categoryLargeMapper.listByCategory(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<CategoryLarge> listByCategory(Long categoryNo, QueryParams queryParams) {
        int page = (int) queryParams.getPage();
        int size = (int) queryParams.getSize();
        log.info("categoryNo : {}, queryParams : {}", categoryNo, queryParams);
        PageHelper.startPage(page, size);
        Map<String, Object> params = new HashMap<>();
        params.put("categoryNo", categoryNo); // No specific category filter
        params.put("queryParams", queryParams);
        List<CategoryLarge> list = categoryLargeMapper.listByCategory(params);
        return new PageInfo<>(list);
    }

    @Override
    public List<CategoryLarge> listByCategory(Long categoryNo) {
        QueryWrapper<CategoryLarge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_no", categoryNo);
        queryWrapper.orderByAsc("seq"); // Order by seq ascending
        queryWrapper.orderByDesc("no"); // Order by no descending as a secondary sort
        return categoryLargeMapper.selectList(queryWrapper);
    }


}
