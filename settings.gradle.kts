// we need to add paper's repositories / gradle plugins portal
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "LoadBalanacer-Parent"
include("LoadBalancer-API")
include("LoadBalancer-API-Server")
include("LoadBalancer-Common")
include("LoadBalancer-API-Server-Bukkit")
