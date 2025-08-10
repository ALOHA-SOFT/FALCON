package com.falcon.shop.api.system;

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

import com.falcon.shop.domain.system.SeqGroups;
import com.falcon.shop.service.system.SeqGroupsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/seqgroups")
public class SeqGroupsApi {
  
  @Autowired private SeqGroupsService seqGroupsService;
  
  @GetMapping()
  public ResponseEntity<?> getAll(
    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
    @RequestParam(value = "size", required = false, defaultValue = "10") int size
  ) {
      try {
          return new ResponseEntity<>(seqGroupsService.page(page, size), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<?> getOne(@PathVariable("id") String id) {
      try {
          return new ResponseEntity<>(seqGroupsService.selectById(id), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PostMapping(path = "", consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> createForm(SeqGroups seqGroups) {
      log.info("## FORM ##");
      log.info("seqGroups={}", seqGroups);
      try {
          return new ResponseEntity<>(seqGroupsService.insert(seqGroups), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @PostMapping(path = "", consumes = "multipart/form-data")
  public ResponseEntity<?> createMultiPartForm(SeqGroups seqGroups) {
      log.info("## MULTIPART ##");
      log.info("seqGroups={}", seqGroups);
      try {
          return new ResponseEntity<>(seqGroupsService.insert(seqGroups), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @PostMapping(path = "", consumes = "application/json")
  public ResponseEntity<?> create(@RequestBody SeqGroups seqGroups) {
      log.info("## JSON ##");
      log.info("seqGroups={}", seqGroups);
      try {
          return new ResponseEntity<>(seqGroupsService.insert(seqGroups), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> updateForm(SeqGroups seqGroups) {
      try {
          return new ResponseEntity<>(seqGroupsService.updateById(seqGroups), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "multipart/form-data")
  public ResponseEntity<?> updateMultiPartForm(SeqGroups seqGroups) {
      try {
          return new ResponseEntity<>(seqGroupsService.updateById(seqGroups), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @PutMapping(path = "", consumes = "application/json")
  public ResponseEntity<?> update(@RequestBody SeqGroups seqGroups) {
      try {
          return new ResponseEntity<>(seqGroupsService.updateById(seqGroups), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<?> destroy(@PathVariable("id") String id) {
      try {
          return new ResponseEntity<>(seqGroupsService.deleteById(id), HttpStatus.OK);
      } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

}
