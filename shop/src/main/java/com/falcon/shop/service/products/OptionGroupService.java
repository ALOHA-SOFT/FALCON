package com.falcon.shop.service.products;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.OptionGroup;
import com.falcon.shop.service.BaseService;

public interface OptionGroupService extends BaseService<OptionGroup> {
    
    PageInfo<OptionGroup> page(QueryParams queryParams);
    OptionGroup select(Long no);
}
