package com.falcon.shop.service.products;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.Products;
import com.falcon.shop.service.BaseService;

public interface ProductService extends BaseService<Products> {
    
    PageInfo<Products> page(QueryParams queryParams);
    PageInfo<Products> pageBEST(QueryParams queryParams);
    PageInfo<Products> pageNEW(QueryParams queryParams);
    public Products selectById(String id);
    
    PageInfo<Products> page(QueryParams queryParams, Long categoryNo);
    PageInfo<Products> page(QueryParams queryParams, Long categoryNo, Long categoryLargeNo);

    public List<Products> relatedList(Long categoryNo);
    public List<Products> randomList();
}
