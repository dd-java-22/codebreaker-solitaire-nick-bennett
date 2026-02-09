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

val openApiGenerator = "java"
val openApiOutDir = layout.buildDirectory.dir("generated/openapi").get().asFile.toString()
val openApiSpec = "$projectDir/api/codebreaker.yaml"
val apiPkg = "${properties.get("basePackage")}.service"
val modelPkg = "${properties.get("basePackage")}.model"
val openApiCommonOptions = mapOf(
    "library" to "retrofit2",
    "serializationLibrary" to "gson",
    "dateLibrary" to "java8",
    "useJakartaEe" to "true",
    "openApiNullable" to "false",
    "useBeanValidation" to "false"
)
val openApiGlobalProperties = mapOf(
    "models" to "false",
    "apis" to "false",
    "supportingFiles" to "false",
    "apiDocs" to "false",
    "apiTests" to "false",
    "modelDocs" to "false",
    "modelTests" to "false"
)

tasks.register<GenerateTask>("openApiGenerateModels") {

    generatorName = openApiGenerator
    inputSpec = openApiSpec
    outputDir = openApiOutDir
    apiPackage = apiPkg
    modelPackage = modelPkg
    ignoreFileOverride = "$projectDir/api/openapi-generator-ignore"

    configOptions = openApiCommonOptions + mapOf(
        "generateBuilders" to "true",
        "useRecords" to "true"
    )

    globalProperties = openApiGlobalProperties + mapOf(
        "models" to "",
        "modelDocs" to "true"
    )
}

tasks.register<GenerateTask>("openApiGenerateApis") {

    generatorName = openApiGenerator
    inputSpec = openApiSpec
    outputDir = openApiOutDir
    apiPackage = apiPkg
    modelPackage = modelPkg
    ignoreFileOverride = "$projectDir/api/openapi-generator-ignore"

    configOptions = openApiCommonOptions + mapOf(
        "useTags" to "true"
    )

    globalProperties = openApiGlobalProperties + mapOf(
        "apis" to "",
        "apiDocs" to "true",
        "supportingFiles" to "CollectionFormats.java,StringUtil.java"
    )
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
