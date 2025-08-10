package com.falcon.shop.service.shop;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.shop.Cancellations;
import com.falcon.shop.service.BaseService;

public interface CancellationService extends BaseService<Cancellations> {

    PageInfo<Cancellations> page(QueryParams queryParams);
    PageInfo<Cancellations> page(QueryParams queryParams, List<Long> orderNos);
    PageInfo<Cancellations> page(QueryParams queryParams, Long userNo);
    PageInfo<Cancellations> page(QueryParams queryParams, Cancellations cancel);

}
