package com.falcon.shop.service.admin;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.admin.Popups;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.mapper.admin.PopupMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PopupServiceImpl extends BaseServiceImpl<Popups, PopupMapper> implements PopupService {

    @Autowired private PopupMapper popupMapper;

    @Override
    public PageInfo<Popups> page(QueryParams queryParams) {
        int page = (int) queryParams.getPage();
        int size = (int) queryParams.getSize();
        log.info("queryParams : {}", queryParams);
        PageHelper.startPage(page, size);
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        List<Popups> list = popupMapper.listWithParams(params);
        return new PageInfo<>(list);
    }

    @Override
    public List<Popups> listByType(String type) {
        QueryWrapper<Popups> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        return popupMapper.selectList(queryWrapper);
    }

    @Override
    public List<Popups> listByTypeOpen(String type) {
        QueryWrapper<Popups> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        queryWrapper.eq("is_show", true);
        queryWrapper.le("started_at", LocalDateTime.now());
        queryWrapper.ge("ended_at", LocalDateTime.now());
        return popupMapper.selectList(queryWrapper);
    }
}
