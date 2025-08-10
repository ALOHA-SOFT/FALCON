package com.falcon.shop.service.users;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.domain.users.Carts;
import com.falcon.shop.service.BaseService;

public interface CartService extends BaseService<Carts> {

    // 회원별 장바구니 페이지징 목록
    PageInfo<Carts> pageByUserNo(QueryParams queryParams, Long userNo);
    // 회원별 장바구니 목록
    List<Carts> listByUserNo(Long userNo);

    // 장바구니로 주문 생성
    Orders createOrder(Carts cart);

    // 장바구니로 여러상품 주문 생성
    Orders createOrder(List<Carts> cartList);

    // 장바구니 전체 주문 생성
    Orders createOrder(Long userNo);

    // 장바구니 ID로 장바구니 조회
    Carts selectById(String id);

    List<Carts> selectByIds(String[] split);
    
  
}
