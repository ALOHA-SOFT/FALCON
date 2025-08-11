package com.falcon.shop.api.admin;

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

import com.falcon.shop.domain.email.Email;
import com.falcon.shop.service.email.EmailService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/email")
public class EmailApi {
  
    @Autowired 
    private EmailService emailService;
  
    @GetMapping()
    public ResponseEntity<?> getAll(
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        try {
            return new ResponseEntity<>(emailService.page(page, size), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting email list", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(emailService.selectById(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting email by id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @PostMapping(path = "", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<?> createForm(Email email) {
        log.info("## FORM ##");
        log.info("email={}", email);
        try {
            return new ResponseEntity<>(emailService.insert(email), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error creating email", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "", consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody Email email) {
        log.info("## JSON ##");
        log.info("email={}", email);
        try {
            return new ResponseEntity<>(emailService.insert(email), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error creating email", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @PutMapping(path = "", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<?> updateForm(Email email) {
        try {
            return new ResponseEntity<>(emailService.updateById(email), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating email", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @PutMapping(path = "", consumes = "application/json")
    public ResponseEntity<?> update(@RequestBody Email email) {
        try {
            return new ResponseEntity<>(emailService.updateById(email), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating email", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(emailService.deleteById(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting email by id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 이메일 재발송
     */
    @PostMapping("/{id}/resend")
    public ResponseEntity<?> resend(@PathVariable("id") String id) {
        try {
            Email email = emailService.selectById(id);
            if (email == null) {
                return new ResponseEntity<>("Email not found", HttpStatus.NOT_FOUND);
            }
            
            // 이메일 재발송 로직
            boolean result = emailService.resendEmail(email.getNo());
            if (result) {
                return new ResponseEntity<>("Email resent successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to resend email", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error resending email by id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
