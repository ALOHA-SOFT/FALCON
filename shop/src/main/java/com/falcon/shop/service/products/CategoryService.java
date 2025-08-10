package com.falcon.shop.service.products;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.Category;
import com.falcon.shop.service.BaseService;

public interface CategoryService extends BaseService<Category> {

    public PageInfo<Category> page(int page, int size);
    public PageInfo<Category> page(QueryParams queryParams);

    public Category select(Long no);
    public Category selectById(String id);

}
