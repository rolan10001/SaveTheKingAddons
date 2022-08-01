package fr.rolan.stk.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import fr.rolan.stk.rolesattributs.Roles;

public class STKGameSettings {
	public List<Roles> ROLES = new ArrayList<Roles>();
	public HashMap<UUID, Roles> PLAYERSROLES = new HashMap<UUID, Roles>();
}
