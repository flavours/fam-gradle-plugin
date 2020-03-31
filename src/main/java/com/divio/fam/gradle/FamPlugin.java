package com.divio.fam.gradle;

import com.divio.fam.gradle.parser.YamlParseException;
import com.divio.fam.gradle.parser.YamlParser;
import com.divio.fam.gradle.model.AppConfig;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;

public class FamPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        YamlParser<AppConfig> yamlParser = new YamlParser<>(AppConfig.class);
        File file = new File("app.flavour");
        AppConfig appConfig = null;
        try {
            appConfig = yamlParser.parse(file);
        } catch (YamlParseException | IOException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }

        for (String key : appConfig.getAddons().keySet()) {
            target.getDependencies().add("implementation", key);
        }
    }
}
