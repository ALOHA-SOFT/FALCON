package com.falcon.shop.service.products;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.Products;
import com.falcon.shop.mapper.products.ProductMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductServiceImpl extends BaseServiceImpl<Products, ProductMapper> implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public PageInfo<Products> page(QueryParams queryParams) {
        // 페이지 시작 
        PageHelper.startPage(queryParams.getPage(), queryParams.getSize());
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        // 검색 및 정렬 조건을 포함하여 데이터 조회
        return new PageInfo<>(productMapper.listWithParams(params));
    }

    @Override
    public PageInfo<Products> pageBEST(QueryParams queryParams) {
        // 페이지 시작 
        PageHelper.startPage(queryParams.getPage(), queryParams.getSize());
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        params.put("isBest", true); // 베스트 상품 필터링
        return new PageInfo<>(productMapper.listWithParams(params));
    }

    @Override
    public PageInfo<Products> pageNEW(QueryParams queryParams) {
        // 페이지 시작
        PageHelper.startPage(queryParams.getPage(), queryParams.getSize());
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        params.put("isNew", true); // 신규 상품 필터링
        return new PageInfo<>(productMapper.listWithParams(params));
    }


    @Override
    public Products selectById(String id) {
        return productMapper.selectById(id);
    }

    @Override
    public PageInfo<Products> page(QueryParams queryParams, Long categoryNo) {
        // 페이지 시작 
        PageHelper.startPage(queryParams.getPage(), queryParams.getSize());
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        params.put("categoryNo", categoryNo); // 카테고리 번호 필터링
        return new PageInfo<>(productMapper.listWithParams(params));
    }

    @Override
    public PageInfo<Products> page(QueryParams queryParams, Long categoryNo, Long categoryLargeNo) {
        // 페이지 시작
        PageHelper.startPage(queryParams.getPage(), queryParams.getSize());
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        params.put("categoryNo", categoryNo); // 카테고리 번호 필터링
        params.put("categoryLargeNo", categoryLargeNo); // 대분류 번호 필터링
        return new PageInfo<>(productMapper.listWithParams(params));
    }

    @Override
    public List<Products> relatedList(Long categoryNo) {
        if (categoryNo == null || categoryNo <= 0) {
            log.error("카테고리 번호가 제공되지 않았습니다.");
            throw new IllegalArgumentException("카테고리 번호가 필요합니다.");
        }
        return productMapper.relatedList(categoryNo);
    }

    @Override
    public List<Products> randomList() {
        return productMapper.randomList();
    }

}

