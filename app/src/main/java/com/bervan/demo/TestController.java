package com.bervan.demo;


import com.bervan.demo.manualconfiguration.model.ProjectHistoryOne;
import com.bervan.demo.manualconfiguration.model.ProjectOne;
import com.bervan.demo.manualconfiguration.model.UserOne;
import com.bervan.demo.manualconfiguration.repo.ProjectHistoryOneRepository;
import com.bervan.demo.manualconfiguration.repo.ProjectOneRepositoryCustom;
import com.bervan.demo.manualconfiguration.repo.UserOneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {
    @Autowired
    private ProjectHistoryOneRepository projectHistoryRepository;    
    @Autowired
    private UserOneRepository userRepository;    
    @Autowired
    private ProjectOneRepositoryCustom projectRepository;
    @Autowired
    private ExportEntities exportEntities;
    
    @GetMapping("/test")
    public void test1() {
        UserOne creator = UserOne.builder().nick("joedoe")
                .name("Joe")
                .lastName("Doe")
                .build();

        creator = userRepository.save(creator);

        ProjectOne project = ProjectOne.builder()
                .name("App project")
                .description("This is project about application!")
                .creator(creator)
                .build();

        project = projectRepository.save(project);

        List<ProjectHistoryOne> all = projectHistoryRepository.findAll();

        project.setName("Changed app project name");
        project.setDescription("Changed description but should not be saved in history!");

        project = projectRepository.save(project);

        all = projectHistoryRepository.findAll();
    }

    @GetMapping("/exportEntities1")
    public void exportEntities1() {
        exportEntities.exportAndSaveLoadAndImport();
    }
}
