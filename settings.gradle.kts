// we need to add paper's repositories / gradle plugins portal
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "LoadBalanacer-Parent"
include("LoadBalancer-ProxyAPI")
include("examples:LoadBalancer-ProxyAPI-Velocity")
include("LoadBalancer-ServerAPI")
include("LoadBalancer-Common")
include("LoadBalancer-ServerAPI-Bukkit")
include("datasources:LoadBalancer-ProxyAPI-JedisDataSource")
include("datasources:JedisConfig")
