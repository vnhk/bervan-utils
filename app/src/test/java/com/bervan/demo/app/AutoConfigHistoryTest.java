package com.bervan.demo.app;

import com.bervan.demo.autoconfiguration.model.ProjectHistoryTwo;
import com.bervan.demo.autoconfiguration.model.ProjectTwo;
import com.bervan.demo.autoconfiguration.model.UserTwo;
import com.bervan.demo.autoconfiguration.repo.ProjectHistoryTwoRepository;
import com.bervan.demo.autoconfiguration.repo.ProjectTwoRepositoryCustom;
import com.bervan.demo.autoconfiguration.repo.UserTwoRepository;
import javax.persistence.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {AppTestConfig.class, EntityManager.class, EntityManagerFactory.class})
@ExtendWith(SpringExtension.class)
class AutoConfigHistoryTest {
    @Autowired
    private UserTwoRepository userRepository;
    @Autowired
    private ProjectTwoRepositoryCustom projectRepository;
    @Autowired
    private ProjectHistoryTwoRepository projectHistoryRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        projectRepository.deleteAll();
        projectHistoryRepository.deleteAll();
    }

    @Test
    void verifyOnUpdateHistoryCreatedInDB() {
        UserTwo creator = UserTwo.builder().nick("joedoe")
                .name("Joe")
                .lastName("Doe")
                .build();

        creator = userRepository.save(creator);

        ProjectTwo project = ProjectTwo.builder()
                .name("App project")
                .description("This is project about application!")
                .creator(creator)
                .build();

        project = projectRepository.save(project);

        List<ProjectHistoryTwo> all = projectHistoryRepository.findAll();

        assertThat(all).hasSize(0);

        project.setName("Changed app project name");
        project.setDescription("Changed description but should not be saved in history!");

        project = projectRepository.save(project);

        all = projectHistoryRepository.findAll();
        assertThat(all).hasSize(1);

        ProjectHistoryTwo projectHistory = all.get(0);
        assertThat(projectHistory.getCreator()).isEqualTo(creator.getId());
        assertThat(projectHistory.getEntity().getId()).isEqualTo(project.getId());
        assertThat(projectHistory.getName()).isEqualTo("App project");
        assertThat(projectHistory.getDescription()).isEqualTo(null);
    }

}
