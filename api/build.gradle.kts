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

val javaVersion: String = libs.versions.java.get()

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
    implementation(libs.kotlin)
    implementation(libs.gson)
    implementation(libs.retrofit.core)
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

    inputSpec = "$projectDir/spec/codebreaker.yaml"
    outputDir = layout.buildDirectory.dir("generated/openapi").get().asFile.toString()

    val basePackage = properties.get("basePackage")
    apiPackage = "${basePackage}.service"
    modelPackage = "${basePackage}.model"

    outputs.dir(outputDir)
    inputs.file(inputSpec)

    ignoreFileOverride = "$projectDir/spec/.openapi-generator-ignore"

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
        links("https://square.github.io/retrofit/3.x/retrofit/")
        links("https://www.javadoc.io/doc/com.google.code.gson/gson/latest/")
        links("https://jakarta.ee/specifications/annotations/3.0/apidocs/")
        links("https://jakarta.ee/specifications/bean-validation/3.1/apidocs/")
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
