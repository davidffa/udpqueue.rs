plugins {
  `java-library`
  `maven-publish`
}

dependencies {
  api(project(":api"))
}

val processResources: Copy by tasks
val target = project.properties["target"]?.toString() ?: ""
val platform = ext["platform"] as String
val artifactName = "udpqueue-native-$platform"

tasks.withType<Jar> {
  archiveBaseName.set(artifactName)
}

tasks.create<Copy>("moveResources") {
  group = "build"

  from("target/$target/release/")

  include {
    it.name.endsWith(".so") || it.name.endsWith(".dll") || it.name.endsWith(".dylib")
  }

  into("src/main/resources/natives/$platform")

  processResources.dependsOn(this)
}

tasks.create<Delete>("cleanNatives") {
  group = "build"
  delete(fileTree("src/main/resources/natives"))
  tasks["clean"].dependsOn(this)
}

processResources.include {
  it.isDirectory || it.file.parentFile.name == platform
}


publishing.publications {
  create<MavenPublication>("Maven") {
    from(components["java"])

    artifactId = artifactName
  }
}