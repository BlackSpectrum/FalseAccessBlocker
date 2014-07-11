package it.github.mats391.falseaccessblocker;

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

	private float getAbsDelta( final float f1, final float f2 ) {
		return Math.abs( f1 - f2 );
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreack( final BlockBreakEvent event ) {
		final Player player = event.getPlayer();
		if ( event.isCancelled() )
		{
			player.setMetadata( "lastCancelledEvent", new FixedMetadataValue( this, player.getLocation() ) );
			return;
		}
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
		final PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents( this, this );
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractBlockMonitor( final PlayerInteractEvent event ) {
		final Player player = event.getPlayer();
		if ( event.getAction().equals( Action.RIGHT_CLICK_BLOCK ) )
			if ( event.isCancelled() )
			{
				player.setMetadata( "lastCancelledEvent", new FixedMetadataValue( this, player.getLocation() ) );
				return;
			}

		if ( player.hasMetadata( "lastCancelledEvent" ) )
		{
			Location lastCancelledEvent = new Location( player.getWorld(), 0, -10, 0 );
			for ( final MetadataValue meta : player.getMetadata( "lastCancelledEvent" ) )
				if ( meta.getOwningPlugin().equals( this ) )
					lastCancelledEvent = (Location) meta.value();

			if ( this.getAbsDelta( lastCancelledEvent.getPitch(), player.getLocation().getPitch() ) < 5f
					&& this.getAbsDelta( lastCancelledEvent.getYaw(), player.getLocation().getYaw() ) < 5f
					&& lastCancelledEvent.distance( player.getLocation() ) < 0.5 )
				event.setCancelled( true );

			player.removeMetadata( "lastCancelledEvent", this );

		}
	}
}
