package me.bbark9.itemcolorer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

public class Main extends JavaPlugin {
	List<String> items = new ArrayList<String>();
	
	@Override
	public void onEnable() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
		        this, ConnectionSide.SERVER_SIDE, ListenerPriority.HIGHEST, 
		        Packets.Server.SET_SLOT, Packets.Server.WINDOW_ITEMS) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (event.getPacketID() == Packets.Server.SET_SLOT) {
					try {
						addGlow(new ItemStack[] { event.getPacket().getItemModifier().read(0) });
					} catch (FieldAccessException e) {
						// TODO Auto-generated catch block
					}
				} else {
					try {
						addGlow(event.getPacket().getItemArrayModifier().read(0));
					} catch (FieldAccessException e) {
						// TODO Auto-generated catch block
					}
				}
			}
		});
		final File file = new File(getDataFolder() + File.separator + "config.yml");
		
		if (!file.exists()) {
			System.out.println("[Glowy] Generating config.yml");
			this.saveDefaultConfig();
			System.out.println("[Glowy] Generation complete!");
			
			items = getConfig().getStringList("Items");
			
		} else {
			items = getConfig().getStringList("Items");
		}
	}
	
	private void addGlow(ItemStack[] stacks) {
		for (ItemStack stack : stacks) {
			if (stack != null) {
				// Only update those stacks that have our flag enchantment
				if (!(stack.getEnchantmentLevel(Enchantment.ARROW_FIRE) == 32)) {
					String tempStack = stack.getTypeId() + ":" + stack.getData().getData();
					if(items.contains(tempStack))
					{
						stack.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 32);
						
						NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(stack);
						compound.put(NbtFactory.ofList("ench"));
					}
				}
			}
		}
	}
}