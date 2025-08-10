package com.falcon.shop.service.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.falcon.shop.domain.users.Address;
import com.falcon.shop.mapper.users.AddressMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AddressServiceImpl extends BaseServiceImpl<Address, AddressMapper> implements AddressService {

  @Autowired private AddressMapper addressMapper;

  @Override
  public List<Address> listByUser(Long userNo) {
    QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_no", userNo);
    queryWrapper.orderByDesc("is_main");
    List<Address> addressList = addressMapper.selectList(queryWrapper);
    log.info("Retrieved {} shipments for user {}", addressList.size(), userNo);
    return addressList;
  }

  @Override
  public boolean insert(Address entity) {
    changeMain(entity);
    return super.insert(entity);
  }

  @Override
  public boolean update(Address entity) {
    changeMain(entity);
    return super.update(entity);
  }

  @Override
  public boolean updateById(Address entity) {
    changeMain(entity);
    return super.updateById(entity);
  }

  @Override
  public boolean delete(Long no) {
    Address address = super.select(no);
    if (address == null) {
      log.warn("Address with no {} not found for deletion", no);
      return false;
    }
    // 기본 주소지 삭제 방지
    if (address.getIsMain() != null && address.getIsMain()) {
      log.warn("Cannot delete main address with no {}", no);
      return false; // 
    }
    return super.delete(no);
  }

  @Override
  public boolean deleteById(String id) {
    Address address = super.selectById(id);
    if (address == null) {
      log.warn("Address with id {} not found for deletion", id);
      return false;
    }
    // 기본 주소지 삭제 방지
    if (address.getIsMain() != null && address.getIsMain()) {
      log.warn("Cannot delete main address with id {}", id);
      return false;
    }
    return super.deleteById(id);
  }


  /**
   * 기본 주소지 변경
   * @param entity
   */
  public void changeMain(Address entity) {
    boolean isMain = entity.getIsMain() != null && entity.getIsMain();
    if (isMain) {
      // If this address is marked as main, set all other addresses to not main
      QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("user_no", entity.getUserNo());
      queryWrapper.eq("is_main", true);
      List<Address> mainAddresses = addressMapper.selectList(queryWrapper);
      for (Address mainAddress : mainAddresses) {
        mainAddress.setIsMain(false);
        addressMapper.updateById(mainAddress);
      }
    }
  }
}
