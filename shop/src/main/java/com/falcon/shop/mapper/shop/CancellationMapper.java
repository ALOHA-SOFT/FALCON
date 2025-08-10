package com.falcon.shop.mapper.shop;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.shop.Cancellations;

@Mapper
public interface CancellationMapper extends BaseMapper<Cancellations> {

    public List<Cancellations> listWithParams(Map<String, Object> params);

}
