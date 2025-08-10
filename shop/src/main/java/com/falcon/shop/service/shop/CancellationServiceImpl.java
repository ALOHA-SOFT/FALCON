package com.falcon.shop.service.shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.shop.Cancellations;
import com.falcon.shop.mapper.shop.CancellationMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CancellationServiceImpl extends BaseServiceImpl<Cancellations, CancellationMapper> implements CancellationService {

    @Autowired
    private CancellationMapper cancellationMapper;

    @Override
    public PageInfo<Cancellations> page(QueryParams queryParams) {
        int page = (int) queryParams.getPage();
        int size = (int) queryParams.getSize();
        log.info("queryParams : {}", queryParams);
        PageHelper.startPage(page, size);
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        List<Cancellations> list = cancellationMapper.listWithParams(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<Cancellations> page(QueryParams queryParams, List<Long> orderNos) {
        int page = (int) queryParams.getPage();
        int size = (int) queryParams.getSize();
        log.info("queryParams : {}", queryParams);
        PageHelper.startPage(page, size);
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        params.put("orderNos", orderNos);
        List<Cancellations> list = cancellationMapper.listWithParams(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<Cancellations> page(QueryParams queryParams, Long userNo) {
        int page = (int) queryParams.getPage();
        int size = (int) queryParams.getSize();
        log.info("queryParams : {}", queryParams);
        PageHelper.startPage(page, size);
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        params.put("userNo", userNo);
        List<Cancellations> list = cancellationMapper.listWithParams(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<Cancellations> page(QueryParams queryParams, Cancellations cancel) {
        int page = (int) queryParams.getPage();
        int size = (int) queryParams.getSize();
        log.info("queryParams : {}", queryParams);
        PageHelper.startPage(page, size);
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        params.put("status", cancel.getStatus());
        params.put("type", cancel.getType());
        List<Cancellations> list = cancellationMapper.listWithParams(params);
        return new PageInfo<>(list);
    }
}
