package com.falcon.shop.service.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.admin.Banners;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.mapper.admin.BannerMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BannerServiceImpl extends BaseServiceImpl<Banners, BannerMapper> implements BannerService {

    @Autowired private BannerMapper bannerMapper;

    @Override
    public PageInfo<Banners> page(QueryParams queryParams) {
        int page = (int) queryParams.getPage();
        int size = (int) queryParams.getSize();
        log.info("queryParams : {}", queryParams);
        PageHelper.startPage(page, size);
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        List<Banners> list = bannerMapper.listWithParams(params);
        return new PageInfo<>(list);
    }

    @Override
    public List<Banners> listByType(String type) {
        QueryWrapper <Banners> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        queryWrapper.orderByAsc("seq"); // seq 순서로 정렬
        List<Banners> banners = bannerMapper.selectList(queryWrapper);
        return banners;
    }
}
