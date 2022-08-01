package fr.rolan.stk.enumstk;

public enum Category {
	KING("stk.categories.king"),
	SERVANT("stk.categories.servant");

	String key;
	
	Category(String key){
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}