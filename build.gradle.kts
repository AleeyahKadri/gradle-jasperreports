plugins {
  id("com.gradle.plugin-publish") version "0.10.1"
  id("net.researchgate.release") version "2.6.0"
	id("groovy")
	id("maven-publish")
	id("java-gradle-plugin")
}

// Unless overridden in the pluginBundle config DSL, the project version will
// be used as your plugin version when publishing
group = "com.github.gmazelier"
version = project.property("version")

repositories {
	mavenCentral()
	jcenter()
	maven {
		url = uri("http://jaspersoft.artifactoryonline.com/jaspersoft/third-party-ce-artifacts/")
		isAllowInsecureProtocol = true
	}
}

dependencies {
	implementation(gradleApi())
	implementation(localGroovy())
	implementation("net.sf.jasperreports:jasperreports:6.10.0")
	implementation("org.codehaus.gpars:gpars:1.2.1")
	testImplementation("junit:junit:4.12")
}

// Use java-gradle-plugin to generate plugin descriptors and specify plugin ids
gradlePlugin {
	plugins {
		create("jasperreports") {
			id = "com.github.gmazelier.jasperreports"
			implementationClass = "com.github.gmazelier.plugins.JasperReportsPlugin"
		}
	}
}

configure<com.gradle.publish.PluginBundleExtension> {
	// These settings are set for the whole plugin bundle
	website = "https://github.com/gmazelier/gradle-jasperreports"
	vcsUrl = "https://github.com/gmazelier/gradle-jasperreports.git"
	description = "Provides the capability to compile JasperReports design files."
	tags = listOf("gradle", "jasperreports")

	plugins {
		named("jasperreports") {
			// id is captured from java-gradle-plugin configuration
			displayName = "Gradle JasperReports Plugin"
		}
	}
}

publishing {
	// uncomment the following section to publish plugin to local maven repository using the following task:
	// $ gradle publishToMavenLocal
	/*
	repositories {
		mavenLocal()
	}
	*/
	publications {
		create<MavenPublication>("mavenJava") {
			afterEvaluate {
				artifact(tasks.named("sourcesJar"))
				artifact(tasks.named("groovydocJar"))
			}
		}
	}
}

tasks.named<GroovyCompile>("compileGroovy") {
	sourceCompatibility = "1.8"
	targetCompatibility = "1.8"
}

val sourcesJar by tasks.registering(Jar::class) {
	archiveClassifier.set("sources")
	from(sourceSets["main"].allSource)
}

val groovydocJar by tasks.registering(Jar::class) {
	archiveClassifier.set("groovydoc")
	from(tasks.named("groovydoc"))
}

artifacts {
	add("archives", sourcesJar)
	add("archives", groovydocJar)
}
