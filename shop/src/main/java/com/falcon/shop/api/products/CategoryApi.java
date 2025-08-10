package com.falcon.shop.api.products;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.falcon.shop.domain.products.Category;
import com.falcon.shop.service.products.CategoryService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/category")
public class CategoryApi {
  
  @Autowired private CategoryService categoryService;
  
  @GetMapping()
  public ResponseEntity<?> getAll(
    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
    @RequestParam(value = "size", required = false, defaultValue = "10") int size
  ) {
      try {
          return new ResponseEntity<>(categoryService.page(page, size), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<?> getOne(@PathVariable("id") String id) {
      try {
          return new ResponseEntity<>(categoryService.selectById(id), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PostMapping(path = "", consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> createForm(Category category) {
      log.info("## FORM ##");
      log.info("category={}", category);
      try {
          return new ResponseEntity<>(categoryService.insert(category), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @PostMapping(path = "", consumes = "multipart/form-data")
  public ResponseEntity<?> createMultiPartForm(Category category) {
      log.info("## MULTIPART ##");
      log.info("category={}", category);
      try {
          return new ResponseEntity<>(categoryService.insert(category), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @PostMapping(path = "", consumes = "application/json")
  public ResponseEntity<?> create(@RequestBody Category category) {
      log.info("## JSON ##");
      log.info("category={}", category);
      try {
          return new ResponseEntity<>(categoryService.insert(category), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> updateForm(Category category) {
      try {
          return new ResponseEntity<>(categoryService.updateById(category), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "multipart/form-data")
  public ResponseEntity<?> updateMultiPartForm(Category category) {
      try {
          return new ResponseEntity<>(categoryService.updateById(category), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "application/json")
  public ResponseEntity<?> update(@RequestBody Category category) {
      try {
          return new ResponseEntity<>(categoryService.updateById(category), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<?> destroy(@PathVariable("id") String id) {
      try {
          return new ResponseEntity<>(categoryService.deleteById(id), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @GetMapping("/{no}/category-large")
  public ResponseEntity<?> categoryLargeByCategory(@PathVariable("no") Long no) {
    log.info("Fetching category large for category no: {}", no);
    try {
        Category category = categoryService.select(no);
        if (category != null) {
            return new ResponseEntity<>(category.getCategoryLargeList(), HttpStatus.OK);
        }
    } catch (Exception e) {
        log.error("Error fetching category large for no {}: {}", no, e.getMessage());
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
  

}
