package com.falcon.shop.api.shop;

import com.falcon.shop.domain.shop.OrderItemOption;
import com.falcon.shop.service.shop.OrderItemOptionService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/shop/order-item-option")
public class OrderItemOptionApi {

    @Autowired private OrderItemOptionService orderItemOptionService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(orderItemOptionService.selectById(id.toString()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<?> createForm(OrderItemOption option) {
        try {
            return new ResponseEntity<>(orderItemOptionService.insert(option), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "", consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody OrderItemOption option) {
        try {
            return new ResponseEntity<>(orderItemOptionService.insert(option), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<?> updateForm(OrderItemOption option) {
        try {
            return new ResponseEntity<>(orderItemOptionService.updateById(option), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "", consumes = "application/json")
    public ResponseEntity<?> update(@RequestBody OrderItemOption option) {
        try {
            return new ResponseEntity<>(orderItemOptionService.updateById(option), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> destroy(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(orderItemOptionService.deleteById(id.toString()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
