package freeze;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.plugin.PluginBase;

import java.util.ArrayList;
import java.util.Collection;

public class Main extends PluginBase implements Listener {

    private final Collection<String> frozenPlayers = new ArrayList<>();

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("freeze")) {
            if (0 == args.length && sender instanceof Player) {
                if (!sender.hasPermission("freeze.self")) {
                    return false;
                }

                if (frozenPlayers.contains(sender.getName())) {
                    sender.sendMessage("\u00A7cYou are already frozen");
                    return true;
                }

                ((Entity) sender).setImmobile(true);
                frozenPlayers.add(sender.getName());
                sender.sendMessage("\u00A7aDone!");
            } else if (1 == args.length) {
                if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("@a")) {
                    if (!sender.hasPermission("freeze.all")) {
                        return false;
                    }

                    for (Player p : getServer().getOnlinePlayers().values()) {
                        p.setImmobile(true);
                        frozenPlayers.add(p.getName());
                    }

                    sender.sendMessage("\u00A7aDone!");
                    return true;
                }

                if (!sender.hasPermission("freeze.other")) {
                    return false;
                }

                Player p = getServer().getPlayer(args[0]);
                if (null == p) {
                    sender.sendMessage("\u00A7cUnknown player");
                } else if (frozenPlayers.contains(p.getName())) {
                    sender.sendMessage("\u00A7cThat player is already frozen");
                } else {
                    p.setImmobile(true);
                    frozenPlayers.add(p.getName());
                    sender.sendMessage("\u00A7aDone!");
                }
            } else {
                return false;
            }
        } else if (command.getName().equalsIgnoreCase("unfreeze")) {
            if (0 == args.length && sender instanceof Player) {
                if (!sender.hasPermission("unfreeze.self")) {
                    return false;
                }

                if (!frozenPlayers.contains(sender.getName())) {
                    sender.sendMessage("\u00A7cYou are not frozen");
                    return true;
                }

                ((Entity) sender).setImmobile(false);
                frozenPlayers.remove(sender.getName());
                sender.sendMessage("\u00A7aDone!");
            } else if (1 == args.length) {
                if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("@a")) {
                    if (!sender.hasPermission("unfreeze.all")) {
                        return false;
                    }

                    for (Player p : getServer().getOnlinePlayers().values()) {
                        p.setImmobile(false);
                        frozenPlayers.remove(p.getName());
                    }

                    sender.sendMessage("\u00A7aDone!");
                    return true;
                }

                if (!sender.hasPermission("unfreeze.other")) {
                    return false;
                }

                Player p = getServer().getPlayer(args[0]);
                if (null == p) {
                    sender.sendMessage("\u00A7cUnknown player");
                } else if (!frozenPlayers.contains(p.getName())) {
                    sender.sendMessage("\u00A7cThat player is not frozen");
                } else {
                    p.setImmobile(false);
                    frozenPlayers.remove(p.getName());
                    sender.sendMessage("\u00A7aDone!");
                }
            } else {
                return false;
            }
        }

        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (frozenPlayers.contains(e.getEntity().getName())) {
                e.setCancelled(true);
                return;
            }
        }

        if (e instanceof EntityDamageByEntityEvent) {
            if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
                if (frozenPlayers.contains(((EntityDamageByEntityEvent) e).getDamager().getName())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        if (frozenPlayers.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (frozenPlayers.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
        }
    }
}
