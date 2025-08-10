package com.falcon.shop.service.system;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.system.CodeGroups;
import com.falcon.shop.service.BaseService;

public interface CodeGroupsService extends BaseService<CodeGroups> {

    public PageInfo<CodeGroups> page(QueryParams queryParams);

}
