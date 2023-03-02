package net.glomc.apis.loadbalancer.impl.bukkit;

import com.github.puregero.multilib.MultiLib;
import net.glomc.apis.loadbalancer.common.enums.DataFieldId;
import net.glomc.apis.loadbalancer.serverapi.CollectorManager;
import net.glomc.apis.loadbalancer.serverapi.collectors.DataCollector;
import org.bukkit.Bukkit;

public class BukkitCollectors {

    public static class CurrentOnlineCollector extends DataCollector  {
        public static DataFieldId FIELD_ID = DataFieldId.ONLINE;

        private final ServerAPIPlugin plugin;
        public CurrentOnlineCollector(ServerAPIPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public String collect() {
            if (plugin.isMultiPaper()) return String.valueOf(MultiLib.getLocalOnlinePlayers().size());
            return String.valueOf(Bukkit.getOnlinePlayers().size());
        }
    }


    public static class MaxOnlineCollector extends DataCollector  {
        public static DataFieldId FIELD_ID = DataFieldId.MAX_ONLINE;

        @Override
        public String collect() {
            return String.valueOf(Bukkit.getMaxPlayers());
        }
    }

    public static void registerAll(CollectorManager collectorManager, ServerAPIPlugin serverAPIPlugin) {
        collectorManager.register(MaxOnlineCollector.FIELD_ID.getFieldId(), new MaxOnlineCollector());
        collectorManager.register(CurrentOnlineCollector.FIELD_ID.getFieldId(), new CurrentOnlineCollector(serverAPIPlugin));
    }

}
