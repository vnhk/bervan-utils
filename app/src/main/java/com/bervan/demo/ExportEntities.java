package com.bervan.demo;

import com.bervan.demo.autoconfiguration.model.ProjectHistoryTwo;
import com.bervan.demo.autoconfiguration.model.ProjectTwo;
import com.bervan.demo.autoconfiguration.model.UserTwo;
import com.bervan.ieentities.BaseExcelExport;
import com.bervan.ieentities.ExcelIEEntity;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class ExportEntities {

    public void exportAndSave() {
        List<UserTwo> users = generateUsers(50);
        List<ProjectTwo> projectTwos = generateProjects(1500, users);
        List<ProjectHistoryTwo> historyTwos = generateProjectsHistory(15000, projectTwos, users);

        List<ExcelIEEntity<?>> entities = new ArrayList<>();
        entities.addAll(users);
        entities.addAll(projectTwos);
        entities.addAll(historyTwos);

        Workbook export = new BaseExcelExport().export(entities, null);
        new BaseExcelExport().save(export, null, null);
    }

    private List<UserTwo> generateUsers(int amount) {
        List<UserTwo> res = new ArrayList<>();
        for (long i = 1; i <= amount; i++) {
            res.add(UserTwo.builder()
                    .id(i)
                    .nick("joedoe_" + i)
                    .name("Joe_" + i)
                    .lastName("Doe_" + 1)
                    .build());
        }

        return res;
    }

    private List<ProjectTwo> generateProjects(int amount, List<UserTwo> users) {
        List<ProjectTwo> res = new ArrayList<>();
        for (long i = 1; i <= amount; i++) {
            UserTwo creator = users.get((int) (i - 1) % users.size());
            res.add(ProjectTwo.builder()
                    .id(i)
                    .name("App project_" + i)
                    .description("This is project about application_" + i)
                    .creator(creator)
                    .build());
            if(creator.getCreatedProjects() == null)
                creator.setCreatedProjects(new HashSet<>());
            creator.getCreatedProjects().add(res.get(res.size() - 1));
        }

        return res;
    }

    private List<ProjectHistoryTwo> generateProjectsHistory(int amount, List<ProjectTwo> projects, List<UserTwo> users) {
        List<ProjectHistoryTwo> res = new ArrayList<>();
        for (long i = 1; i <= amount; i++) {
            ProjectTwo project = projects.get((int) (i - 1) % projects.size());
            res.add(ProjectHistoryTwo.builder()
                    .id(i)
                    .name("History project_" + i)
                    .description("History desc project about application_" + i)
                    .creator(users.get((int) (i - 1) % users.size()).getId())
                    .project(project)
                    .build());

            if(project.getHistory() == null)
                project.setHistory(new HashSet<>());
            project.getHistory().add(res.get(res.size() - 1));
        }

        return res;
    }
}
