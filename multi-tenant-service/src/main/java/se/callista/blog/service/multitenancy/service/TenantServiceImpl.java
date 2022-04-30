package se.callista.blog.service.multitenancy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.callista.blog.service.multitenancy.domain.entity.Tenant;
import se.callista.blog.service.multitenancy.repository.TenantRepository;

@RequiredArgsConstructor
@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    @Override
    public Tenant findByTenantId(String tenantId) {
        return tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("No such tenant: " + tenantId));
    }

}
