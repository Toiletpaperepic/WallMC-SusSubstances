package io.github.toiletpaperepic.SusSubstances_decompiled;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Bean {
  public HashMap<UUID, Integer> BeanList = Maps.newHashMap();
  
  public Plugin plugin = (Plugin)Main.getPlugin(Main.class);
  
  public ItemStack getBean() {
    ItemStack bean = new ItemStack(Material.COCOA_BEANS);
    bean.setAmount(1);
    ItemMeta m = bean.getItemMeta();
    assert m != null;
    NamespacedKey key = new NamespacedKey(this.plugin, this.plugin.getDescription().getName());
    Glow glow = new Glow(key);
    m.addEnchant(glow, 1, true);
    m.setDisplayName(ChatColor.DARK_PURPLE + "Bean");
    bean.setItemMeta(m);
    return bean;
  }
  
  public void triggerHigh(final Player p) {
    final UUID id = p.getUniqueId();
    if (this.BeanList.get(id) == null) {
      this.BeanList.put(id, Integer.valueOf(0));
      p.sendMessage(ChatColor.GREEN + "Yummy");
      p.sendMessage("" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + "*stomach growls*");
      p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 100.0F, 1.0F);
      p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 50, true));
      p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 50, true));
      (new BukkitRunnable() {
          public void run() {
            Bean.this.BeanList.remove(id);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 100.0F, 100.0F);
            p.setGliding(false);
          }
        }).runTaskLater(this.plugin, 50L);
    } 
  }
  
  public void PlayerInteract(final Player p, ItemStack item, Bean bean) {
	  p.getWorld().spawnParticle(Particle.SLIME, new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 1.0D, p.getLocation().getZ()), 10);
      p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EAT, 100.0F, 1.0F);
      p.playSound(p.getLocation(), Sound.ENTITY_PARROT_IMITATE_CREEPER, 100.0F, 1.0F);
      if (bean.BeanList.get(p.getUniqueId()) != null) {
        bean.BeanList.replace(p.getUniqueId(), Integer.valueOf(((Integer)bean.BeanList.get(p.getUniqueId())).intValue() + 1));
        if (((Integer)bean.BeanList.get(p.getUniqueId())).intValue() == 1) {
          p.sendMessage(ChatColor.RED + "Damn. Ambitious");
        } else if (((Integer)bean.BeanList.get(p.getUniqueId())).intValue() == 2) {
          p.sendMessage(ChatColor.RED + "Jeez");
        } else if (((Integer)bean.BeanList.get(p.getUniqueId())).intValue() >= 5) {
          p.getWorld().createExplosion(p.getLocation(), 3.0F, false);
        } else {
          p.sendMessage(ChatColor.RED + "SHEEEEEEEEEEEEEEEEESH");
        } 
      } else {
          bean.triggerHigh(p);
      } 
      item.setAmount(item.getAmount() - 1);
      if (item.getAmount() < 1)
          item = null; 
      p.getInventory().setItemInMainHand(item);
  }
}
