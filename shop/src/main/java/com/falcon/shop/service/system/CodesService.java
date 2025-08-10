package com.falcon.shop.service.system;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.system.Codes;
import com.falcon.shop.service.BaseService;

public interface CodesService extends BaseService<Codes> {

    public PageInfo<Codes> page(QueryParams queryParams);

}
