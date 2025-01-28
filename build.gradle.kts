plugins {
    kotlin("jvm")
    alias(libs.plugins.springManagement)
    alias(libs.plugins.springBoot) apply(false)
}

allprojects {
    apply {
        plugin("org.springframework.boot")
        plugin("kotlin")
    }

    dependencies {
        implementation(platform(rootProject.libs.springBootBom))
        implementation(platform(rootProject.libs.springCloudStreamBom))
    }
}
