package mxc.app.engine;

import java.util.Hashtable;

public class Variant {	
	Hashtable<String, String> items;	
	
	public Variant() {
		items = new Hashtable<String, String>();		
	}
	
	public int size() {
		return items.size();
	}
	
	public String nextKey(String key) {		
		return key;
	}
	
	public void put(String key, String value) {
		if (key == null) return;
		if (value == null) value = "";
		if (items.containsKey(key))			
			key = nextKey(key);		
		
		items.put(key.trim(), value);
	}
	
	public boolean found(String key) {
		return items.containsKey(key);			
	}
	
	public String get(String key) {
		if (items.containsKey(key))
			return items.get(key).toString();
		
		return "";
	}
		
	public long getInt(String key) {
		if (items.containsKey(key)) {
			String value = items.get(key).toString();
			if (value != null && value.length() >= 1) {
				char c = value.charAt(0);
				if (c == 'i') value = value.substring(1, value.length());
			}
			return Integer.parseInt(value);
		}
		
		return 0;
	}
	
	public float getFloat(String key) {
		if (items.containsKey(key)) {
			String value = items.get(key).toString();
			if (value != null && value.length() >= 1) {
				char c = value.charAt(0);
				if (c == 'f') value = value.substring(1, value.length());
			}
			
			float val = 0;
			try {
				val = Float.parseFloat(value);
			} catch (Exception e) {
				
			}
			return val;
		}
		
		return 0;
	}
	
	public String getString(String key) {
		if (items.containsKey(key)) {
			String value = items.get(key).toString();
			if (value != null && value.length() >= 1) {
				char c = value.charAt(0);
				if (c == 's') value = value.substring(1, value.length());
			}
			return value;
		}
		
		return "";
	}
	
	public String getString(String key, int len) {
		if (items.containsKey(key)) {
			String value = items.get(key).toString();
			if (value != null && value.length() >= 1) {
				char c = value.charAt(0);
				if (c == 's') value = value.substring(1, value.length());
			}
			
			return value.substring(0, Math.min(len, value.length()));
		}
		
		return "";
	}
	
	public void setInt(String key, String nextkey, int value) {
		if (items.containsKey(key)) {
			int gvalue = Integer.parseInt(items.get(nextkey));
			gvalue+=value;			
			items.put(nextkey, Integer.toString(gvalue));
		}
	}
}
