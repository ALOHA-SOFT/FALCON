package com.falcon.shop.service.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.Options;
import com.falcon.shop.domain.shop.CartItemOption;
import com.falcon.shop.domain.shop.OrderItem;
import com.falcon.shop.domain.shop.OrderItemOption;
import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.domain.users.Carts;
import com.falcon.shop.mapper.products.OptionMapper;
import com.falcon.shop.mapper.users.CartMapper;
import com.falcon.shop.service.BaseServiceImpl;
import com.falcon.shop.service.shop.OrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CartServiceImpl extends BaseServiceImpl<Carts, CartMapper> implements CartService {

    @Autowired private CartMapper cartMapper;
    @Autowired private OptionMapper optionMapper;
    @Autowired private OrderService orderService;
    
    @Override
    public PageInfo<Carts> pageByUserNo(QueryParams queryParams, Long userNo) {
        log.info("queryParams : {}", queryParams);
        PageHelper.startPage(queryParams.getPage(), queryParams.getSize());
        Map<String, Object> params = new HashMap<>();
        params.put("userNo", userNo);
        params.put("queryParams", queryParams);
        PageInfo<Carts> pageInfo = new PageInfo<>(cartMapper.listByUser(params));
        List<Carts> carts = pageInfo.getList();
        for (Carts cart : carts) {
            cart.toItemOption();
            List<CartItemOption> options = cart.getCartItemOptions();
            if (options != null && !options.isEmpty()) {
                for (CartItemOption option : options) {
                    Long optionNo = option.getOptionNo();
                    if (optionNo != null) {
                        Options optionDetails = optionMapper.selectById(optionNo);
                        option.setOption(optionDetails);
                    }
                }
            }
        }
        log.info("pageInfo : {}", pageInfo);
        return pageInfo;
    }

    @Override
    public Orders createOrder(Carts cart) {
        log.info("장바구니로 주문 생성 요청: {}", cart);
        try {
            Carts realCart = selectById(cart.getId());
            // Cart 데이터를 Orders 객체로 변환
            Orders order = Orders.builder()
                .title(realCart.getProduct().getName())
                .userNo(realCart.getUserNo())
                .totalPrice(realCart.getTotalPrice())
                .totalQuantity(realCart.getQuantity())
                .totalItemCount(1L) // 단일 상품이므로 1
                .status("결제대기")
                .shipPrice(realCart.getProduct().getShipPrice() != null ? realCart.getProduct().getShipPrice() : 0L)
                .build();
            List<CartItemOption> cartItemOptions = realCart.getCartItemOptions();

            // OrderItem을 위한 매핑
            if (cartItemOptions != null && !cartItemOptions.isEmpty()) {
                List<OrderItem> orderItems = new ArrayList<>();
                
                OrderItem orderItem = OrderItem.builder()
                                                .productNo(realCart.getProductNo())
                                                .quantity(realCart.getQuantity())
                                                .price(realCart.getTotalPrice()== null ? 0L : realCart.getTotalPrice())
                                                .build();
                
                // CartItemOption을 OrderItemOption으로 변환
                List<OrderItemOption> orderItemOptions = new ArrayList<>();
                for (CartItemOption cartOption : cartItemOptions) {
                    OrderItemOption orderItemOption = OrderItemOption.builder()
                                                                    .optionNo(cartOption.getOptionNo())
                                                                    .quantity(cartOption.getQuantity())
                                                                    .price(cartOption.getPrice())
                                                                    .build();
                    orderItemOptions.add(orderItemOption);
                }
                orderItem.setOrderItemOptions(orderItemOptions);
                orderItems.add(orderItem);
                order.setOrderItems(orderItems);
            }
            // 주문 생성
            Orders createdOrder = orderService.createOrder(order);
            if( createdOrder == null) {
                log.error("장바구니로 주문 생성 실패: 주문 정보가 null입니다.");
                throw new RuntimeException("장바구니로 주문 생성에 실패했습니다.");
            }
            // 장바구니 삭제
            boolean isDeleted = this.deleteById(cart.getId());
            if( isDeleted) {
                log.info("장바구니 삭제 성공: {}", cart.getId());
            } else {
                log.warn("장바구니 삭제 실패: {}", cart.getId());
            }
            return createdOrder;
        } catch (Exception e) {
            log.error("장바구니로 주문 생성 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("장바구니로 주문 생성 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public Carts selectById(String id) {
        Carts cart = cartMapper.selectById(id);
        if (cart == null) {
            log.warn("장바구니를 찾을 수 없습니다: {}", id);
            return null;
        }
        cart.toItemOption();
        List<CartItemOption> options = cart.getCartItemOptions();
        if (options != null && !options.isEmpty()) {
            for (CartItemOption option : options) {
                Long optionNo = option.getOptionNo();
                if (optionNo != null) {
                    Options optionDetails = optionMapper.selectById(optionNo);
                    option.setOption(optionDetails);
                }
            }
        }
        log.info("장바구니 조회 성공: {}", cart);
        return cart;
    }

    @Override
    public Orders createOrder(List<Carts> cartList) {
        log.info("장바구니 목록으로 주문 생성 요청: {}", cartList);
        if (cartList == null || cartList.isEmpty()) {
            log.error("장바구니 목록이 비어 있습니다.");
            throw new RuntimeException("장바구니 목록이 비어 있습니다.");
        }
        
        Orders order = Orders.builder()
            .title("장바구니 주문")
            .userNo(cartList.get(0).getUserNo())
            .status("결제대기")
            .build();
        
        List<OrderItem> orderItems = new ArrayList<>();
        for (Carts cart : cartList) {
            OrderItem orderItem = OrderItem.builder()
                .productNo(cart.getProductNo())
                .quantity(cart.getQuantity())
                .price(cart.getTotalPrice() != null ? cart.getTotalPrice() : 0L)
                .build();
            
            // CartItemOption을 OrderItemOption으로 변환
            List<OrderItemOption> orderItemOptions = new ArrayList<>();
            List<CartItemOption> cartItemOptions = cart.getCartItemOptions();
            if (cartItemOptions != null && !cartItemOptions.isEmpty()) {
                for (CartItemOption cartOption : cartItemOptions) {
                    OrderItemOption orderItemOption = OrderItemOption.builder()
                        .optionNo(cartOption.getOptionNo())
                        .quantity(cartOption.getQuantity())
                        .price(cartOption.getPrice())
                        .build();
                    orderItemOptions.add(orderItemOption);
                }
                orderItem.setOrderItemOptions(orderItemOptions);
            }
            orderItems.add(orderItem);
        }
        
        order.setOrderItems(orderItems);
        
        // 주문 생성
        Orders createdOrder = orderService.createOrder(order);
        if (createdOrder == null) {
            log.error("장바구니 목록으로 주문 생성 실패: 주문 정보가 null입니다.");
            throw new RuntimeException("장바구니 목록으로 주문 생성에 실패했습니다.");
        }
        
        // 장바구니 삭제
        for (Carts cart : cartList) {
            boolean isDeleted = this.deleteById(cart.getId());
            if (isDeleted) {
                log.info("장바구니 삭제 성공: {}", cart.getId());
            } else {
                log.warn("장바구니 삭제 실패: {}", cart.getId());
            }
        }
        
        return createdOrder;
    }

    @Override
    public List<Carts> selectByIds(String[] split) {
        if (split == null || split.length == 0) {
            log.warn("장바구니 ID 목록이 비어 있습니다.");
            return new ArrayList<>();
        }
        List<Carts> carts = new ArrayList<>();
        for (String id : split) {
            Carts cart = selectById(id);
            if (cart != null) {
                carts.add(cart);
            } else {
                log.warn("장바구니를 찾을 수 없습니다: {}", id);
            }
        }
        return carts;
    }

    @Override
    public Orders createOrder(Long userNo) {
        List<Carts> cartList = listByUserNo(userNo);
        if (cartList == null || cartList.isEmpty()) {
            log.warn("회원별 장바구니 목록이 비어 있습니다: userNo={}", userNo);
            throw new RuntimeException("회원별 장바구니 목록이 비어 있습니다.");
        }
        return createOrder(cartList);
    }

    @Override
    public List<Carts> listByUserNo(Long userNo) {
        log.info("회원별 장바구니 목록 조회: userNo={}", userNo);
        QueryWrapper<Carts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_no", userNo);
        List<Carts> carts = cartMapper.selectList(queryWrapper);
        if (carts == null || carts.isEmpty()) {
            log.warn("회원별 장바구니 목록이 비어 있습니다: userNo={}", userNo);
            return new ArrayList<>();
        }
        for (Carts cart : carts) {
            cart.toItemOption();
            List<CartItemOption> options = cart.getCartItemOptions();
            if (options != null && !options.isEmpty()) {
                for (CartItemOption option : options) {
                    Long optionNo = option.getOptionNo();
                    if (optionNo != null) {
                        Options optionDetails = optionMapper.selectById(optionNo);
                        option.setOption(optionDetails);
                    }
                }
            }
        }
        return carts;
    }

        

    
    

}
