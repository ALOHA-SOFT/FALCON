package com.falcon.shop.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.Pagination;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.products.Category;
import com.falcon.shop.domain.products.CategoryLarge;
import com.falcon.shop.domain.products.OptionGroup;
import com.falcon.shop.domain.products.Products;
import com.falcon.shop.service.products.CategoryLargeService;
import com.falcon.shop.service.products.CategoryService;
import com.falcon.shop.service.products.OptionGroupService;
import com.falcon.shop.service.products.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller("AdminProductController")
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;
    @Autowired private CategoryLargeService categoryLargeService;
    @Autowired private OptionGroupService optionGroupService;

    /**
     * 상품 목록 페이지
     */
    @GetMapping("")
    public String list(
        HttpServletRequest request,
        Model model, 
        Pagination pagination, 
        QueryParams queryParams
    ) {
        PageInfo<Products> pageInfo = productService.page(queryParams);
        log.info("pageInfo : {}", pageInfo);
        model.addAttribute("pageInfo", pageInfo);
        
        Long total = pageInfo.getTotal();
        pagination.setPage(queryParams.getPage());
        pagination.setSize(queryParams.getSize());
        pagination.setTotal(total);
        model.addAttribute("pagination", pagination);
        
        String path = request.getServletPath();
        String pageUri = UriComponentsBuilder.fromPath(path)
                                             .queryParam("search", queryParams.getSearch())
                                             .queryParam("size", pagination.getSize())
                                             .build()
                                             .toUriString();
        model.addAttribute("pageUri", pageUri);
        
        return "page/admin/products/list";
    }

    /**
     * 상품 등록 페이지
     */
    @GetMapping("/create")
    public String createForm(Model model, QueryParams queryParams) {
        // 카테고리 목록 조회
        List<Category> list = categoryService.list();
        model.addAttribute("categories", list);
        model.addAttribute("product", new Products());
        // 옵션그룹 목록 조회
        queryParams.setSize(1000); // 모든 옵션그룹 조회
        PageInfo<OptionGroup> pageInfo = optionGroupService.page(queryParams);
        model.addAttribute("optionGroups", pageInfo.getList());
        return "page/admin/products/create";
    }

    /**
     * 상품 수정 페이지
     */
    @GetMapping("/{id}")
    public String updateForm(@PathVariable("id") String id, Model model, QueryParams queryParams) {
        // 상품 정보 조회
        Products product = productService.selectById(id);
        model.addAttribute("product", product);
        // 카테고리 목록 조회
        List<Category> categories = categoryService.list();
        model.addAttribute("categories", categories);
        // 대분류 목록 조회
        List<CategoryLarge> categoryLargeList = categoryLargeService.listByCategory(product.getCategoryNo());
        model.addAttribute("categoryLargeList", categoryLargeList);
        // 옵션그룹 목록 조회
        queryParams.setSize(1000); // 모든 옵션그룹 조회
        PageInfo<OptionGroup> pageInfo = optionGroupService.page(queryParams);
        model.addAttribute("optionGroups", pageInfo.getList());
        return "page/admin/products/update";
    }

   
    /**
     * 카테고리별 대분류 목록 조회 (AJAX)
     */
    @GetMapping("/categories/{categoryNo}/large")
    @ResponseBody
    public java.util.List<CategoryLarge> getCategoryLargeList(@PathVariable Long categoryNo) {
        QueryParams queryParams = new QueryParams();
        queryParams.setSize(1000); // 모든 대분류 조회
        PageInfo<CategoryLarge> pageInfo = categoryLargeService.listByCategory(categoryNo, queryParams);
        return pageInfo.getList();
    }

    // 상품 미디어 관리
    @GetMapping("/{id}/media")
    public String productMedia(@PathVariable("id") String id, Model model) {
        Products product = productService.selectById(id);
        if (product == null) {
            log.warn("Product not found for id: {}", id);
            return "redirect:/admin/products";
        }
        model.addAttribute("product", product);
        return "page/admin/products/media";
    }
}
