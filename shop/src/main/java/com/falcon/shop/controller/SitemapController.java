package com.falcon.shop.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.falcon.shop.domain.products.Category;
import com.falcon.shop.domain.products.Products;
import com.falcon.shop.service.products.CategoryService;
import com.falcon.shop.service.products.ProductService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SitemapController {

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;

    /**
     * 동적 사이트맵 생성
     * @return XML 형태의 사이트맵
     */
    @GetMapping(value = "/sitemap-dynamic.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String generateSitemap() {
        log.info("동적 사이트맵 생성 요청");
        
        StringBuilder sitemap = new StringBuilder();
        String baseUrl = "https://falcon.com";
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // XML 헤더
        sitemap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sitemap.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        
        // 메인 페이지
        addUrl(sitemap, baseUrl + "/", currentDate, "daily", "1.0");
        
        // 상품 목록 페이지
        addUrl(sitemap, baseUrl + "/products", currentDate, "daily", "0.9");
        
        // 회사 소개
        addUrl(sitemap, baseUrl + "/info", currentDate, "monthly", "0.7");
        
        // 이용약관
        addUrl(sitemap, baseUrl + "/terms/service", currentDate, "monthly", "0.5");
        addUrl(sitemap, baseUrl + "/terms/privacy", currentDate, "monthly", "0.5");
        
        try {
            // 카테고리 페이지들
            List<Category> categories = categoryService.list();
            if (categories != null) {
                for (Category category : categories) {
                    addUrl(sitemap, baseUrl + "/products/category/" + category.getNo(), 
                           currentDate, "weekly", "0.8");
                }
            }
            
            // 상품 상세 페이지들 (기존 상품 목록 활용)
            try {
                com.falcon.shop.domain.common.QueryParams queryParams = new com.falcon.shop.domain.common.QueryParams();
                queryParams.setPage(1);
                queryParams.setSize(100);
                com.github.pagehelper.PageInfo<Products> pageInfo = productService.page(queryParams);
                List<Products> products = pageInfo.getList();
                
                if (products != null) {
                    for (Products product : products) {
                        addUrl(sitemap, baseUrl + "/products/" + product.getId(), 
                               currentDate, "weekly", "0.6");
                    }
                }
            } catch (Exception e) {
                log.error("상품 목록 조회 중 오류: ", e);
            }
        } catch (Exception e) {
            log.error("사이트맵 생성 중 오류: ", e);
        }
        
        // XML 종료
        sitemap.append("</urlset>");
        
        return sitemap.toString();
    }
    
    /**
     * URL 엔트리 추가 헬퍼 메서드
     */
    private void addUrl(StringBuilder sitemap, String loc, String lastmod, 
                       String changefreq, String priority) {
        sitemap.append("  <url>\n");
        sitemap.append("    <loc>").append(loc).append("</loc>\n");
        sitemap.append("    <lastmod>").append(lastmod).append("</lastmod>\n");
        sitemap.append("    <changefreq>").append(changefreq).append("</changefreq>\n");
        sitemap.append("    <priority>").append(priority).append("</priority>\n");
        sitemap.append("  </url>\n");
    }
}
