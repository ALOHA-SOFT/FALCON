package com.falcon.shop.controller.admin;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.Pagination;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.domain.users.Users;
import com.falcon.shop.service.shop.OrderService;
import com.falcon.shop.service.users.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller("AdminOrderController")
@RequestMapping("/admin/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;

    @GetMapping("")
    public String list(
        HttpServletRequest request,
        Model model, 
        Pagination pagination,
        QueryParams queryParams,
        Orders order
    ) {
        PageInfo<Orders> pageInfo = orderService.page(queryParams, order);
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
        
        // 상태 필터 옵션
        List<String> statusOptions = Arrays.asList("주문접수", "결제완료", "배송준비중", "배송중", "배송완료", "주문취소", "환불완료");
        model.addAttribute("statusOptions", statusOptions);
        
        return "page/admin/orders/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        // 회원 목록 (셀렉트박스용)
        List<Users> users = userService.list();
        model.addAttribute("users", users);
        return "page/admin/orders/create";
    }

    @GetMapping("/{id}")
    public String update(@PathVariable("id") String id, Model model) {
        Orders order = orderService.selectById(id);
        if (order == null) {
            log.error("Order with id {} not found", id);
            return "redirect:/admin/orders";
        }
        
        // 회원 목록 (셀렉트박스용)
        List<Users> users = userService.list();
        model.addAttribute("users", users);
        model.addAttribute("order", order);
        
        return "page/admin/orders/update";
    }

    @PostMapping("")
    public String store(Orders order, RedirectAttributes redirectAttributes) {
        try {
            boolean result = orderService.insert(order);
            if (result) {
                redirectAttributes.addFlashAttribute("successMessage", "주문이 성공적으로 등록되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "주문 등록에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("Error creating order", e);
            redirectAttributes.addFlashAttribute("errorMessage", "주문 등록 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/orders";
    }

    @PutMapping("/{id}")
    public String updateOrder(@PathVariable("id") String id, Orders order, RedirectAttributes redirectAttributes) {
        try {
            order.setId(id);
            boolean result = orderService.updateById(order);
            if (result) {
                redirectAttributes.addFlashAttribute("successMessage", "주문이 성공적으로 수정되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "주문 수정에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("Error updating order", e);
            redirectAttributes.addFlashAttribute("errorMessage", "주문 수정 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/orders";
    }

    @DeleteMapping("/{id}")
    public String destroy(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            boolean result = orderService.deleteById(id);
            if (result) {
                redirectAttributes.addFlashAttribute("successMessage", "주문이 성공적으로 삭제되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "주문 삭제에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("Error deleting order", e);
            redirectAttributes.addFlashAttribute("errorMessage", "주문 삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/delete-selected")
    public String deleteSelected(@RequestParam("selectedIds") String selectedIds, RedirectAttributes redirectAttributes) {
        try {
            String[] ids = selectedIds.split(",");
            int deletedCount = 0;
            
            for (String id : ids) {
                if (orderService.deleteById(id.trim())) {
                    deletedCount++;
                }
            }
            
            if (deletedCount > 0) {
                redirectAttributes.addFlashAttribute("successMessage", deletedCount + "개의 주문이 성공적으로 삭제되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "선택된 주문 삭제에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("Error deleting selected orders", e);
            redirectAttributes.addFlashAttribute("errorMessage", "선택된 주문 삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/update-status")
    public String updateStatus(
        @RequestParam("selectedIds") String selectedIds,
        String status,
        RedirectAttributes redirectAttributes
    ) {
        try {
            String[] ids = selectedIds.split(",");
            int updatedCount = 0;
            
            for (String id : ids) {
                Orders order = orderService.selectById(id.trim());
                if (order != null) {
                    order.setStatus(status);
                    if (orderService.updateById(order)) {
                        updatedCount++;
                    }
                }
            }
            
            if (updatedCount > 0) {
                redirectAttributes.addFlashAttribute("successMessage", updatedCount + "개의 주문 상태가 성공적으로 변경되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "선택된 주문 상태 변경에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("Error updating order status", e);
            redirectAttributes.addFlashAttribute("errorMessage", "주문 상태 변경 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/orders";
    }
}
