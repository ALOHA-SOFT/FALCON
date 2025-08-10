package com.falcon.shop.mapper.system;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.system.CodeGroups;

@Mapper
public interface CodeGroupsMapper extends BaseMapper<CodeGroups> {

    public List<CodeGroups> list(Map<String, Object> params);
  
}
