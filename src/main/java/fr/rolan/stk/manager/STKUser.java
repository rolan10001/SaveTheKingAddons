package fr.rolan.stk.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import fr.rolan.stk.enumstk.State;
import fr.rolan.stk.rolesattributs.Roles;

public class STKUser {
	
	private final UUID uuid;
	private Roles role;
	private State state;
	private List<ItemStack> itemsDeath = new ArrayList<ItemStack>();
	private boolean kit;
	
	public STKUser(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getUUID() {
		return uuid;
	}
	
	public void setRole(Roles role) {
		this.role = role;
	}
	
	public boolean isRole(Roles role) {
		return role.equals(this.role);
	}
	
	public Roles getRole() {
		return role;
	}
	
	public State getState() {
		return state;
	}
	
	public boolean isState(State state) {
		return state.equals(this.state);
	}

	public void setState(State state) {
		this.state = state;
	}
	
	public List<ItemStack> getItemsDeath() {
		return this.itemsDeath;
	}

	public void setItemsDeath(ItemStack[] itemsDeath) {
		this.itemsDeath.addAll(Arrays.asList(itemsDeath));
	}
	
	public void setKit(Boolean paramBoolean) {
		this.kit = paramBoolean;
	}
	
	public boolean hasKit() {
		return kit;
	}
}
