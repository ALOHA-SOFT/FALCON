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
import com.falcon.shop.domain.system.Seq;
import com.falcon.shop.service.system.SeqGroupsService;
import com.falcon.shop.service.system.SeqService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/seq")
public class SeqController {

    @Autowired private SeqService seqService;
    @Autowired private SeqGroupsService seqGroupsService;


    /**
     * 시퀀스 목록
     */
    @GetMapping("")
    public String list(
        HttpServletRequest request,
        Model model, Pagination pagination, 
        QueryParams queryParams
    ) {
        PageInfo<Seq> pageInfo = seqService.page(queryParams);
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
        return "page/admin/seq/list";
    }

    /**
     * 시퀀스 등록 폼
     */
    @GetMapping("/create")
    public String create() {
        return "page/admin/seq/create";
    }

    /**
     * 시퀀스 수정 폼
     */
    @GetMapping("/{id}")
    public String update(@PathVariable("id") String id, Model model) {
        // 시퀀스 그룹 목록을 조회하여 모델에 추가
        model.addAttribute("seqGroups", seqGroupsService.list());

        model.addAttribute("seq", seqService.selectById(id));
        return "page/admin/seq/update";
    }
}
