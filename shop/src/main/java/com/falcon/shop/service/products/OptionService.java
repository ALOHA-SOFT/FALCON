package com.falcon.shop.service.products;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.Options;
import com.falcon.shop.service.BaseService;

public interface OptionService extends BaseService<Options> {
    
    PageInfo<Options> page(QueryParams queryParams);

}
