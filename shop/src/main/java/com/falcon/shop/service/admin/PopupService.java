package com.falcon.shop.service.admin;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.admin.Popups;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.service.BaseService;

public interface PopupService extends BaseService<Popups> {
  
    PageInfo<Popups> page(QueryParams queryParams);
  
    List<Popups> listByType(String type);
    List<Popups> listByTypeOpen(String type);
}
