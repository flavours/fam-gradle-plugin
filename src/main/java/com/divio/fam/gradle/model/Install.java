package com.divio.fam.gradle.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class Install {
    @JsonProperty("package")
    @NotBlank
    private String packageValue;

    Install() {
    }

    public Install(final String packageValue) {
        this.packageValue = packageValue;
    }

    public String getPackage() {
        return this.packageValue;
    }
}
