package com.falcon.shop.service.system;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.system.Seq;
import com.falcon.shop.service.BaseService;

public interface SeqService extends BaseService<Seq> {

    public PageInfo<Seq> page(QueryParams queryParams);
}
