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
import com.falcon.shop.domain.admin.Banners;
import com.falcon.shop.domain.common.Pagination;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.service.admin.BannerService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/banners")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping("")
    public String list(
        HttpServletRequest request,
        Model model, 
        Pagination pagination,
        QueryParams queryParams
    ) {
        PageInfo<Banners> pageInfo = bannerService.page(queryParams);
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
        
        // 배너 유형 옵션
        List<String> typeOptions = Arrays.asList("메인배너", "서브배너", "이벤트배너", "광고배너");
        model.addAttribute("typeOptions", typeOptions);
        
        return "page/admin/banners/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        return "page/admin/banners/create";
    }

    @GetMapping("/{id}")
    public String update(@PathVariable("id") String id, Model model) {
        Banners banner = bannerService.selectById(id);
        if (banner == null) {
            log.error("Banner with id {} not found", id);
            return "redirect:/admin/banners";
        }
        
        model.addAttribute("banner", banner);
        return "page/admin/banners/update";
    }

  
}
