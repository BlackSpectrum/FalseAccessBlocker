package falseaccessblocker;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FalseAccessBlocker extends JavaPlugin implements Listener
{
	@Override
	public void onDisable() {}
	
	@Override
	public void onEnable()
	{
	  PluginManager pm = getServer().getPluginManager();
	  pm.registerEvents(this, this);
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onBlockBreack(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		if (event.isCancelled())
		{
			player.setMetadata( "lastCancelledEvent", new FixedMetadataValue(this, player.getLocation()) );
			return;
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerInteractBlockMonitor(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) 
		{
			if (event.isCancelled())
			{
				player.setMetadata( "lastCancelledEvent", new FixedMetadataValue(this, player.getLocation()) );
				return;
			}
		}
		
		if ( player.hasMetadata("lastCancelledEvent") )
		{
			Location lastCancelledEvent = new Location(player.getWorld(), 0, -10, 0);
			for (MetadataValue meta : player.getMetadata("lastCancelledEvent")) 
			{
				if (meta.getOwningPlugin().equals(this))
					lastCancelledEvent = (Location)meta.value();
		    }
			
			
			if( getAbsDelta(lastCancelledEvent.getPitch(), player.getLocation().getPitch())  < 5f && 
					getAbsDelta(lastCancelledEvent.getYaw(), player.getLocation().getYaw()) < 5f &&
					lastCancelledEvent.distance(player.getLocation()) < 0.5)
			{
				event.setCancelled(true);
			}
		    
			player.removeMetadata("lastCancelledEvent", this);
	    
		}
	}
	
	private float getAbsDelta(float f1, float f2)
	{
		return Math.abs(f1 - f2);
	}
}
