plugins {
    id("org.springframework.boot") version PluginVersions.SPRING_BOOT_VERSION
    id("io.spring.dependency-management") version PluginVersions.DEPENDENCY_MANAGER_VERSION
    kotlin("plugin.spring") version PluginVersions.SPRING_PLUGIN_VERSION
    kotlin("plugin.jpa") version PluginVersions.JPA_PLUGIN_VERSION
}

dependencyManagement {
    imports {
        mavenBom(Dependencies.SPRING_CLOUD)
    }
}

dependencies {
    implementation(Dependencies.REACTIVE_HIBERNATE)
    implementation(Dependencies.REACTIVE_JDSL)
    implementation(Dependencies.SPRING_DATA_COMMON)
    implementation(Dependencies.REACTIVE_MYSQL)
    implementation(Dependencies.MUTINY_KOTLIN)
    implementation(Dependencies.MUTINY_REACTOR)
    implementation(Dependencies.COROUTINE_REACTOR)
    implementation(Dependencies.REACTOR_COROUTINE_EXTENSION)
    implementation(Dependencies.WEBFLUX)
    implementation(Dependencies.VALIDATION)
    implementation(Dependencies.JACKSON)
    implementation(Dependencies.SPRING_SECURITY)
    implementation(Dependencies.CLOUD_CONFIG)
    implementation(Dependencies.UUID_V7)
    implementation(Dependencies.REACTIVE_REDIS)
    implementation(Dependencies.APACHE_POI)
    implementation(Dependencies.APACHE_POI_OOXML)
    implementation(Dependencies.AWS_MESSAGING)

    implementation(project(":point-domain"))
}

kapt {
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
        arg("mapstruct.unmappedTargetPolicy", "ignore")
    }
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

noArg {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
