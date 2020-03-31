package com.divio.fam.gradle;

import com.divio.fam.gradle.model.AddonMeta;
import com.divio.fam.gradle.model.AppConfig;
import com.divio.fam.gradle.parser.YamlParseException;
import com.divio.fam.gradle.parser.YamlParser;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class FamPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        YamlParser<AppConfig> yamlParser = new YamlParser<>(AppConfig.class);
        File file = new File("app.flavour");

        if (file.exists()) {
            AppConfig appConfig = null;
            try {
                List<String> fileContents = Files.readAllLines(file.toPath());
                if (fileContents.stream().anyMatch(line -> !line.trim().isEmpty())) {
                    appConfig = yamlParser.parse(fileContents);
                }
            } catch (YamlParseException | IOException e) {
                throw new GradleException("Error when parsing file 'app.flavour'.", e);
            }

            if (appConfig != null) {
                for (Map.Entry<String, AddonMeta> entry : appConfig.getAddons().entrySet()) {
                    if (entry.getValue().getManager().startsWith("flavour/fam-gradle")) {
                        target.getDependencies().add("implementation", entry.getKey());
                    }
                }
            }
        }
    }
}
