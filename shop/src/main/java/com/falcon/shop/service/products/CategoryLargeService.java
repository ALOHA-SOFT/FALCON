package com.falcon.shop.service.products;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.CategoryLarge;
import com.falcon.shop.service.BaseService;

public interface CategoryLargeService extends BaseService<CategoryLarge> {

    PageInfo<CategoryLarge> page(QueryParams queryParams);
    PageInfo<CategoryLarge> listByCategory(Long categoryNo, QueryParams queryParams);
    List<CategoryLarge> listByCategory(Long categoryNo);

}
