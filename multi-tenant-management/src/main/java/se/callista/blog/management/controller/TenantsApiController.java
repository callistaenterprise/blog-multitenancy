package se.callista.blog.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.callista.blog.management.service.TenantManagementService;

@Controller
@RequestMapping("/")
public class TenantsApiController {

    @Autowired
    private TenantManagementService tenantManagementService;

    @PostMapping("/tenants")
    public ResponseEntity<Void> createTenant(@RequestParam String tenantId, @RequestParam String db, @RequestParam String password) {
        tenantManagementService.createTenant(tenantId, db, password);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
