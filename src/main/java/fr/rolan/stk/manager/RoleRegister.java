package fr.rolan.stk.manager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.enumstk.Category;

public class RoleRegister {
	
	final List<Category> categories = new ArrayList<Category>();
	
	final String key;
	
	List<String> lore = new ArrayList<String>();
	
	Constructor<?> constructors = null;
	
	public RoleRegister(String key) {
		this.key = key;
	}
	
	public RoleRegister registerRole(Class<?> roleClass) throws NoSuchMethodException {
		this.constructors = roleClass.getConstructor(new Class[] {STKGameManager.class, UUID.class});
		return this;
	}
	
	public RoleRegister addCategory(Category category) {
		this.categories.add(category);
		return this;
	}
	
	public RoleRegister setLore(List<String> lore) {
		this.lore = lore;
		return this;
	}
	
	public void create() {
		if(this.constructors == null)
			return;
		STKPlugin.getInstance().getRegisterRoles().add(this);
	}
	
	public List<String> getLore() {
		return this.lore;
	}
	
	public String getName() {
		return STKPlugin.getInstance().getSTKConfig().getString(key);
	}
	
	public List<Category> getCategories() {
		return this.categories;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public Constructor<?> getConstructors() {
		return this.constructors;
	}
}
