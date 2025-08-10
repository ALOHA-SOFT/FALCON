package com.falcon.shop.controller.admin;

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
import com.falcon.shop.domain.system.CodeGroups;
import com.falcon.shop.domain.system.Codes;
import com.falcon.shop.service.system.CodeGroupsService;
import com.falcon.shop.service.system.CodesService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/codes")
public class CodesController {

    @Autowired private CodesService codesService;
    @Autowired private CodeGroupsService codeGroupsService;

    /**
     * 코드 목록
     */
    @GetMapping("")
    public String list(
        HttpServletRequest request,
        Model model, Pagination pagination, 
        QueryParams queryParams
    ) {
        PageInfo<Codes> pageInfo = codesService.page(queryParams);
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
        return "page/admin/codes/list";
    }

    /**
     * 코드 등록 폼
     */
    @GetMapping("/create")
    public String create() {
        
        return "page/admin/codes/create";
    }
    
    /**
     * 코드 수정 폼
     */
    @GetMapping("/{id}")
    public String update(@PathVariable("id") String id, Model model) {
        List<CodeGroups> codeGroups = codeGroupsService.list();
        log.info("codeGroups : {}", codeGroups);
        // 코드 그룹 목록을 조회하여 모델에 추가
        model.addAttribute("codeGroups", codeGroups);
        model.addAttribute("codes", codesService.selectById(id));
        return "page/admin/codes/update";
    }
}
