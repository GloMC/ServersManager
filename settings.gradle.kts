rootProject.name = "LoadBalanacer-Parent"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}


include("common")
include("ProxyAPI")
include("ServerAPI")


// redis data source
include("datasources:ProxyAPI-JedisDataSource")
include("datasources:JedisConfig")
include("datasources:ServerAPI-JedisDataSource")
findProject(":datasources:ServerAPI-JedisDataSource")?.name = "ServerAPI-JedisDataSource"
