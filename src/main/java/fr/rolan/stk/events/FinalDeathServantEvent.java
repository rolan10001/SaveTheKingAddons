package fr.rolan.stk.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotation.NotNull;

public class FinalDeathServantEvent extends Event {
	
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private final UUID uuid;
	
	public FinalDeathServantEvent(UUID uuid) {
		this.uuid = uuid;
	}
	
	@NotNull
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}
	
	public UUID getUUID() {
		return uuid;
	}
}