package com.falcon.shop.service.admin;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.admin.Banners;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.service.BaseService;

public interface BannerService extends BaseService<Banners> {
  
    PageInfo<Banners> page(QueryParams queryParams);
  
    List<Banners> listByType(String type);
}
