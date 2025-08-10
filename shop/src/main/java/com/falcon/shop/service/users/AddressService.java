package com.falcon.shop.service.users;

import java.util.List;

import com.falcon.shop.domain.users.Address;
import com.falcon.shop.service.BaseService;

public interface AddressService extends BaseService<Address> {

    List<Address> listByUser(Long userNo);

    boolean insert(Address address);
    boolean update(Address address);
    boolean updateById(Address address);
    boolean delete(Long no);                          
    boolean deleteById(String id);                          

    
  
}
