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
import java.util.Set;

@Service
public class ExportEntities {

    public void exportAndSave() {
        UserTwo creator = UserTwo.builder()
                .id(1L)
                .nick("joedoe")
                .name("Joe")
                .lastName("Doe")
                .build();


        ProjectTwo project1 = ProjectTwo.builder()
                .id(1L)
                .name("App project1")
                .description("This is project about application!1")
                .creator(creator)
                .build();

        project1.setName("Changed app project name1");
        project1.setDescription("Changed description but should not be saved in history!1");

        ProjectHistoryTwo history1 = ProjectHistoryTwo.builder().id(1L)
                .project(project1)
                .name("History 1")
                .build();

        Set<ProjectHistoryTwo> histories = new HashSet<>();
        histories.add(history1);
        project1.setHistory(histories);

        ProjectTwo project2 = ProjectTwo.builder()
                .id(2L)
                .name("App project2")
                .description("This is project about application!2")
                .creator(creator)
                .build();

        project2.setName("Changed app project name2");
        project2.setDescription("Changed description but should not be saved in history!2");

        List<ExcelIEEntity<?>> entities = new ArrayList<>();
        entities.add(project2);
        entities.add(project1);
        entities.add(creator);
        entities.add(history1);

        Workbook export = new BaseExcelExport().export(entities, null);
        new BaseExcelExport().save(export);
    }
}
