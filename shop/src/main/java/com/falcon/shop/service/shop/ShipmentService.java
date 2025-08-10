package com.falcon.shop.service.shop;

import java.util.List;

import com.falcon.shop.domain.shop.Shipments;
import com.falcon.shop.service.BaseService;

public interface ShipmentService extends BaseService<Shipments> {

    List<Shipments> listByUser(Long userNo);

}
