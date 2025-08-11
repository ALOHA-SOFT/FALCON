package com.falcon.shop.api.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.falcon.shop.domain.users.Carts;
import com.falcon.shop.domain.users.CustomUser;
import com.falcon.shop.service.users.CartService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users/cart")
public class CartApi {
  
  @Autowired private CartService cartService;
  
  @GetMapping()
  public ResponseEntity<?> getAll(
    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
    @RequestParam(value = "size", required = false, defaultValue = "10") int size,
    @AuthenticationPrincipal CustomUser customUser
  ) {
      try {
          if (customUser != null) {
              // 로그인된 사용자의 장바구니만 가져오기
              Long userNo = customUser.getUser().getNo();
              List<Carts> cartList = cartService.listByUserNo(userNo);
              log.info("User's cart items: {}", cartList);
              return new ResponseEntity<>(cartList, HttpStatus.OK);
          }
          return new ResponseEntity<>(cartService.page(page, size), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<?> getOne(@PathVariable("id") String id) {
      try {
          return new ResponseEntity<>(cartService.selectById(id), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PostMapping(path = "", consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> createForm(Carts cart,
    @AuthenticationPrincipal CustomUser customUser
  ) {
      log.info("## FORM ##");
      log.info("cart={}", cart);
      Long userNo = customUser.getUser().getNo();
      cart.setUserNo(userNo);
      try {
          return new ResponseEntity<>(cartService.insert(cart), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @PostMapping(path = "", consumes = "multipart/form-data")
  public ResponseEntity<?> createMultiPartForm(Carts cart,
    @AuthenticationPrincipal CustomUser customUser
  ) {
      log.info("## MULTIPART ##");
      log.info("cart={}", cart);
      Long userNo = customUser.getUser().getNo();
      cart.setUserNo(userNo);
      try {
          return new ResponseEntity<>(cartService.insert(cart), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @PostMapping(path = "", consumes = "application/json")
  public ResponseEntity<?> create(@RequestBody Carts cart,
    @AuthenticationPrincipal CustomUser customUser
  ) {
      log.info("## JSON ##");
      log.info("cart={}", cart);
      Long userNo = customUser.getUser().getNo();
      cart.setUserNo(userNo);
      try {
          return new ResponseEntity<>(cartService.insert(cart), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "/{id}", consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> updateQuantityForm(@PathVariable("id") String id, 
    @RequestParam("quantity") Long quantity) {
      try {
          Carts cart = new Carts();
          cart.setNo(Long.parseLong(id));
          cart.setQuantity(quantity);
          return new ResponseEntity<>(cartService.updateById(cart), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> updateForm(Carts cart) {
      try {
          return new ResponseEntity<>(cartService.updateById(cart), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "multipart/form-data")
  public ResponseEntity<?> updateMultiPartForm(Carts cart) {
      try {
          return new ResponseEntity<>(cartService.updateById(cart), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "application/json")
  public ResponseEntity<?> update(@RequestBody Carts cart) {
      try {
          return new ResponseEntity<>(cartService.updateById(cart), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<?> destroy(@PathVariable("id") String id) {
      try {
          return new ResponseEntity<>(cartService.deleteById(id), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

}
