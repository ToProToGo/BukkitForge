package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitWorld;
import keepcalm.mods.bukkit.bukkitAPI.BukkitArt;
import net.minecraft.src.EntityPainting;
import net.minecraft.src.EnumArt;
import net.minecraft.src.WorldServer;

import org.bukkit.Art;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
//import org.bukkit.craftbukkit.BukkitArt;
//import org.bukkit.craftbukkit.BukkitServer;
//import org.bukkit.craftbukkit.BukkitWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;

public class BukkitPainting extends BukkitEntity implements Painting {

    public BukkitPainting(BukkitServer server, EntityPainting entity) {
        super(server, entity);
    }

    public Art getArt() {
        EnumArt art = getHandle().art;
        return BukkitArt.NotchToBukkit(art);
    }

    public boolean setArt(Art art) {
        return setArt(art, false);
    }

    public boolean setArt(Art art, boolean force) {
        EntityPainting painting = this.getHandle();
        EnumArt oldArt = painting.art;
        painting.art = BukkitArt.BukkitToNotch(art);
        painting.setDirection(painting.direction);
        if (!force && painting.isDead) {
            // Revert painting since it doesn't fit
            painting.art = oldArt;
            painting.setDirection(painting.direction);
            return false;
        }
        this.update();
        return true;
    }

    public BlockFace getAttachedFace() {
        return getFacing().getOppositeFace();
    }

    public void setFacingDirection(BlockFace face) {
        setFacingDirection(face, false);
    }

    public boolean setFacingDirection(BlockFace face, boolean force) {
        Block block = getLocation().getBlock().getRelative(getAttachedFace()).getRelative(face.getOppositeFace()).getRelative(getFacing());
        EntityPainting painting = getHandle();
        int x = (int) Math.round(painting.posX), y = (int) Math.round(painting.posY), z = (int) Math.round(painting.posZ), dir = painting.direction;
        painting.posX = block.getX();
        painting.posY = block.getY();
        painting.posZ = block.getZ();
        switch (face) {
        case EAST:
        default:
            getHandle().setDirection(0);
            break;
        case NORTH:
            getHandle().setDirection(1);
            break;
        case WEST:
            getHandle().setDirection(2);
            break;
        case SOUTH:
            getHandle().setDirection(3);
            break;
        }
        if (!force && painting.isDead) {
            // Revert painting since it doesn't fit
            painting.posX = x;
            painting.posY = y;
            painting.posZ = z;
            painting.setDirection(dir);
            return false;
        }
        this.update();
        return true;
    }

    public BlockFace getFacing() {
        switch (this.getHandle().direction) {
        case 0:
        default:
            return BlockFace.EAST;
        case 1:
            return BlockFace.NORTH;
        case 2:
            return BlockFace.WEST;
        case 3:
            return BlockFace.SOUTH;
        }
    }

    private void update() {
        WorldServer world = ((BukkitWorld) getWorld()).getHandle();
        EntityPainting painting = new EntityPainting(world);
        painting.posX = getHandle().posX;
        painting.posY = getHandle().posY;
        painting.posZ = getHandle().posZ;
        painting.art = getHandle().art;
        painting.setDirection(getHandle().direction);
        getHandle().setDead();
        getHandle().velocityChanged = true; // because this occurs when the painting is broken, so it might be important
        world.spawnEntityInWorld(painting);
        this.entity = painting;
    }

    @Override
    public EntityPainting getHandle() {
        return (EntityPainting) entity;
    }

    @Override
    public String toString() {
        return "BukkitPainting{art=" + getArt() + "}";
    }

    public EntityType getType() {
        return EntityType.PAINTING;
    }
}
