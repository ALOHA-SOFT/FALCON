package com.falcon.shop.controller.admin;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.Pagination;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.shop.Cancellations;
import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.service.shop.CancellationService;
import com.falcon.shop.service.shop.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/cancel")
public class CancellationController {

    @Autowired
    private CancellationService cancellationService;
    
    @Autowired
    private OrderService orderService;

    /**
     * 취소/반품 목록 조회
     * @param request
     * @param model
     * @param pagination
     * @param queryParams
     * @return
     */
    @GetMapping("")
    public String list(
        HttpServletRequest request,
        Model model, 
        Pagination pagination,
        QueryParams queryParams,
        Cancellations cancellation // Optional: 특정 취소 정보로 필터링할 경우 사용
    ) {
        PageInfo<Cancellations> pageInfo = cancellationService.page(queryParams, cancellation);
        log.info("pageInfo : {}", pageInfo);
        
        model.addAttribute("pageInfo", pageInfo);
        
        Long total = pageInfo.getTotal();
        pagination.setPage(queryParams.getPage());
        pagination.setSize(queryParams.getSize());
        pagination.setTotal(total);
        model.addAttribute("pagination", pagination);
        
        String path = request.getServletPath();
        String pageUri = UriComponentsBuilder.fromPath(path)
                                             .queryParam("size", pagination.getSize())
                                             .build()
                                             .toUriString();
        model.addAttribute("pageUri", pageUri);
        
        // 취소 유형 옵션
        List<String> typeOptions = Arrays.asList("주문취소", "반품", "교환", "환불");
        model.addAttribute("typeOptions", typeOptions);
        
        return "page/admin/cancel/list";
    }

    /**
     * 취소/반품 생성 페이지
     * @param model
     * @return
     */
    @GetMapping("/create")
    public String create(Model model) {
        // 주문 목록 (셀렉트박스용)
        List<Orders> orders = orderService.list();
        model.addAttribute("orders", orders);
        return "page/admin/cancel/create";
    }

    /**
     * 취소/반품 수정 페이지
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{id}")
    public String update(@PathVariable("id") String id, Model model) {
        Cancellations cancellation = cancellationService.selectById(id);
        if (cancellation == null) {
            log.error("Cancellation with id {} not found", id);
            return "redirect:/admin/cancel";
        }
        
        // 주문 목록 (셀렉트박스용)
        List<Orders> orders = orderService.list();
        model.addAttribute("orders", orders);
        model.addAttribute("cancellation", cancellation);
        
        return "page/admin/cancel/update";
    }

 
}
