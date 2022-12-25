// we need to add paper's repositories / gradle plugins portal
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "LoadBalanacer-Parent"
include("LoadBalancer-API")
include("LoadBalancer-APIServer")
include("LoadBalancer-Common")
include("LoadBalancer-APIServer-Bukkit")
include("LoadBalancer-API-Velocity")
