# LoadBalancer

This is an API that help you with load balancing or even matchmaking.

# Todo
* Finish Java docs for ServerAPI
* RedisBungee data source
* Provide examples on how to implement WITHOUT including in the source code.

# ProxyAPI

* todo

# ServerAPI

* todo

## Supported databases

* Redis "Jedis lib"
* Todo: RedisBungee "Jedis lib Relocated"
* Any database you can use, But you will have to implement it.


## config for Bukkit plugin
* [config.yml](https://github.com/GloMC/LoadBalancer/blob/main/LoadBalancer-ServerAPI/src/main/resources/config.yml)
* [redis.yml](https://github.com/GloMC/LoadBalancer/blob/main/LoadBalancer-Common/src/main/resources/redis.yml) same file can be seen on the proxy if you implement interface [JedisConfigLoader](https://github.com/GloMC/LoadBalancer/blob/main/datasources/JedisConfig/src/main/java/net/glomc/apis/loadbalancer/config/jedis/JedisConfigLoader.java)

