/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    java
    jacoco
    alias(libs.plugins.openapi)
}

val javaVersion = libs.versions.java.get()

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir("generated/openapi/src/main/java"))
        }
    }
}

dependencies {

    implementation(libs.gson)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.jakarta.annotation)
    implementation(libs.jakarta.validation)

    testImplementation(libs.junit.aggregator)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform)
}

val openApiCommonOptions = mapOf(
    "dateLibrary" to "java8",
    "hideGenerationTimestamp" to "true",
    "library" to "retrofit2",
    "openApiNullable" to "false",
    "serializationLibrary" to "gson",
    "skipIfSpecIsUnchanged" to "true",
    "useBeanValidation" to "false",
    "useJakartaEe" to "true"
)
val openApiGlobalProperties = mapOf(
    "apiDocs" to "false",
    "apiTests" to "false",
    "apis" to "false",
    "modelDocs" to "false",
    "modelTests" to "false",
    "models" to "false",
    "supportingFiles" to "false",
)

tasks.register<GenerateTask>("openApiGenerateModels") {

    configOptions = openApiCommonOptions

    globalProperties = openApiGlobalProperties + mapOf(
        "models" to "",
        "modelDocs" to "true"
    )
}

tasks.register<GenerateTask>("openApiGenerateApis") {

    configOptions = openApiCommonOptions + mapOf(
        "useTags" to "true"
    )

    globalProperties = openApiGlobalProperties + mapOf(
        "apis" to "",
        "apiDocs" to "true",
        "supportingFiles" to "CollectionFormats.java,StringUtil.java"
    )
}

tasks.withType<GenerateTask> {

    group = "openapi tools"

    generatorName = "java"

    inputSpec = "$projectDir/api/codebreaker.yaml"
    outputDir = layout.buildDirectory.dir("generated/openapi").get().asFile.toString()

    apiPackage = "${properties.get("basePackage")}.service"
    modelPackage = "${properties.get("basePackage")}.model"

    outputs.dir(outputDir)
    inputs.file(inputSpec)

    ignoreFileOverride = "$projectDir/api/.openapi-generator-ignore"

}

tasks.openApiGenerate {
    enabled = false
}

tasks.withType<JavaCompile> {
    dependsOn(tasks.named("openApiGenerateModels"))
    dependsOn(tasks.named("openApiGenerateApis"))
    options.release = javaVersion.toInt()
}

tasks.javadoc {
    with(options as StandardJavadocDocletOptions) {
        links("https://docs.oracle.com/en/java/javase/${javaVersion}/docs/api/")
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events.addAll(setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED))
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}
