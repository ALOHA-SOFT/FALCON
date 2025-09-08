package com.falcon.shop.service.shop;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.Products;
import com.falcon.shop.domain.shop.OrderItem;
import com.falcon.shop.domain.shop.OrderItemOption;
import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.domain.system.Seq;
import com.falcon.shop.domain.system.SeqGroups;
import com.falcon.shop.mapper.products.ProductMapper;
import com.falcon.shop.mapper.shop.OrderItemMapper;
import com.falcon.shop.mapper.shop.OrderItemOptionMapper;
import com.falcon.shop.mapper.shop.OrderMapper;
import com.falcon.shop.mapper.system.SeqGroupsMapper;
import com.falcon.shop.mapper.system.SeqMapper;
import com.falcon.shop.mapper.users.AddressMapper;
import com.falcon.shop.service.BaseServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderServiceImpl extends BaseServiceImpl<Orders, OrderMapper> implements OrderService {

    @Autowired private OrderMapper orderMapper;
    @Autowired private OrderItemMapper orderItemMapper;
    @Autowired private OrderItemOptionMapper orderItemOptionMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private SeqGroupsMapper seqGroupsMapper;
    @Autowired private SeqMapper seqMapper;
    @Autowired private AddressMapper addressMapper;


    @Override
    public PageInfo<Orders> page(QueryParams queryParams) {
        int page = (int) queryParams.getPage();
        int size = (int) queryParams.getSize();
        log.info("queryParams : {}", queryParams);
        PageHelper.startPage(page, size);
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        List<Orders> list = orderMapper.listWithParams(params);
        return new PageInfo<>(list);
    }

    @Transactional
    @Override
    public Orders createOrder(Orders order) {
        // 주문 등록
        // 주문 번호 생성
        if (order == null) {
            log.error("주문 정보가 제공되지 않았습니다.");
            throw new IllegalArgumentException("주문 정보가 필요합니다.");
        }
        String code = createOrderCode(order);
        order.setCode(code);

        // 주문 총가격 - totalPrice
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems == null || orderItems.isEmpty()) {
            log.error("주문 항목이 제공되지 않았습니다.");
            throw new IllegalArgumentException("주문 항목이 필요합니다.");
        }
        Double totalPrice = 0D;
        Long totalQuantity = 0L;
        for (OrderItem item : orderItems) {
            if (item.getPrice() == null || item.getQuantity() == null) {
                log.error("주문 항목의 가격 또는 수량이 없습니다: {}", item);
                throw new RuntimeException("주문 항목의 가격 또는 수량이 없습니다.");
            }
            totalPrice += item.getPrice() * item.getQuantity();
            totalQuantity += item.getQuantity();
        }
        order.setTotalPrice(totalPrice);
        order.setTotalQuantity(totalQuantity);
        order.setTotalItemCount( orderItems.size() + 0L );

        // 주문 등록
        orderMapper.insert(order);

        // 배송비 설정
        Double totalShipPrice = 0D;
        for (OrderItem item : orderItems) {
            // - 배송비 추가: item 의 productNo 의 Product의 배송비 최댓값을 order 의 배송비로 설정
            if (item.getProductNo() == null) {
                log.error("주문 항목에 제품 번호가 없습니다: {}", item);
                throw new RuntimeException("주문 항목에 제품 번호가 없습니다.");
            }
                
            // 배송비는 주문 항목의 제품 번호로부터 가져옴
            QueryWrapper<Products> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("no", item.getProductNo());
            Products product = productMapper.selectOne(queryWrapper);
            log.info("제품 정보: {}", product);
            Double shipPrice = product != null ? product.getShipPrice() : null;
            if (shipPrice == null) {
                log.error("제품의 배송비를 찾을 수 없습니다: 제품 번호 {}", item.getProductNo());
                // 배송비 0 으로 설정
                shipPrice = 0D;
                // throw new RuntimeException("제품의 배송비를 찾을 수 없습니다.");
            }
            totalShipPrice = Math.max(totalShipPrice, shipPrice);
        }
        order.setShipPrice(totalShipPrice);
        orderMapper.updateById(order);

        for (OrderItem item : orderItems) {
            // 주문 항목 등록
            item.setOrderNo(order.getNo());
            orderItemMapper.insert(item);
            log.info("주문 항목 등록: {}", item);
            // 주문 항목 옵션 등록
            List<OrderItemOption> options = item.getOrderItemOptions();
            if (options == null || options.isEmpty()) {
                continue; // 옵션이 없는 경우 건너뜀
            }
            for (OrderItemOption option : options) {
                option.setOrderItemNo(item.getNo());
                orderItemOptionMapper.insert(option);
            }
        }

        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("no", order.getNo());
        Orders newOrder = orderMapper.selectOne(queryWrapper);
        if (newOrder == null) {
            log.error("주문 생성 실패: 주문을 찾을 수 없습니다. 주문 번호: {}", order.getNo());
            throw new RuntimeException("주문 생성 실패");
        }
        return newOrder;
    }

    @Override
    public Orders selectById(String id) {
        Orders order = orderMapper.selectById(id);
        return order;
    }

    @Override
    public PageInfo<Orders> pageByUserNo(QueryParams queryParams, Long userNo) {
        if (userNo == null || userNo <= 0) {
            log.error("사용자 번호가 제공되지 않았습니다.");
            throw new IllegalArgumentException("사용자 번호가 필요합니다.");
        }   
        int page = (int) queryParams.getPage();
        int size = (int) queryParams.getSize();
        log.info("queryParams : {}", queryParams);
        PageHelper.startPage(page, size);
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        params.put("userNo", userNo);
        List<Orders> list = orderMapper.listWithParams(params);
        return new PageInfo<>(list);
    }


    /**
     * 주문번호 생성
     * 주문코드 (20250101_상품번호_유저번호_당일시퀀스)
     * @return
     */
    public String createOrderCode(Orders order) {


        // 20250101
        String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
        // 상품번호 - 첫번재 상품의 상품번호
        Long productNo = null;
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            productNo = order.getOrderItems().get(0).getProductNo();
        }
        // 유저번호
        Long userNo = order.getUserNo();

        // 당일 시퀀스
        
        QueryWrapper<SeqGroups> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", "SEQ_ORDER_CODE");     // 주문번호 시퀀스 그룹코드
        SeqGroups seqGroups = seqGroupsMapper.selectOne(queryWrapper);
        Long seqGroupNo = seqGroups != null ? seqGroups.getNo() : null;
        QueryWrapper<Seq> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("seq_group_no", seqGroupNo);  // 시퀀스
        queryWrapper2.apply("DATE(date) = DATE(NOW())"); // MySQL
        Seq seq = seqMapper.selectOne(queryWrapper2);
        // 없으면 insert
        if (seq == null) {
            seq = new Seq();
            seq.setSeqGroupNo(seqGroupNo);
            seq.setDate(new Date());
            seq.setCode("SEQ_ORDER_CODE");
            seq.setValue(1L);
            seqMapper.insert(seq);
        } else {
            seq.setValue(seq.getValue() + 1);
            seqMapper.updateById(seq);
        }

        Long seqValue = seq.getValue();
        return String.format("%s_%d_%d_%d", datePart, productNo, userNo, seqValue);
    }

    @Override
    public PageInfo<Orders> page(QueryParams queryParams, Orders order) {
        int page = (int) queryParams.getPage();
        int size = (int) queryParams.getSize();
        log.info("queryParams : {}", queryParams);
        PageHelper.startPage(page, size);
        Map<String, Object> params = new HashMap<>();
        params.put("queryParams", queryParams);
        params.put("status", order.getStatus());
        List<Orders> list = orderMapper.listWithParams(params);
        return new PageInfo<>(list);        
    }
    
    
}
