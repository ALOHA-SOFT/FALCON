package com.falcon.shop.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.Pagination;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.Category;
import com.falcon.shop.domain.products.CategoryLarge;
import com.falcon.shop.service.products.CategoryLargeService;
import com.falcon.shop.service.products.CategoryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/category-large")
public class CategoryLargeController {

    @Autowired 
    private CategoryLargeService categoryLargeService;
    
    @Autowired 
    private CategoryService categoryService;

    @GetMapping("")
    public String list(Model model, Pagination pagination,
                      @RequestParam(value = "categoryNo", required = false) Long categoryNo,
                      QueryParams queryParams
    ) {
        PageInfo<CategoryLarge> pageInfo;
        
        if (categoryNo != null) {
            // 특정 카테고리의 대분류만 조회
            PageInfo<CategoryLarge> categoryLarges = categoryLargeService.listByCategory(categoryNo, queryParams);
            pageInfo = categoryLarges;
        } else {
            // 전체 대분류 조회
            pageInfo = categoryLargeService.page(queryParams);
        }
        
        log.info("pageInfo : {}", pageInfo);
        model.addAttribute("pageInfo", pageInfo);
        
        Long total = pageInfo.getTotal();
        pagination.setPage(queryParams.getPage());
        pagination.setSize(queryParams.getSize());
        pagination.setTotal(total);
        model.addAttribute("pagination", pagination);
        
        // 카테고리 목록 (셀렉트박스용)
        List<Category> categories = categoryService.list();
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryNo", categoryNo);
        
        String pageUri = UriComponentsBuilder.fromPath("/admin/category-large")
                                             .queryParam("size", pagination.getSize())
                                             .build()
                                             .toUriString();
        model.addAttribute("pageUri", pageUri);
        
        return "page/admin/category-large/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        // 카테고리 목록 (셀렉트박스용)
        List<Category> categories = categoryService.list();
        model.addAttribute("categories", categories);
        return "page/admin/category-large/create";
    }

    @GetMapping("/{id}")
    public String update(@PathVariable("id") String id, Model model) {
        CategoryLarge categoryLarge = categoryLargeService.selectById(id);
        if (categoryLarge == null) {
            log.error("CategoryLarge with id {} not found", id);
            return "redirect:/admin/category-large"; // Redirect to list if not found
        }
        
        // 카테고리 목록 (셀렉트박스용)
        List<Category> categories = categoryService.list();
        model.addAttribute("categories", categories);
        model.addAttribute("categoryLarge", categoryLarge);
        
        return "page/admin/category-large/update";
    }

}
