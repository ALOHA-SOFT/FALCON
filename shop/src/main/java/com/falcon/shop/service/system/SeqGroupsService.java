package com.falcon.shop.service.system;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.system.SeqGroups;
import com.falcon.shop.service.BaseService;

public interface SeqGroupsService extends BaseService<SeqGroups> {

    public PageInfo<SeqGroups> page(QueryParams queryParams);
}
