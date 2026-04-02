import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.javadoc.Groovydoc
import org.gradle.api.tasks.compile.GroovyCompile
import com.gradle.publish.PluginBundleExtension

plugins {
    id("com.gradle.plugin-publish") version "0.10.1"
    id("net.researchgate.release") version "2.6.0"
    groovy
    `maven-publish`
    `java-gradle-plugin`
}

group = "com.github.gmazelier"
version = project.property("version") as String
val jasperreportsPluginName = "jasperreports"

repositories {
    jcenter()
    mavenCentral()
    maven {
        url = uri("http://jaspersoft.artifactoryonline.com/jaspersoft/third-party-ce-artifacts/")
    }
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("net.sf.jasperreports:jasperreports:6.10.0")
    implementation("org.codehaus.gpars:gpars:1.2.1")
    testImplementation("junit:junit:4.12")
}

gradlePlugin {
    plugins {
        create(jasperreportsPluginName) {
            id = "com.github.gmazelier.jasperreports"
            implementationClass = "com.github.gmazelier.plugins.JasperReportsPlugin"
        }
    }
}

configure<PluginBundleExtension> {
    website = "https://github.com/gmazelier/gradle-jasperreports"
    vcsUrl = "https://github.com/gmazelier/gradle-jasperreports.git"
    description = "Provides the capability to compile JasperReports design files."
    tags = listOf("gradle", "jasperreports")

    plugins {
        named(jasperreportsPluginName) {
            displayName = "Gradle JasperReports Plugin"
        }
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val groovydocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.named("groovydoc"))
    archiveClassifier.set("groovydoc")
    from(tasks.named<Groovydoc>("groovydoc").map { it.destinationDir })
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            afterEvaluate {
                artifact(sourcesJar.get())
                artifact(groovydocJar.get())
            }
        }
    }
}

tasks.named<GroovyCompile>("compileGroovy") {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

artifacts {
    add("archives", sourcesJar)
    add("archives", groovydocJar)
}
