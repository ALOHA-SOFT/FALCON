package com.falcon.shop.mapper.admin;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.admin.Popups;

@Mapper
public interface PopupMapper extends BaseMapper<Popups> {

    public List<Popups> listWithParams(Map<String, Object> params);

}
