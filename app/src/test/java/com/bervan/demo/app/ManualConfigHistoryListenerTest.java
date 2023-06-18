package com.bervan.demo.app;

import com.bervan.demo.manualconfiguration.model.ProjectHistoryOne;
import com.bervan.demo.manualconfiguration.model.ProjectOne;
import com.bervan.demo.manualconfiguration.model.UserOne;
import com.bervan.demo.manualconfiguration.repo.ProjectHistoryOneRepository;
import com.bervan.demo.manualconfiguration.repo.ProjectOneRepositoryCustom;
import com.bervan.demo.manualconfiguration.repo.UserOneRepository;
import com.bervan.history.model.AbstractBaseHistoryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {AppTestConfig.class, EntityManager.class, EntityManagerFactory.class})
@ExtendWith(SpringExtension.class)
class ManualConfigHistoryListenerTest {
    @Autowired
    private UserOneRepository userRepository;
    @Autowired
    private ProjectOneRepositoryCustom projectRepository;
    @Autowired
    private ProjectHistoryOneRepository projectHistoryRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        projectRepository.deleteAll();
        projectHistoryRepository.deleteAll();
    }

    @Test
    void verifyOnUpdateHistoryCreatedInDB() {
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

        project = projectRepository.saveWithHistory(project);

        List<ProjectHistoryOne> all = projectHistoryRepository.findAll();

        assertThat(all).hasSize(0);

        project.setName("Changed app project name");
        project.setDescription("Changed description but should not be saved in history!");

        project = projectRepository.saveWithHistory(project);

        all = projectHistoryRepository.findAll();
        assertThat(all).hasSize(1);

        ProjectHistoryOne projectHistory = all.get(0);
        assertThat(projectHistory.getCreator()).isEqualTo(creator.getId());
        assertThat(projectHistory.getEntity().getId()).isEqualTo(project.getId());
        assertThat(projectHistory.getName()).isEqualTo("App project");
        assertThat(projectHistory.getDescription()).isEqualTo(null);

        Set<? extends AbstractBaseHistoryEntity<UUID>> historyEntities = project.getHistoryEntities();

    }

}
