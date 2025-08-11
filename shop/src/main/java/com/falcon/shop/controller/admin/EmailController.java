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
import com.falcon.shop.domain.email.Email;
import com.falcon.shop.service.email.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/email")
public class EmailController {

    @Autowired 
    EmailService emailService;

    @GetMapping("")
    public String list(
        HttpServletRequest request,
        Model model, 
        Pagination pagination, 
        QueryParams queryParams
    ) {
        PageInfo<Email> pageInfo = emailService.page(queryParams.getPage(), queryParams.getSize());
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
        
        return "page/admin/email/list";
    }

    @GetMapping("/create")
    public String create() {
        return "page/admin/email/create";
    }

    @GetMapping("/{id}")
    public String update(@PathVariable("id") String id, Model model) {
        Email email = emailService.selectById(id);
        if (email == null) {
            log.error("Email with id {} not found", id);
            return "redirect:/admin/email";
        }
        model.addAttribute("email", email);
        return "page/admin/email/update";
    }

    @GetMapping("/{id}/view")
    public String view(@PathVariable("id") String id, Model model) {
        Email email = emailService.selectById(id);
        if (email == null) {
            log.error("Email with id {} not found", id);
            return "redirect:/admin/email";
        }
        model.addAttribute("email", email);
        return "page/admin/email/view";
    }
}
