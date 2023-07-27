package mine.plugins.lunar.plugin_framework.item;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemBuilder {
    private final ItemStack item;

    @Getter private String name = "";
    @Getter private boolean isUnbreakable = false;

    @Getter private ItemFlag[] flags = new ItemFlag[0];
    @Getter private String[] lore = new String[0];
    @Getter private Map<Enchantment, Integer> enchantments = new HashMap<>();

    public ItemBuilder(ItemStack item) {
        this.item = item;

        var itemMeta = item.getItemMeta();
        if (itemMeta == null) return;

        name = itemMeta.getDisplayName();
        isUnbreakable = itemMeta.isUnbreakable();

        var itemLore = itemMeta.getLore();
        lore = itemLore == null ? new String[0] : itemLore.toArray(new String[0]);
    }

    /**
     * Creates a new ItemBuilder with the specified material.
     *
     * @param material The material
     */
    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }

    /**
     * Sets the amount of the item.
     *
     * @param amount Item amount
     * @return This ItemBuilder
     */
    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * Sets the name of the item.
     *
     * @param name Item name
     * @return This ItemBuilder
     */
    public ItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Adds the specified lore line to the
     * item. If there are newline characters
     * in the string, multiple lines are added.
     *
     * @param lore Line to add to the lore.
     * @return This ItemBuilder
     */
    public ItemBuilder setLore(String... lore) {
        this.lore = lore;
        return this;
    }

    /**
     * Hides this item's attributes, like damage
     * stats on swords.
     *
     * @return This ItemBuilder
     */
    public ItemBuilder setFlags(ItemFlag... flags) {
        this.flags = flags;
        return this;
    }

    public ItemBuilder setUnbreakable() {
        this.isUnbreakable = true;
        return this;
    }

    public ItemBuilder setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    /**
     * Returns an ItemStack with the settings
     * specified in this ItemBuilder.
     * <p>
     * This returns a clone, so modifying
     * the ItemStack further afterward does
     * not affect this ItemBuilder.
     *
     * @return A clone of the ItemStack
     */
    public ItemStack get() {
        var item = this.item.clone();

        modify(item);
        return item;
    }

    public void modify() {
        modify(item);
    }

    public void modify(ItemStack modified) {
        var meta = modified.getItemMeta();
        if (meta == null) return;

        meta.setDisplayName(name);
        meta.setLore(Arrays.stream(lore).map(loreLine -> ChatColor.WHITE+loreLine).toList());

        meta.addItemFlags(flags);
        meta.setUnbreakable(isUnbreakable);

        for (var enchantment : enchantments.entrySet())
            meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);

        modified.setItemMeta(meta);
    }

}
