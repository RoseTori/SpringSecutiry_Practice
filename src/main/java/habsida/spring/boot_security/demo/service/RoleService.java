package habsida.spring.boot_security.demo.service;

import habsida.spring.boot_security.demo.models.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {

    List<Role> findAll();
    Set<Role> findByIds(List<Long> roleIds);
}
