package com.falcon.shop.mapper.admin;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.admin.Banners;

@Mapper
public interface BannerMapper extends BaseMapper<Banners> {

    public List<Banners> listWithParams(Map<String,Object> params);

}
