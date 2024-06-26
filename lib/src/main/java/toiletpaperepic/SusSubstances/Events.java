package toiletpaperepic.SusSubstances;

import toiletpaperepic.SusSubstances.Main.ItemValues;

import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Events implements Listener, CommandExecutor {
    public Events(Main main) {
        Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, main);
        Objects.requireNonNull(Bukkit.getPluginCommand("sapi")).setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("sapi"))
            //console has permission too.
            if (sender.hasPermission("substances.perms")) {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "Invalid Syntax!" + ChatColor.WHITE + " try /sapi help");
                } else {
                    if (args[0].equals("help")) {
                        sender.sendMessage(ChatColor.AQUA + "Options:");
                        sender.sendMessage("");
                        sender.sendMessage(ChatColor.GRAY + "> " + ChatColor.WHITE + "/sapi reload");
                        sender.sendMessage(ChatColor.GRAY + "> " + ChatColor.WHITE + "/sapi help");

                        return true;
                    }
                    if (args[0].equals("reload")) {
                        Main.getInstance().reloadConfig();
                        Main.registerConfig();
                        sender.sendMessage("Reloaded!");
                        return true;
                    }
                }
            }
        return true;
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onVelEvent(PlayerVelocityEvent event) {
        Player p = event.getPlayer();

        if (Main.bean.BeanList.containsKey(p.getUniqueId()) || Main.sugar.sugarFly.contains(p.getUniqueId())) {
            if (!p.isOnGround() | (p.getFallDistance() >= 5.0F)) {
                p.setGliding(true);
            } else {
                //this does not mater if player used sugar
                //only for bean
                //jumps
                p.setVelocity(new Vector(0, 1, 0));
            }

            p.getWorld().spawnParticle(
                Particle.FLAME, new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()),10);

            p.getWorld().spawnParticle(
                Particle.CAMPFIRE_COSY_SMOKE, new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()), 10);
        }
    }

    //Leaving these two separate
    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (Main.bean.BeanList.containsKey(p.getUniqueId())) {
            double vec;
            if (Main.bean.BeanList.get(p.getUniqueId()).intValue() == 0) {
                vec = 0.1D;
            } else {
                vec = Main.bean.BeanList.get(p.getUniqueId()) * 0.3D;
            }
            if (!p.isOnGround()) {
                if (!p.isGliding())
                    p.setGliding(true);
                Vector v = p.getVelocity().clone();
                Vector d = p.getLocation().getDirection().clone().multiply(0.1D + vec);
                Vector hor = new Vector(d.getX(), 0.0D, d.getZ());
                Vector horV = new Vector(v.getX(), 0.0D, v.getZ());
                Vector vert = new Vector(0.0D, d.getY(), 0.0D);
                Vector use = hor.add(vert);
                if (hor.clone().add(horV).lengthSquared() >= 1.0D)
                    use = vert;
                p.setVelocity(v.add(use));
            } else {
                p.setVelocity(new Vector(0.0D, 0.1D, 0.0D));
            }
        }
        if (Main.sugar.sugarFly.contains(p.getUniqueId())) {
            if (!p.isOnGround() | (p.getFallDistance() >= 5.0F)) {
                double num;
                p.setGliding(true);
                if (Main.sugar.sugarList.containsKey(p.getUniqueId()) && (Main.sugar.sugarList.get(p.getUniqueId())).intValue() >= 1) {
                    num = Main.sugar.sugarList.get(p.getUniqueId()) * 0.2D;
                } else {
                    num = 0.2D;
                }

                p.setVelocity(new Vector(0.0D, num, 0.0D));
            } else {
                p.setVelocity(new Vector(0, 1, 0));
            }
        }
    }

    @EventHandler
    public void onToggleElytra(PlayerToggleFlightEvent e) {
        if (Main.bean.BeanList.containsKey(e.getPlayer().getUniqueId()) || Main.sugar.sugarFly.contains(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        handleleave(e, Main.lettuce.LettuceList, null);
        handleleave(e, Main.sugar.sugarList, Main.sugar.sugarFly);
        handleleave(e, Main.bean.BeanList, null);
        handleleave(e, Main.crystal.CrystalList, null);
        //add anything else here if you want to
    }

    private void handleleave(PlayerQuitEvent quit, HashMap<UUID, Integer> list, ArrayList<UUID> flylist) {
        Player player = quit.getPlayer();
        if (list.containsKey(player.getUniqueId())) {
            list.remove(player.getUniqueId());

            if (!(flylist == null))
                list.remove(player.getUniqueId());
            for (PotionEffect effect : player.getActivePotionEffects())
                player.removePotionEffect(effect.getType());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        handledeath(e, Main.lettuce.LettuceList, "died by a exploding battery", null);
        handledeath(e, Main.sugar.sugarList, "died by a exploding battery", Main.sugar.sugarFly);
        handledeath(e, Main.bean.BeanList, "died by a exploding battery", null);
        handledeath(e, Main.crystal.CrystalList, "died by a exploding battery", null);
        //add anything else here if you want to
    }

    private void handledeath(PlayerDeathEvent death, HashMap<UUID, Integer> list, String message, ArrayList<UUID> flylist) {
        Player deadplayer = death.getEntity();
        if (list.containsKey(deadplayer.getUniqueId())) {
            if ((list.get(deadplayer.getUniqueId())).intValue() >= 5)
                death.setDeathMessage(deadplayer.getName() + " " + message);
            list.remove(deadplayer.getUniqueId());

            if (!(flylist == null))
                flylist.remove(deadplayer.getUniqueId());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        handlebreak(e, Main.LettuceItemValues, Main.lettuce, Material.FERN);
        handlebreak(e, Main.SugarItemValues, Main.sugar, Material.SUGAR_CANE);
        handlebreak(e, Main.BeanItemValues, Main.bean, Material.COCOA);
        handlebreak(e, Main.CrystalItemValues, Main.crystal, Material.AMETHYST_BLOCK);
        handlebreak(e, Main.SpecialSauceItemValues, Main.sauce, Material.OAK_WOOD);
        //add anything else here if you want to
    }

    private void handlebreak(BlockBreakEvent event, ItemValues itemvalues, Item item, Material block) {
        if (itemvalues.getStatus() == true && event.getBlock().getType() == block && Math.random() < itemvalues.getRate()) {
            Location loc = event.getBlock().getLocation();
            World world = loc.getWorld();
            assert world != null;
            world.dropItem(loc, item.getitem());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        handleplayerinteract(e, Main.lettuce);
        handleplayerinteract(e, Main.sugar);
        handleplayerinteract(e, Main.bean);
        handleplayerinteract(e, Main.crystal);
        handleplayerinteract(e, Main.sauce);
        //add anything else here if you want to
    }

    private void handleplayerinteract(PlayerInteractEvent event, Item item) {
        Player p = event.getPlayer();
        ItemStack itemstack = p.getInventory().getItemInMainHand();

        // Main.log.info("" + itemstack.getType().equals(item.getmaterial()));

        if (itemstack.getType() != Material.AIR && (event.getAction().equals(Action.RIGHT_CLICK_AIR) | event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            if (itemstack.getType().equals(item.getmaterial()) &&
                Objects.requireNonNull(itemstack.getItemMeta())
                    .getDisplayName()
                    .equals(ChatColor.DARK_PURPLE + item.getname())) {
                item.PlayerInteract(p, itemstack);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        handleplace(e, Main.lettuce);
        handleplace(e, Main.sugar);
        handleplace(e, Main.bean);
        handleplace(e, Main.crystal);
        handleplace(e, Main.sauce);
    }

    private void handleplace(BlockPlaceEvent e, Item item) {
        Player p = e.getPlayer();
        if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
            ItemStack itemstack = p.getInventory().getItemInMainHand();
            if (itemstack.getType().equals(item.getmaterial()) &&
                Objects.requireNonNull(itemstack.getItemMeta())
                    .getDisplayName()
                    .equals(ChatColor.DARK_PURPLE + item.getname())) {
                e.setCancelled(true);
                e.getBlock()
                    .getWorld()
                    .getBlockAt(e.getBlock().getLocation())
                    .setType(Material.AIR);
            }
        }
    }
}