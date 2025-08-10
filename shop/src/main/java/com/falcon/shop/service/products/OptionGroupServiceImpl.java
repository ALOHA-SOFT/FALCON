package com.falcon.shop.service.products;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.OptionGroup;
import com.falcon.shop.domain.products.Options;
import com.falcon.shop.mapper.products.OptionGroupMapper;
import com.falcon.shop.mapper.products.OptionMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OptionGroupServiceImpl extends BaseServiceImpl<OptionGroup, OptionGroupMapper> implements OptionGroupService {

    @Autowired private OptionGroupMapper optionGroupMapper;
    @Autowired private OptionMapper optionMapper;

    @Override
    public PageInfo<OptionGroup> page(QueryParams queryParams) {
        // 페이지 시작 
        PageHelper.startPage(queryParams.getPage(), queryParams.getSize());
        HashMap<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        // 검색 및 정렬 조건을 포함하여 데이터 조회
        List<OptionGroup> list = optionGroupMapper.listWithParams(params);
        return new PageInfo<>(list);
    }

    @Override
    public OptionGroup select(Long no) {
        if (no == null) {
            return null;
        }
        // 옵션 그룹 조회
        OptionGroup optionGroup = optionGroupMapper.selectById(no);
        if (optionGroup != null) {
            // 옵션 그룹에 속한 옵션 리스트 조회
            QueryWrapper<Options> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("group_no", no);
            optionGroup.setOptions(optionMapper.selectList(queryWrapper));
        }
        return optionGroup;
    }

    
  
}
