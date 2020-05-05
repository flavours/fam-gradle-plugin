package com.divio.fam.gradle;

import com.divio.fam.gradle.model.AddonConfig;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class FamPlugin implements Plugin<Project> {
    public static final Path ADDONS_DIRECTORY_PATH = Paths.get("./.flavour", "addons");

    @Override
    public void apply(Project target) {
        YamlParser<AppConfig> appConfigParser = new YamlParser<>(AppConfig.class);
        YamlParser<AddonConfig> addonConfigParser = new YamlParser<>(AddonConfig.class);
        File appFile = new File("app.flavour");

        if (appFile.exists()) {
            AppConfig appConfig = null;
            try {
                List<String> fileContents = Files.readAllLines(appFile.toPath());
                if (fileContents.stream().anyMatch(line -> !line.trim().isEmpty())) {
                    appConfig = appConfigParser.parse(fileContents);
                }
            } catch (YamlParseException | IOException e) {
                throw new GradleException("Error when parsing file 'app.flavour'.", e);
            }

            if (appConfig != null) {
                for (Map.Entry<String, AddonMeta> entry : appConfig.getAddons().entrySet()) {
                    if (entry.getValue().getManager().startsWith("flavour/fam-gradle")) {
                        target.getDependencies().add("implementation", readAddonFile(entry.getValue().getHash(), addonConfigParser));
                    }
                }
            }
        }
    }

    private String readAddonFile(final String addonHash, YamlParser<AddonConfig> addonConfigParser) {
        Path addonPath = ADDONS_DIRECTORY_PATH.resolve(addonHash);

        if (!addonPath.toFile().exists()) {
            throw new GradleException(String.format("File '%s' does not exist.", addonPath.toString()));
        }

        try {
            List<String> lines = Files.readAllLines(addonPath);
            AddonConfig addonConfig = addonConfigParser.parse(lines);
            return addonConfig.getInstall().getPackage();
        } catch (IOException e) {
            throw new GradleException(String.format("Could not read file '%s'.", addonPath.toString()), e);
        } catch (YamlParseException e) {
            throw new GradleException(String.format("Bad format of addon file '%s'.", addonPath.toString()), e);
        }
    }
}
