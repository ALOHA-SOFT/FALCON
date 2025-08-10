package com.falcon.shop.controller.products;

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
import com.falcon.shop.domain.products.Products;
import com.falcon.shop.service.products.CategoryLargeService;
import com.falcon.shop.service.products.CategoryService;
import com.falcon.shop.service.products.OptionGroupService;
import com.falcon.shop.service.products.ProductService;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Controller
@RequestMapping({"/products", "/product", "/goods"})
public class ProductController {

    @Autowired ProductService productService;
    @Autowired OptionGroupService optionGroupService;
    @Autowired CategoryService categoryService;
    @Autowired CategoryLargeService categoryLargeService;

    /**
     * 상품 목록 페이지
     * @return
     */
    @GetMapping("")
    public String productHome(
        Model model, 
        Pagination pagination,
        QueryParams queryParams,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "size", defaultValue = "20") int size
    ) {

        queryParams.setPage(page);
        queryParams.setSize(size);
        log.info("productHome queryParams : {}", queryParams);
        PageInfo<Products> pageInfo = productService.page(queryParams);
        log.info("pageInfo : {}",pageInfo);
        model.addAttribute("pageInfo", pageInfo);
        Long total = pageInfo.getTotal();
        pagination.setSize(size);
        pagination.setTotal(total);
        model.addAttribute("pagination", pagination);

        // BEST 상품 4개
        queryParams.setPage(1);
        queryParams.setSize(4);
        PageInfo<Products> bestPageInfo = productService.pageBEST(queryParams);
        model.addAttribute("bestProducts", bestPageInfo.getList());
        // NEW 상품 4개
        // PageInfo<Products> newPageInfo = productService.pageNEW(queryParams);
        // model.addAttribute("newProducts", newPageInfo.getList());
        // 카테고리 목록
        List<Category> categoryList = categoryService.list();
        model.addAttribute("categoryList", categoryList);
        // 대분류 목록
        List<CategoryLarge> categoryLargeList = categoryLargeService.list();
        model.addAttribute("categoryLargeList", categoryLargeList);

        // Uri 빌더
        String pageUri = UriComponentsBuilder.fromPath("/products")
                                            // Pagination
                                             .queryParam("size", pagination.getSize())
                                             .queryParam("count", pagination.getCount())
                                            // PageHelper
                                            //  .queryParam("size", pageInfo.getSize())
                                            //  .queryParam("count", pageInfo.getPageSize())
                                             .build()
                                             .toUriString();
        model.addAttribute("pageUri", pageUri);
        return "page/products/list";
    }
    


    /**
     * 상품 상세 페이지
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{id}")
    public String productDetail(@PathVariable("id") String id, Model model) {
        log.info("productDetail id : {}", id);
        try {
            // 상품 정보 조회
            Products product = productService.selectById(id);
            model.addAttribute("product", product);
            // 상품 옵션그룹 조회
            Long optionGroupNo = product.getOptionGroupNo();
            if (optionGroupNo != null) {
                model.addAttribute("optionGroup", optionGroupService.select(optionGroupNo));
            }
            // 관련 상품
            List<Products> relatedProducts = productService.relatedList(product.getCategoryNo());
            log.info("relatedProducts : {}", relatedProducts);
            model.addAttribute("relatedProducts", relatedProducts);
            
        } catch (Exception e) {
            return "redirect:/products";
        }
        return "page/products/detail";
    }
    

    /**
     * 상품 목록 - 카테고리별 조회
     * @param categoryNo
     * @param model
     * @param pagination
     * @param queryParams
     * @return
     */
    @GetMapping("/category/{categoryNo}")
    public String productByCategory(
        @PathVariable("categoryNo") Long categoryNo, 
        Model model, 
        Pagination pagination,
        QueryParams queryParams,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "size", defaultValue = "12") int size
    ) {
        queryParams.setPage(page);
        queryParams.setSize(size);
        log.info("productByCategory categoryNo : {}", categoryNo);
        model.addAttribute("categoryNo", categoryNo);
        PageInfo<Products> pageInfo = productService.page(queryParams, categoryNo);
        log.info("pageInfo : {}", pageInfo);
        model.addAttribute("pageInfo", pageInfo);
        Long total = pageInfo.getTotal();
        pagination.setSize(size);
        pagination.setTotal(total);
        model.addAttribute("pagination", pagination);
        // BEST 상품 4개
        queryParams.setPage(1);
        queryParams.setSize(4);
        PageInfo<Products> bestPageInfo = productService.pageBEST(queryParams);
        model.addAttribute("bestProducts", bestPageInfo.getList());
        // 카테고리 목록
        List<Category> categoryList = categoryService.list();
        model.addAttribute("categoryList", categoryList);
        // 대분류 목록
        List<CategoryLarge> categoryLargeList = categoryLargeService.listByCategory(categoryNo);
        model.addAttribute("categoryLargeList", categoryLargeList);

        // Uri 빌더
        String pageUri = UriComponentsBuilder.fromPath("/products/category/" + categoryNo)
                                            // Pagination
                                             .queryParam("size", pagination.getSize())
                                             .queryParam("count", pagination.getCount())
                                            // PageHelper
                                            //  .queryParam("size", pageInfo.getSize())
                                            //  .queryParam("count", pageInfo.getPageSize())
                                             .build()
                                             .toUriString();
        model.addAttribute("pageUri", pageUri);
        
        return "page/products/list";
    }
    

    // 상품 목록 - 대분류별 조회
    @GetMapping("/category/{categoryNo}/{categoryLargeNo}")
    public String productByCategoryLarge(
        @PathVariable("categoryNo") Long categoryNo,
        @PathVariable("categoryLargeNo") Long categoryLargeNo, 
        Model model, 
        Pagination pagination,
        QueryParams queryParams,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "size", defaultValue = "12") int size
    ) {
        queryParams.setPage(page);
        queryParams.setSize(size);
        log.info("productByCategoryLarge categoryLargeNo : {}", categoryLargeNo);
        model.addAttribute("categoryNo", categoryNo);
        model.addAttribute("categoryLargeNo", categoryLargeNo);
        PageInfo<Products> pageInfo = productService.page(queryParams, categoryNo, categoryLargeNo);
        log.info("pageInfo : {}", pageInfo);
        model.addAttribute("pageInfo", pageInfo);
        Long total = pageInfo.getTotal();
        pagination.setSize(size);
        pagination.setTotal(total);
        model.addAttribute("pagination", pagination);
        // BEST 상품 4개
        queryParams.setPage(1);
        queryParams.setSize(4);
        PageInfo<Products> bestPageInfo = productService.pageBEST(queryParams);
        model.addAttribute("bestProducts", bestPageInfo.getList());
        // 카테고리 목록
        List<Category> categoryList = categoryService.list();
        model.addAttribute("categoryList", categoryList);
        // 대분류 목록
        List<CategoryLarge> categoryLargeList = categoryLargeService.listByCategory(categoryNo);
        model.addAttribute("categoryLargeList", categoryLargeList);
        
        // Uri 빌더
        String pageUri = UriComponentsBuilder.fromPath("/products/category/" + categoryNo + "/" + categoryLargeNo)
                                             .queryParam("search", queryParams.getSearch())
                                             .queryParam("size", pagination.getSize())
                                             .queryParam("count", pagination.getCount())
                                             .build()
                                             .toUriString();
        model.addAttribute("pageUri", pageUri);
        
        return "page/products/list";
    }
}
