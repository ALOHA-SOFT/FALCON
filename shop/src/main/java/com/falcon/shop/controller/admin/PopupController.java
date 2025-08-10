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
import com.falcon.shop.domain.admin.Popups;
import com.falcon.shop.domain.common.Pagination;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.service.admin.PopupService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/popups")
public class PopupController {

    @Autowired
    private PopupService popupService;

    @GetMapping("")
    public String list(
        HttpServletRequest request,
        Model model, 
        Pagination pagination,
        QueryParams queryParams
    ) {
        PageInfo<Popups> pageInfo = popupService.page(queryParams);
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
        
        // 팝업 유형 옵션
        List<String> typeOptions = Arrays.asList("메인팝업", "이벤트팝업", "공지팝업", "광고팝업");
        model.addAttribute("typeOptions", typeOptions);
        
        return "page/admin/popups/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        return "page/admin/popups/create";
    }

    @GetMapping("/{id}")
    public String update(@PathVariable("id") String id, Model model) {
        Popups popup = popupService.selectById(id);
        if (popup == null) {
            log.error("Popup with id {} not found", id);
            return "redirect:/admin/popups";
        }
        
        model.addAttribute("popup", popup);
        return "page/admin/popups/update";
    }

   
}
