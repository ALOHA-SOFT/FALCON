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

import com.falcon.shop.domain.products.CategoryLarge;
import com.falcon.shop.service.products.CategoryLargeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/category-large")
public class CategoryLargeApi {
  
    @Autowired 
    private CategoryLargeService categoryLargeService;
  
    @GetMapping()
    public ResponseEntity<?> getAll(
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        try {
            return new ResponseEntity<>(categoryLargeService.page(page, size), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting all category large", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") String id) {
        try {
            CategoryLarge categoryLarge = categoryLargeService.selectById(id);
            if (categoryLarge == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(categoryLarge, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting category large by id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @PostMapping(path = "", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<?> createForm(CategoryLarge categoryLarge) {
        log.info("## FORM ##");
        log.info("categoryLarge={}", categoryLarge);
        try {
            boolean result = categoryLargeService.insert(categoryLarge);
            if (result) {
                return new ResponseEntity<>(categoryLarge, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error creating category large (form)", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "", consumes = "multipart/form-data")
    public ResponseEntity<?> createMultiPartForm(CategoryLarge categoryLarge) {
        log.info("## MULTIPART ##");
        log.info("categoryLarge={}", categoryLarge);
        try {
            boolean result = categoryLargeService.insert(categoryLarge);
            if (result) {
                return new ResponseEntity<>(categoryLarge, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error creating category large (multipart)", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "", consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody CategoryLarge categoryLarge) {
        log.info("## JSON ##");
        log.info("categoryLarge={}", categoryLarge);
        try {
            boolean result = categoryLargeService.insert(categoryLarge);
            if (result) {
                return new ResponseEntity<>(categoryLarge, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error creating category large (json)", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @PutMapping(path = "", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<?> updateForm(CategoryLarge categoryLarge) {
        try {
            boolean result = categoryLargeService.updateById(categoryLarge);
            if (result) {
                return new ResponseEntity<>(categoryLarge, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error updating category large (form)", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @PutMapping(path = "", consumes = "multipart/form-data")
    public ResponseEntity<?> updateMultiPartForm(CategoryLarge categoryLarge) {
        try {
            boolean result = categoryLargeService.updateById(categoryLarge);
            if (result) {
                return new ResponseEntity<>(categoryLarge, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error updating category large (multipart)", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @PutMapping(path = "", consumes = "application/json")
    public ResponseEntity<?> update(@RequestBody CategoryLarge categoryLarge) {
        try {
            boolean result = categoryLargeService.updateById(categoryLarge);
            if (result) {
                return new ResponseEntity<>(categoryLarge, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error updating category large (json)", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @DeleteMapping("/{id}")
    public ResponseEntity<?> destroy(@PathVariable("id") String id) {
        try {
            boolean result = categoryLargeService.deleteById(id);
            if (result) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error deleting category large by id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
