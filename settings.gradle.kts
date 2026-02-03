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
import java.net.URI

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// TODO Set the project name to the assignment name (should also be directory name and GitHub repo
//  name), written in lower-spinal-case.
rootProject.name = "practical-assignment-template"

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/ddc-java/${rootProject.name}-solution")
            credentials {
                username = System.getenv("PACKAGE_CONSUMER_USER")
                password = System.getenv("PACKAGE_CONSUMER_TOKEN")
            }
        }
    }
}


// TODO REMOVE THIS BLOCK when copying settings to a project; in this template, use it to check for
//  new library versions.
plugins {
    id("de.fayard.refreshVersions") version "0.60.5"
////                            # available:"0.60.6"
}