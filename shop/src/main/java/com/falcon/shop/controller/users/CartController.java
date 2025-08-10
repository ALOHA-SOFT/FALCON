package com.falcon.shop.controller.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.domain.users.Carts;
import com.falcon.shop.domain.users.CustomUser;
import com.falcon.shop.service.users.CartService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/carts")
public class CartController {

    @Autowired private CartService cartService;

    @GetMapping("")
    public String cartHome() {
        
        return "page/users/cart";
    }

    /**
     * 장바구니 id 로 주문 생성
     * @param id
     * @param customUser
     * @param model
     * @return
     */
    @GetMapping("/create/{id}")
    public String createCart(
        @PathVariable("id") String id, 
        @AuthenticationPrincipal CustomUser customUser, 
        Carts cart,
        Model model
    ) {
        log.info("장바구니 생성 요청: {}", id);
        // 사용자 정보
        if (customUser == null || customUser.getUser() == null) {
            log.warn("사용자 정보가 없습니다.");
            model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
            return "page/users/error";
        }
        cart.setId(id);
        cart.setUserNo(customUser.getUser().getNo());


        Orders createdOrder = cartService.createOrder(cart);
        String orderId = createdOrder.getId();

        return "redirect:/pay/order/" + orderId;
    }

    /**
     * 장바구니 선택 주문 생성
     * - 여러 개의 Carts id 를 받아 List<Carts> 를 만들고 이거로 주문 생성
     * /create/list/id1,id2,id3 형태로 장바구니 목록 생성
     * @param customUser
     * @param model
     * @param ids
     * @return
     */
    @GetMapping("/create/list/{ids}")
    public String createCartList(
        @AuthenticationPrincipal CustomUser customUser, 
        Model model,
        @PathVariable("ids") String ids
    ) {
        List<Carts> cartList = cartService.selectByIds(ids.split(","));
        log.info("장바구니 목록 생성 요청: {}", ids);
        // 사용자 정보
        if (customUser == null || customUser.getUser() == null) {
            log.warn("사용자 정보가 없습니다.");
            model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
            return "page/users/error";
        }
        if (cartList == null || cartList.isEmpty()) {
            log.warn("장바구니 목록이 비어 있습니다: {}", ids);
            model.addAttribute("error", "장바구니 목록이 비어 있습니다.");
            return "page/users/error";
        }
        for (Carts cart : cartList) {
            cart.setUserNo(customUser.getUser().getNo());
        }
        Orders createdOrder = cartService.createOrder(cartList);
        String orderId = createdOrder.getId();
        log.info("장바구니 목록으로 주문 생성 완료: {}", orderId);
        if (orderId == null || orderId.isEmpty()) {
            log.error("장바구니 목록으로 주문 생성 실패: 주문 ID가 null 또는 비어 있습니다.");
            model.addAttribute("error", "장바구니 목록으로 주문 생성에 실패했습니다.");
            return "page/users/error";
        }
        return "redirect:/pay/order/" + orderId;
    }


    /**
     * 회원별 장바구니 전체 주문 생성
     * @param customUser
     * @param model
     * @return
     */
    @GetMapping("/create/all")
    public String createCartAll(
        @AuthenticationPrincipal CustomUser customUser, 
        Model model
    ) {
        log.info("회원별 장바구니 전체 주문 생성 요청");
        if (customUser == null || customUser.getUser() == null) {
            log.warn("사용자 정보가 없습니다.");
            model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
            return "page/users/error";
        }
        Long userNo = customUser.getUser().getNo();
        Orders createdOrder = cartService.createOrder(userNo);
        String orderId = createdOrder.getId();
        log.info("회원별 장바구니 전체 주문 생성 완료: {}", orderId);
        if (orderId == null || orderId.isEmpty()) {
            log.error("회원별 장바구니 전체 주문 생성 실패: 주문 ID가 null 또는 비어 있습니다.");
            model.addAttribute("error", "회원별 장바구니 전체 주문 생성에 실패했습니다.");
            return "page/users/error";
        }
        return "redirect:/pay/order/" + orderId;
    }
        
}
