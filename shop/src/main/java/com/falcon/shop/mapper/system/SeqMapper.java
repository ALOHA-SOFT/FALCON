package com.falcon.shop.mapper.system;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.system.Seq;

@Mapper
public interface SeqMapper extends BaseMapper<Seq> {
  
    public List<Seq> list(Map<String, Object> params);
  
}
