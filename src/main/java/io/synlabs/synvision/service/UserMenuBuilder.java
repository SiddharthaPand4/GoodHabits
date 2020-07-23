package io.synlabs.synvision.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.synlabs.synvision.entity.core.Module;
import io.synlabs.synvision.entity.core.SynVisionUser;
import io.synlabs.synvision.ex.UploadException;
import io.synlabs.synvision.jpa.ModuleRepository;
import io.synlabs.synvision.views.core.Menu;
import io.synlabs.synvision.views.core.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Component
public class UserMenuBuilder {
    private static final Logger logger = LoggerFactory.getLogger(UserMenuBuilder.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ModuleRepository moduleRepository;

    private ObjectMapper jsonMapper = new ObjectMapper();
    private Map<String, MenuItem> menuMap = new TreeMap<>();

    private Menu navigation;

    @Value("${file.upload-dir}")
    private String uploaddir;


    @PostConstruct
    public void init() {

        //List<Module> modules = moduleRepository.findByEnabledTrue();
        Path uploadpath = Paths.get(uploaddir);
        if (uploadpath.toFile().exists()) {
            if (!uploadpath.toFile().isDirectory()) {
                logger.error("{} is not a dir, cant continue", uploaddir);
                throw new UploadException(uploaddir + "is not a dir, cant continue");
            }
        } else {
            if (!uploadpath.toFile().mkdirs()) {
                logger.error("cannot create upload directory", uploaddir);
                throw new UploadException(uploaddir + "cannot create upload directory!");
            }
        }

        navigation = new Menu();
        try {
            Resource[] resources
                    = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                    .getResources("classpath*:/menu/*.json");

            for (Resource resource : resources) {
                Path finalOutputPath = uploadpath.resolve(resource.getFilename());
                InputStream inputStream = resource.getInputStream();
                Files.copy(inputStream, finalOutputPath, StandardCopyOption.REPLACE_EXISTING);
                File file = finalOutputPath.toFile();

                logger.info("Loaded menu from {}", file.getName());
                MenuItem item = jsonMapper.readValue(file, MenuItem.class);
                menuMap.put(item.getKey(),item);

            }


        } catch (IOException e) {
            logger.error("Cannot load menu from disk!", e);
        }
    }

    public Menu getMenu(SynVisionUser currentUser) {

        Menu navigation= new Menu();
            for (MenuItem menu : menuMap.values()) {
                if(menu.getSubmenu()==null)
                {
                    if(currentUser.getPrivileges().contains(menu.getPrivilege())){
                        navigation.add(menu);
                    }
                }
                else{
                    for (MenuItem submenu : menu.getSubmenu()) {
                        submenu.setParent(menu.getKey());
                        if (currentUser.getPrivileges().contains(submenu.getPrivilege())) {
                            if(submenu.getParent().equals(menu.getKey()))
                            {navigation.merge(menu,submenu);}
                        }
                    }
                }
            }

            return navigation;
    }
}

/*
TODO :
 a. user level based on role/privilege
 b. check if the module is enabled or not
 */
