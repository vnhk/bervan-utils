package com.bervan.demo.app;

import com.bervan.demo.autoconfiguration.model.ProjectHistoryTwo;
import com.bervan.demo.autoconfiguration.model.ProjectTwo;
import com.bervan.demo.autoconfiguration.model.UserTwo;
import com.bervan.demo.autoconfiguration.repo.ProjectHistoryTwoRepository;
import com.bervan.demo.autoconfiguration.repo.ProjectTwoRepositoryCustom;
import com.bervan.demo.autoconfiguration.repo.UserTwoRepository;
import com.bervan.ieentities.BaseExcelExport;
import com.bervan.ieentities.BaseExcelImport;
import com.bervan.ieentities.ExcelIEEntity;
import com.bervan.ieentities.LoadIEAvailableEntities;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {AppTestConfig.class, EntityManager.class, EntityManagerFactory.class})
@ExtendWith(SpringExtension.class)
class BaseImportExportTest {
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
    public void exportAndSaveLoadAndImport() {
        List<UserTwo> users = generateUsers(50, true);
        List<ProjectTwo> projectTwos = generateProjects(1500, users, true);
        List<ProjectHistoryTwo> historyTwos = generateProjectsHistory(15000, projectTwos, users, true);

        List<ExcelIEEntity<?>> entities = new ArrayList<>();
        entities.addAll(users);
        entities.addAll(projectTwos);
        entities.addAll(historyTwos);

        Workbook export = new BaseExcelExport().exportExcel(entities, null);
        new BaseExcelExport().save(export, null, null);

        LoadIEAvailableEntities loadIEAvailableEntities = new LoadIEAvailableEntities();
        List<Class<?>> subclassesOf = loadIEAvailableEntities.getSubclassesOfExcelEntity("com.bervan.demo");
        BaseExcelImport baseExcelImport = new BaseExcelImport(subclassesOf);
        Workbook imported = baseExcelImport.load(null, null);
        List<? extends ExcelIEEntity<?>> excelIEEntities = (List<? extends ExcelIEEntity<?>>) baseExcelImport.importExcel(imported);

        assertThat(excelIEEntities).hasSize(entities.size());
    }

    @Test
    public void exportAndSaveLoadAndImportWithDB() {
        List<UserTwo> users = generateUsers(3, false);
        users = userRepository.saveAll(users);
        List<ProjectTwo> projectTwos = generateProjects(25, users, false);
        projectTwos = projectRepository.saveAll(projectTwos);
        List<ProjectHistoryTwo> historyTwos = generateProjectsHistory(55, projectTwos, users, false);
        historyTwos = projectHistoryRepository.saveAll(historyTwos);

        List<ExcelIEEntity<?>> entities = new ArrayList<>();
        entities.addAll(users);
        entities.addAll(projectTwos);
        entities.addAll(historyTwos);

        Workbook export = new BaseExcelExport().exportExcel(entities, null);
        new BaseExcelExport().save(export, "src/test/exportAndSaveLoadAndImportWithDB", null);

        LoadIEAvailableEntities loadIEAvailableEntities = new LoadIEAvailableEntities();
        List<Class<?>> subclassesOf = loadIEAvailableEntities.getSubclassesOfExcelEntity("com.bervan.demo");
        BaseExcelImport baseExcelImport = new BaseExcelImport(subclassesOf);
        Workbook imported = baseExcelImport.load("src/test/exportAndSaveLoadAndImportWithDB", null);
        List<? extends ExcelIEEntity<?>> excelIEEntities = (List<? extends ExcelIEEntity<?>>) baseExcelImport.importExcel(imported);

        assertThat(entities).hasSize(excelIEEntities.size());
    }

    private List<UserTwo> generateUsers(int amount, boolean generateId) {
        List<UserTwo> res = new ArrayList<>();
        for (long i = 1; i <= amount; i++) {
            res.add(UserTwo.builder()
                    .id(generateId ? i : null)
                    .nick("joedoe_" + i)
                    .name("Joe_" + i)
                    .lastName("Doe_" + 1)
                    .build());
        }

        return res;
    }

    private List<ProjectTwo> generateProjects(int amount, List<UserTwo> users, boolean generateId) {
        List<ProjectTwo> res = new ArrayList<>();
        for (long i = 1; i <= amount; i++) {
            UserTwo creator = users.get((int) (i - 1) % users.size());
            res.add(ProjectTwo.builder()
                    .id(generateId ? i : null)
                    .name("App project_" + i)
                    .description("This is project about application_" + i)
                    .creator(creator)
                    .build());
            if (creator.getCreatedProjects() == null)
                creator.setCreatedProjects(new HashSet<>());
            creator.getCreatedProjects().add(res.get(res.size() - 1));
        }

        return res;
    }

    private List<ProjectHistoryTwo> generateProjectsHistory(int amount, List<ProjectTwo> projects, List<UserTwo> users, boolean generateId) {
        List<ProjectHistoryTwo> res = new ArrayList<>();
        for (long i = 1; i <= amount; i++) {
            ProjectTwo project = projects.get((int) (i - 1) % projects.size());
            res.add(ProjectHistoryTwo.builder()
                    .id(generateId ? i : null)
                    .name("History project_" + i)
                    .description("History desc project about application_" + i)
                    .creator(users.get((int) (i - 1) % users.size()).getId())
                    .project(project)
                    .build());

            if (project.getHistory() == null)
                project.setHistory(new HashSet<>());
            project.getHistory().add(res.get(res.size() - 1));
        }

        return res;
    }
}
