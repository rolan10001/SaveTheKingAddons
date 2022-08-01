package fr.rolan.stk.listeners.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.rolan.api.UHCAPI;
import fr.rolan.api.scoreboard.IScoreboardTeam;
import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.events.RepartitionRoleEvent;
import fr.rolan.stk.manager.RoleRegister;
import fr.rolan.stk.manager.STKUser;
import fr.rolan.stk.rolesattributs.Roles;

public class GeneralGameManager implements Listener {
	
	public GeneralGameManager() {
		Bukkit.getPluginManager().registerEvents(this, STKPlugin.getInstance());
	}
	
	@EventHandler
	public void onRepartitionRole(RepartitionRoleEvent event) {
		List<STKUser> u = new ArrayList<>();
		for(IScoreboardTeam sbTeams : UHCAPI.get().getTeam().getTeams()) {
			List<UUID> playersUUID = new ArrayList<UUID>(UHCAPI.get().getTeam().getTeam(sbTeams.getName()).getPlayers());
			List<RoleRegister> config = new ArrayList<RoleRegister>();
			HashMap<Roles, Integer> r = new HashMap<>();
			for(UUID uuid : STKPlugin.getInstance().gameSettings.PLAYERSROLES.keySet())
				if(playersUUID.contains(uuid))
					if(r.containsKey(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid)))
						r.replace(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid), r.get(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid))+1);
					else
						r.put(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid), 1);
			for(RoleRegister roleRegister : STKPlugin.getInstance().getRegisterRoles()) {
				Roles role = null;
				for(Roles ro : r.keySet())
					if(ro.getDisplay().equals(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey()))) {
						role = ro;
						break;
					}
				if(role == null || STKPlugin.getInstance().getSTKGameManager().getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey())).intValue() > r.get(role))
					for(int i = 0; i < ((Integer) STKPlugin.getInstance().getSTKGameManager().getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey()))).intValue(); i++) {
						if(role != null && r.containsKey(role)) {
							r.replace(role, r.get(role)-1);
							if(r.get(role) == 0)
								r.remove(role);
							continue;
						}
						if(role == null || !r.containsKey(role))
							config.add(roleRegister);
					}
			}
			for(UUID uuid : STKPlugin.getInstance().gameSettings.PLAYERSROLES.keySet())
				if(playersUUID.contains(uuid))
					playersUUID.remove(uuid);
			while(!playersUUID.isEmpty()) {
				int n = (int)Math.floor((STKPlugin.getInstance().getSTKGameManager().getRandom().nextFloat() * playersUUID.size()));
				UUID playerUUID = playersUUID.get(n);
				STKUser user = STKPlugin.getInstance().getUser(playerUUID);
				try {
					Roles role = (Roles) ((RoleRegister)config.get(0)).getConstructors().newInstance(new Object[] {STKPlugin.getInstance().getSTKGameManager(), playerUUID});
					Bukkit.getPluginManager().registerEvents((Listener) role, STKPlugin.getInstance());
					user.setRole(role);
					STKPlugin.getInstance().gameSettings.ROLES.add(role);
					STKPlugin.getInstance().gameSettings.PLAYERSROLES.put(playerUUID, role);
					u.add(user);
				}catch(InstantiationException | java.lang.reflect.InvocationTargetException | IllegalAccessException e) {
					e.printStackTrace();
				}
				config.remove(0);
				playersUUID.remove(n);
			}
		}
		for(STKUser user : u) {
			try {
				user.getRole().recoverPotionEffect(user.getRole().recoverPower());
			}catch(Exception e) {}
		}
	}
}
