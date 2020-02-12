package io.synlabs.synvision.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.synlabs.synvision.entity.core.Module;
import io.synlabs.synvision.jpa.ModuleRepository;
import io.synlabs.synvision.views.core.Menu;
import io.synlabs.synvision.views.core.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Component
public class UserMenuBuilder {
    private static final Logger logger = LoggerFactory.getLogger(UserMenuBuilder.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ModuleRepository moduleRepository;

    private ObjectMapper jsonMapper = new ObjectMapper();

    private Menu navigation;


    @PostConstruct
    public void init() {

        //List<Module> modules = moduleRepository.findByEnabledTrue();

        navigation = new Menu();
        try {
            Resource[] resources
                    = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                    .getResources("classpath*:/menu/*.json");

            for (Resource resource : resources) {
                logger.info("Loaded menu from {}", resource.getFilename());
                MenuItem item = jsonMapper.readValue(resource.getFile(), MenuItem.class);
                navigation.merge(item);
            }


        } catch (IOException e) {
            logger.error("Cannot load menu from disk!", e);
        }
    }

    public Menu getMenu() {
        return navigation;
    }
}

/*
TODO :
 a. user level based on role/privilege
 b. check if the module is enabled or not

 */
