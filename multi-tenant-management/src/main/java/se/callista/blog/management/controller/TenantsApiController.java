package se.callista.blog.management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.callista.blog.management.service.TenantManagementService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class TenantsApiController {

    private final TenantManagementService tenantManagementService;

    @PostMapping("/tenants")
    public ResponseEntity<Void> createTenant(@RequestParam String tenantId) {
        tenantManagementService.createTenant(tenantId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
