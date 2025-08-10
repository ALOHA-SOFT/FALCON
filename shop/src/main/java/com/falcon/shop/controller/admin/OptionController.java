package com.falcon.shop.controller.admin;

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
import com.falcon.shop.domain.products.Options;
import com.falcon.shop.service.products.OptionGroupService;
import com.falcon.shop.service.products.OptionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/options")
public class OptionController {

    @Autowired
    private OptionService optionService;
    
    @Autowired
    private OptionGroupService optionGroupService;

    /**
     * 옵션 목록 페이지
     */
    @GetMapping("")
    public String list(
        HttpServletRequest request,
        Model model, Pagination pagination, 
        QueryParams queryParams
    ) {
        PageInfo<Options> pageInfo = optionService.page(queryParams);
        log.info("pageInfo : {}",pageInfo);
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
        return "page/admin/options/list";
    }

    /**
     * 옵션 등록 페이지
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("option", new Options());
        
        // 옵션그룹 목록 조회
        model.addAttribute("optionGroups", optionGroupService.list());
        
        return "page/admin/options/create";
    }


    /**
     * 옵션 수정 페이지
     */
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") String id, Model model) {
        Options option = optionService.selectById(id);
        model.addAttribute("option", option);
        
        // 옵션그룹 목록 조회
        model.addAttribute("optionGroups", optionGroupService.list());
        
        return "page/admin/options/update";
    }

  
}
