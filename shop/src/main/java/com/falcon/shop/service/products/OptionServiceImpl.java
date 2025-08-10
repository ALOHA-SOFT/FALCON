package com.falcon.shop.service.products;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.Options;
import com.falcon.shop.mapper.products.OptionMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OptionServiceImpl extends BaseServiceImpl<Options, OptionMapper> implements OptionService {

    @Autowired
    private OptionMapper optionMapper;

    @Override
    public PageInfo<Options> page(QueryParams queryParams) {
        // 페이지 시작 
        PageHelper.startPage(queryParams.getPage(), queryParams.getSize());
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        // 검색 및 정렬 조건을 포함하여 데이터 조회
        return new PageInfo<>(optionMapper.listWithParams(params));
    }
  
}
