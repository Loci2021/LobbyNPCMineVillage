package de.loci.lobbynpcs.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemBuilder {

    private final ItemMeta itemMeta;
    private final ItemStack itemStack;

    public ItemBuilder(Material mat) {
        itemStack = new ItemStack(mat);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setAmount(int amount){
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setDisplayname(String s) {
        itemMeta.setDisplayName(s);
        return this;
    }


    public ItemBuilder setLore(String... s) {
        itemMeta.setLore(Arrays.asList(s));
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... s) {
        itemMeta.addItemFlags(s);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment e,int level){
        itemMeta.addEnchant(e,level,true);
        return this;
    }


    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}