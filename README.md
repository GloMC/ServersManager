# LoadBalancer

## Bukkit plugin
Bukkit plugin [LoadBalancer-API-Server-Bukkit](https://github.com/GloMC/LoadBalancer/tree/main/LoadBalancer-API-Server-Bukkit)
is implementation of LoadBalancer-API-Server which does the heartbeating and data publishing
you could create Implemention on any platform Fabric, forge, sponge etc. fell free to pr it :) 

## Supported databases

* Redis

at the momment redis is only supported database but the api got you covered as you can support any database of your choice but you will need to implement it,
feel free to pr it to the project :)

## config for Bukkit plugin
* [config.yml](https://github.com/GloMC/LoadBalancer/blob/main/LoadBalancer-API-Server/src/main/resources/config.yml)
* [redis.yml](https://github.com/GloMC/LoadBalancer/blob/main/LoadBalancer-Common/src/main/resources/redis.yml) same file can be seen on the proxy if you implement interface [RedisConfigLoader](https://github.com/GloMC/LoadBalancer/blob/main/LoadBalancer-Common/src/main/java/net/glomc/apis/loadbalancer/common/config/RedisConfigLoader.java)

Bukkit plugin has System propertys for setting the Ip address, group id, server id
`-DserverHost=192.168.0.151`
`-DgroupId=minigame`
`-DserverId=server-1` note: this option is ignored when MultiPaper is detected.
