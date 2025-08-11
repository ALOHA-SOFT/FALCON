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

import com.falcon.shop.domain.email.EmailTemplate;
import com.falcon.shop.service.email.EmailTemplateService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/email-template")
public class EmailTemplateApi {
  
    @Autowired 
    private EmailTemplateService emailTemplateService;
  
    @GetMapping()
    public ResponseEntity<?> getAll(
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        try {
            return new ResponseEntity<>(emailTemplateService.page(page, size), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting email template list", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(emailTemplateService.selectById(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting email template by id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getByType(@PathVariable("type") String type) {
        try {
            return new ResponseEntity<>(emailTemplateService.getByType(type), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting email template by type: {}", type, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveTemplates() {
        try {
            return new ResponseEntity<>(emailTemplateService.getActiveTemplates(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting active email templates", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @PostMapping(path = "", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<?> createForm(EmailTemplate emailTemplate) {
        log.info("## FORM ##");
        log.info("emailTemplate={}", emailTemplate);
        try {
            return new ResponseEntity<>(emailTemplateService.insert(emailTemplate), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error creating email template", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "", consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody EmailTemplate emailTemplate) {
        log.info("## JSON ##");
        log.info("emailTemplate={}", emailTemplate);
        try {
            return new ResponseEntity<>(emailTemplateService.insert(emailTemplate), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error creating email template", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @PutMapping(path = "", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<?> updateForm(EmailTemplate emailTemplate) {
        try {
            return new ResponseEntity<>(emailTemplateService.updateById(emailTemplate), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating email template", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @PutMapping(path = "", consumes = "application/json")
    public ResponseEntity<?> update(@RequestBody EmailTemplate emailTemplate) {
        try {
            return new ResponseEntity<>(emailTemplateService.updateById(emailTemplate), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating email template", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(emailTemplateService.deleteById(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting email template by id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 템플릿 활성화/비활성화 토글
     */
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleActive(@PathVariable("id") String id) {
        try {
            EmailTemplate template = emailTemplateService.selectById(id);
            if (template == null) {
                return new ResponseEntity<>("Template not found", HttpStatus.NOT_FOUND);
            }
            
            template.setIsActive(!template.getIsActive());
            emailTemplateService.updateById(template);
            
            return new ResponseEntity<>("Template status updated", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error toggling template status by id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
