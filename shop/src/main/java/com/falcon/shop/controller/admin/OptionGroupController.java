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
import com.falcon.shop.domain.products.OptionGroup;
import com.falcon.shop.service.products.OptionGroupService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/option-groups")
public class OptionGroupController {

    @Autowired
    private OptionGroupService optionGroupService;

    /**
     * 옵션그룹 목록 페이지
     */
    @GetMapping("")
    public String list(
        HttpServletRequest request,
        Model model, Pagination pagination, 
        QueryParams queryParams
    ) {
        PageInfo<OptionGroup> pageInfo = optionGroupService.page(queryParams);
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
        return "page/admin/option-groups/list";
    }

    /**
     * 옵션그룹 등록 페이지
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("optionGroup", new OptionGroup());
        return "page/admin/option-groups/create";
    }


    /**
     * 옵션그룹 수정 페이지
     */
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") String id, Model model) {
        OptionGroup optionGroup = optionGroupService.selectById(id);
        model.addAttribute("optionGroup", optionGroup);
        return "page/admin/option-groups/update";
    }


   


}
