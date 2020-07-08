package io.synlabs.synvision.service;


import io.synlabs.synvision.auth.LicenseServerAuth;
import io.synlabs.synvision.entity.core.Privilege;
import io.synlabs.synvision.entity.core.Role;
import io.synlabs.synvision.jpa.PrivilegeRepository;
import io.synlabs.synvision.jpa.RoleRepository;
import io.synlabs.synvision.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Service
public class PrivilegeService extends BaseService
{
  private static Logger logger = LoggerFactory.getLogger(PrivilegeService.class);

  @Autowired
  private PrivilegeRepository privilegeRepository;

  @Autowired
  private RoleRepository roleRepository;

  @PostConstruct
  public void init()
  {
    Field[] fields = LicenseServerAuth.Privileges.class.getDeclaredFields();
    for (Field f : fields)
    {
      if (Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()))
      {
        logger.info("Found privilege {} ", f.getName());
        if (!(privilegeRepository.countByName(f.getName()) > 0))
        {
          Privilege p = new Privilege();
          p.setName(f.getName());
          privilegeRepository.saveAndFlush(p);
          logger.info("Not in db, saved {}", f.getName());

          //attach it to tech admin role:)
          Role role = roleRepository.getOneByName("TECHADMIN");
          if (role == null)
          {
            role = new Role();
            role.setName("TECHADMIN");
            roleRepository.saveAndFlush(role);
          }
          role.addPrivilege(p);
          roleRepository.saveAndFlush(role);
          logger.info("attached to tech admin, saved {}", f.getName());
        }
      }
    }
  }
}

