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
import com.falcon.shop.domain.products.Category;
import com.falcon.shop.service.products.CategoryService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired CategoryService categoryService;

    @GetMapping("")
    public String list(
        HttpServletRequest request,
        Model model, Pagination pagination, 
        QueryParams queryParams
    ) {
        PageInfo<Category> pageInfo = categoryService.page(queryParams);
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
        return "page/admin/category/list";
    }

    @GetMapping("/create")
    public String create() {
        return "page/admin/category/create";
    }

    @GetMapping("/{id}")
    public String update(@PathVariable("id") String id, Model model) {
        Category category = categoryService.selectById(id);
        if (category == null) {
            log.error("Category with id {} not found", id);
            return "redirect:/admin/category"; // Redirect to list if category not found
        }
        model.addAttribute("category", category);
        return "page/admin/category/update";
    }

}
