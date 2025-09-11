package com.falcon.shop.service.shop;

import java.math.BigDecimal;
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
import com.falcon.shop.domain.shop.Shipments;
import com.falcon.shop.domain.system.Seq;
import com.falcon.shop.domain.system.SeqGroups;
import com.falcon.shop.mapper.products.ProductMapper;
import com.falcon.shop.mapper.shop.OrderItemMapper;
import com.falcon.shop.mapper.shop.OrderItemOptionMapper;
import com.falcon.shop.mapper.shop.OrderMapper;
import com.falcon.shop.mapper.shop.ShipmentMapper;
import com.falcon.shop.mapper.system.SeqGroupsMapper;
import com.falcon.shop.mapper.system.SeqMapper;
import com.falcon.shop.mapper.users.AddressMapper;
import com.falcon.shop.service.BaseServiceImpl;
import com.falcon.shop.service.email.EmailService;
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
    @Autowired private ShipmentMapper shipmentMapper;
    @Autowired private EmailService emailService;


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
        
        log.info("🔍 주문 생성 시작 - 초기 totalPrice: {}", order.getTotalPrice());
        
        String code = createOrderCode(order);
        order.setCode(code);

        // 주문 총가격 - totalPrice
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems == null || orderItems.isEmpty()) {
            log.error("주문 항목이 제공되지 않았습니다.");
            throw new IllegalArgumentException("주문 항목이 필요합니다.");
        }
        BigDecimal totalPrice = BigDecimal.ZERO;
        Long totalQuantity = 0L;
        for (OrderItem item : orderItems) {

            if (item.getPrice() == null || item.getQuantity() == null) {
                log.error("주문 항목의 가격 또는 수량이 없습니다: {}", item);
                throw new RuntimeException("주문 항목의 가격 또는 수량이 없습니다.");
            }
            
            log.info("🔍 아이템 가격: {}, 수량: {}", item.getPrice(), item.getQuantity());
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            log.info("🔍 아이템 총액: {}", itemTotal);

            totalPrice = totalPrice.add(itemTotal);
            totalQuantity += item.getQuantity();
            log.info("🔍 누적 totalPrice: {}", totalPrice);
        }
        
        log.info("🔍 상품 총액 (배송비 제외): {}", totalPrice);
        
        order.setTotalPrice(totalPrice);
        order.setTotalQuantity(totalQuantity);
        order.setTotalItemCount( orderItems.size() + 0L );

        // 주문 등록
        orderMapper.insert(order);

        // 배송비 설정
        BigDecimal totalShipPrice = BigDecimal.ZERO;
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
            BigDecimal shipPrice = product != null ? product.getShipPrice() : null;
            if (shipPrice == null) {
                log.error("제품의 배송비를 찾을 수 없습니다: 제품 번호 {}", item.getProductNo());
                // 배송비 0 으로 설정
                shipPrice = BigDecimal.ZERO;
                // throw new RuntimeException("제품의 배송비를 찾을 수 없습니다.");
            }
            totalShipPrice = totalShipPrice.max(shipPrice);
        }
        log.info("🔍 배송비: {}", totalShipPrice);
        order.setShipPrice(totalShipPrice);
        
        // totalPrice에 배송비 포함
        BigDecimal finalTotalPrice = totalPrice.add(totalShipPrice);
        log.info("🔍 최종 totalPrice (상품가격 + 배송비): {}", finalTotalPrice);
        order.setTotalPrice(finalTotalPrice);
        
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

    @Transactional
    @Override
    public boolean processOrder(Orders order) {
        // 주문 번호 확인
        String orderId = order.getId();
        if (order == null || order.getId() == null) {
            log.error("주문 정보가 제공되지 않았습니다.");
            throw new IllegalArgumentException("주문 정보가 필요합니다.");
        }
        // 주문 상태 확인
        String orderStatus = order.getStatus();
        String orderStatusForMail = "";                 // 영문 상태
        switch (orderStatus) {
            case "결제대기":            orderStatusForMail = "Payment Pending"; break;
            case "결제완료":            orderStatusForMail = "Payment Completed"; break;
            case "배송준비중":  orderStatusForMail = "Preparing for Shipment"; break;
            case "배송시작":  orderStatusForMail = "Dispatched"; break;
            case "배송중":  orderStatusForMail = "In Transit"; break;
            case "배송완료":  orderStatusForMail = "Delivered"; break;
            case "주문취소":  orderStatusForMail = "Order Cancelled"; break;
            case "환불완료":  orderStatusForMail = "Refund Completed"; break;
        }

        
        if (orderStatus == null || orderStatus.isEmpty()) {
            log.error("주문 상태가 제공되지 않았습니다.");
            throw new IllegalArgumentException("주문 상태가 필요합니다.");
        }
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", orderId);
        
        // 상태 업데이트
        log.info("주문 상태 업데이트: 주문번호 {}, 상태 {}", orderId, orderStatus);
        orderMapper.update(order, queryWrapper);

        // 상태별 처리
        switch (orderStatus) {
            case "결제대기": break;
            case "결제완료": 
                log.info("결제완료 처리");

                // 결제완료 메일 발송
                Orders orderforMail = orderMapper.selectById(orderId);
                if (orderforMail == null) {
                    log.error("주문 정보를 찾을 수 없습니다: 주문 ID {}", orderId);
                    throw new RuntimeException("주문 정보를 찾을 수 없습니다.");
                }

                String paymentMethod = orderforMail.getPaymentMethod();
                String recipientEmail = orderforMail.getGuestEmail();
                String recipientName = orderforMail.getGuestFirstName() + " " + orderforMail.getGuestLastName();
                emailService.sendPaymentCompleteEmail(orderforMail, paymentMethod, recipientEmail, recipientName);
                break;
            case "배송준비중":  
            case "배송시작":  
            case "배송중":  
            case "배송완료": 
            case "주문취소": 
                // 배송상태 업데이트
                QueryWrapper<Shipments> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("id", order.getShipment().getId());
                Shipments shipment = order.getShipment();
                shipmentMapper.update(shipment, queryWrapper2);
                // 배송상태 변경 이메일 발송
                Orders orderforMail2 = orderMapper.selectById(orderId);
                if (orderforMail2 == null) {
                    log.error("주문 정보를 찾을 수 없습니다: 주문 ID {}", orderId);
                    throw new RuntimeException("주문 정보를 찾을 수 없습니다.");
                }
                String recipientEmail2 = orderforMail2.getGuestEmail();
                String recipientName2 = orderforMail2.getGuestFirstName() + " " + orderforMail2.getGuestLastName();
                String trackingNo = shipment.getTrackingNo();
                String shipCompany = shipment.getShipCompany();
                String deliveryMethod = shipment.getDeliveryMethod();

                emailService.sendUpdateShipmentStatus(orderforMail2, orderStatusForMail, trackingNo, shipCompany, deliveryMethod, recipientEmail2, recipientName2);
                break;
        }

        return true;
    }
    
    
}
