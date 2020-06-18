package no.helponline.Utils;

import no.helponline.OddJob;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Utility {

    public static void doorToggle(Block block) {
        Door door = (Door) block.getBlockData();
        boolean open = door.isOpen();
        Bisected.Half doorHalf = door.getHalf();
        Door.Hinge doorHinge = door.getHinge();

        //OddJob.getInstance().log("Open:" + (open ? "true" : "false") + "; Face:" + doorFace + "; Hinge:" + doorHinge + "; Half:" + doorHalf);

        boolean left = (doorHinge == Door.Hinge.LEFT);
        boolean lower = (doorHalf == Bisected.Half.BOTTOM);
        Block doorUpperRight = null;
        Block doorLowerRight = null;
        Block doorLowerLeft = null;
        Block doorUpperLeft = null;

        List<Block> doors = new ArrayList<>();

        if (!lower) {
            if (!left) {
                //UPPER RIGHT
                doorUpperRight = block;
                doorLowerRight = block.getRelative(BlockFace.DOWN);
                Block test = getLowerLeftDoor(block).getBlock();
                if (((Door) test.getBlockData()).getHinge().equals(Door.Hinge.LEFT)) {
                    doorLowerLeft = test;
                    doorUpperLeft = doorLowerLeft.getRelative(BlockFace.UP);
                }

            } else {
                //UPPER LEFT
                doorUpperLeft = block;
                doorLowerLeft = block.getRelative(BlockFace.DOWN);
                Block test = getLowerRightDoor(block).getBlock();
                if (((Door) test.getBlockData()).getHinge().equals(Door.Hinge.RIGHT)) {
                    doorLowerRight = test;
                    doorUpperRight = doorLowerRight.getRelative(BlockFace.UP);
                }
            }
        } else {
            if (!left) {
                //LOWER RIGHT
                doorUpperRight = block.getRelative(BlockFace.UP);
                doorLowerRight = block;
                Block test = getLowerLeftDoor(block).getBlock();
                if (((Door) test.getBlockData()).getHinge().equals(Door.Hinge.LEFT)) {
                    doorLowerLeft = test;
                    doorUpperLeft = doorLowerLeft.getRelative(BlockFace.UP);
                }
            } else {
                //LOWER LEFT
                doorUpperLeft = block.getRelative(BlockFace.UP);
                doorLowerLeft = block;
                Block test = getLowerRightDoor(block).getBlock();
                if (((Door) test.getBlockData()).getHinge().equals(Door.Hinge.RIGHT)) {
                    doorLowerRight = test;
                    doorUpperRight = doorLowerRight.getRelative(BlockFace.UP);
                }
            }
        }
        if (doorLowerLeft != null && OddJob.getInstance().getLockManager().getDoors().contains(doorLowerLeft.getType()))
            doors.add(doorLowerLeft);
        if (doorLowerRight != null && OddJob.getInstance().getLockManager().getDoors().contains(doorLowerRight.getType()))
            doors.add(doorLowerRight);
        if (doorUpperLeft != null && OddJob.getInstance().getLockManager().getDoors().contains(doorUpperLeft.getType()))
            doors.add(doorUpperLeft);
        if (doorUpperRight != null && OddJob.getInstance().getLockManager().getDoors().contains(doorUpperRight.getType()))
            doors.add(doorUpperRight);

        for (Block bl : doors) {
            Openable o = (Openable) bl.getState().getBlockData();
            o.setOpen(!open);
            bl.setBlockData(o);
            bl.getState().update(true, true);
        }
    }

    public static Location getLowerRightDoor(Block block) {
        Door door = (Door) block.getBlockData();
        BlockFace doorFace = door.getFacing();
        Bisected.Half doorHalf = door.getHalf();
        Door.Hinge doorHinge = door.getHinge();

        //OddJob.getInstance().log("Open:" + (open ? "true" : "false") + "; Face:" + doorFace + "; Hinge:" + doorHinge + "; Half:" + doorHalf);

        boolean right = (doorHinge == Door.Hinge.RIGHT);
        boolean lower = (doorHalf == Bisected.Half.BOTTOM);

        if (!lower) {
            block = block.getRelative(BlockFace.DOWN);
            //OddJob.getInstance().log("is upper, moving down");
        }
        if (!right) {
            //OddJob.getInstance().log("is left");
            Block test = switch (doorFace) {
                case NORTH -> block.getRelative(BlockFace.EAST);
                case WEST -> block.getRelative(BlockFace.NORTH);
                case SOUTH -> block.getRelative(BlockFace.WEST);
                case EAST -> block.getRelative(BlockFace.SOUTH);
                default -> null;
            };
            if (test != null && OddJob.getInstance().getLockManager().getDoors().contains(test.getType()) && !((Door) test.getBlockData()).getHinge().equals(Door.Hinge.LEFT)) {
                //OddJob.getInstance().log("has right");
                block = test;
            }
        } else {
            OddJob.getInstance().log("is right");
        }
        //OddJob.getInstance().log("Lower right : " + block.getLocation().serialize().toString());
        return block.getLocation();
    }

    public static Location getLowerLeftDoor(Block block) {
        Door door = (Door) block.getBlockData();
        BlockFace doorFace = door.getFacing();
        Bisected.Half doorHalf = door.getHalf();
        Door.Hinge doorHinge = door.getHinge();

        //OddJob.getInstance().log("Open:" + (open ? "true" : "false") + "; Face:" + doorFace + "; Hinge:" + doorHinge + "; Half:" + doorHalf);

        boolean left = (doorHinge == Door.Hinge.LEFT);
        boolean lower = (doorHalf == Bisected.Half.BOTTOM);

        if (!lower) {
            block = block.getRelative(BlockFace.DOWN);
            //OddJob.getInstance().log("is upper, moving down");
        }
        if (!left) {
            //OddJob.getInstance().log("is right");
            Block test = switch (doorFace) {
                case NORTH -> block.getRelative(BlockFace.WEST);
                case WEST -> block.getRelative(BlockFace.SOUTH);
                case SOUTH -> block.getRelative(BlockFace.EAST);
                case EAST -> block.getRelative(BlockFace.NORTH);
                default -> null;
            };
            if (test != null && OddJob.getInstance().getLockManager().getDoors().contains(test.getType()) && !((Door) test.getBlockData()).getHinge().equals(Door.Hinge.RIGHT)) {
                //OddJob.getInstance().log("has left");
                block = test;
            }
        } else {
            //OddJob.getInstance().log("is left");
        }

        //OddJob.getInstance().log("Lower left : " + block.getLocation().serialize().toString());
        return block.getLocation();
    }

    public static Location getChestPosition(Block block) {
        Chest chest = (Chest) block.getState();

        if (chest.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest left = (DoubleChest) chest.getInventory().getHolder();
            InventoryHolder inv = left.getLeftSide();
            if (inv != null) {
                return inv.getInventory().getLocation();
            }
        }
        return block.getLocation();
    }

    public static String serializeLoc(Location l) {
        String ret = "";
        if (l.getWorld() != null)
            ret = l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getYaw() + "," + l.getPitch();
        return ret;
    }

    public static Location deserializeLoc(String string) {
        if (string.equals("")) return null;
        String[] st = string.split(",");
        return new Location(Bukkit.getWorld(st[0]), Integer.parseInt(st[1]), Integer.parseInt(st[2]), Integer.parseInt(st[3]), Float.parseFloat(st[4]), Float.parseFloat(st[5]));

    }

    public static ItemStack parseItemStack(String string) {
        ItemStack is;
        String[] gSplit = string.split(" ");
        String[] idsSplit = gSplit[0].split(":");
        is = new ItemStack(Material.valueOf(idsSplit[0]));
        if (idsSplit.length > 1 && is.getData() instanceof Colorable) {
            // TODO
        }
        if (gSplit.length > 1) {
            int metaStart = 2;
            try {
                is.setAmount(Integer.parseInt(gSplit[1]));
            } catch (NumberFormatException ex) {
                metaStart = 1;
            }
            ItemMeta im = is.getItemMeta();
            for (int meta = metaStart; meta < gSplit.length; meta++) {
                String rawKey = gSplit[meta];
                String[] split = rawKey.split(":");
                String key = split[0];

                switch (key) {
                    case "name":
                        if (im != null)
                            im.setDisplayName(split[1].replace("_", " "));
                        break;
                    case "lore":
                        if (im != null) {
                            List<String> lore = new ArrayList<>();
                            for (String line : split[1].split("//")) {
                                lore.add(line.replace("_", " "));
                            }
                            im.setLore(lore);
                        }
                        break;
                    case "color":
                        if (im instanceof LeatherArmorMeta) {
                            LeatherArmorMeta lam = (LeatherArmorMeta) im;
                            String[] csplit = split[1].split(",");
                            Color color = Color.fromRGB(Integer.parseInt(csplit[0]), Integer.parseInt(csplit[1]), Integer.parseInt(csplit[2]));
                            lam.setColor(color);
                        }
                        break;
                    case "effect":
                        PotionMeta pm = (PotionMeta) im;
                        if (pm != null) {
                            String[] psplit = split[1].split(",");
                            if (psplit.length == 3) {
                                PotionEffectType potionEffectType = PotionEffectType.getByName(psplit[0]);
                                if (potionEffectType != null)
                                    pm.addCustomEffect(new PotionEffect(potionEffectType, Integer.parseInt(psplit[1]) * 20, Integer.parseInt(psplit[2])), true);
                            }
                        }
                        break;
                    case "player":
                        if (im instanceof SkullMeta) {
                            ((SkullMeta) im).setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(split[1])));
                        }
                        break;
                    case "enchant":
                        if (im != null) {
                            String[] esplit = split[1].split(",");
                            im.addEnchant(getEnchantment(esplit[0]), Integer.parseInt(esplit[1]), true);
                        }
                        break;
                }
            }

            is.setItemMeta(im);
        }
        return is;
    }

    public static Enchantment getEnchantment(String enc) {
        enc = enc.toUpperCase();
        Enchantment en = Enchantment.getByKey(NamespacedKey.minecraft(enc));

        if (en == null) {
            en = switch (enc) {
                case "PROTECTION" -> Enchantment.PROTECTION_ENVIRONMENTAL;
                case "FIRE_PROTECTION" -> Enchantment.PROTECTION_FIRE;
                case "FEATHER_FALLING" -> Enchantment.PROTECTION_FALL;
                case "BLAST_PROTECTION" -> Enchantment.PROTECTION_EXPLOSIONS;
                case "PROJECTILE_PROTCETION" -> Enchantment.PROTECTION_PROJECTILE;
                case "RESPIRATION" -> Enchantment.OXYGEN;
                case "AQUA_AFFINITY" -> Enchantment.WATER_WORKER;
                case "SHARPNESS" -> Enchantment.DAMAGE_ALL;
                case "SMITE" -> Enchantment.DAMAGE_UNDEAD;
                case "BANE_OF_ARTHROPODS" -> Enchantment.DAMAGE_ARTHROPODS;
                case "LOOTING" -> Enchantment.LOOT_BONUS_MOBS;
                case "EFFICIENCY" -> Enchantment.DIG_SPEED;
                case "UNBREAKING" -> Enchantment.DURABILITY;
                case "FORTUNE" -> Enchantment.LOOT_BONUS_BLOCKS;
                case "POWER" -> Enchantment.ARROW_DAMAGE;
                case "PUNCH" -> Enchantment.ARROW_KNOCKBACK;
                case "FLAME" -> Enchantment.ARROW_FIRE;
                case "INFINITY" -> Enchantment.ARROW_INFINITE;
                case "LUCK_OF_THE_SEA" -> Enchantment.LUCK;
                default -> null;
            };
        }

        return en;
    }
}
