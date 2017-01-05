package mxc.app.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;

public class Collection {
	ArrayList<Variant> collection;		
	
	public Collection() {
		collection = new ArrayList<Variant>();
	}
	
	public ArrayList<Variant> getCollection() {
		return collection;		
	}
	
	public int size() {
		return collection.size();
	}
	
	public void mergeCollection(Collection newCollection) {
		for (int i = 0; i < newCollection.size(); i++) {
			Variant v = (Variant)newCollection.elementAt(i);
			collection.add(v);
		}
	}
	
	
	public Hashtable<String, String> indexedCollection(String key, String value) {
		Hashtable<String, String> hash = new Hashtable<String, String>();
		for (int i = 0; i < collection.size(); i++) {
			Variant v = (Variant)collection.get(i);
			hash.put(v.getString(key), Long.toString(v.getInt(value)));
		}
		
		return hash;
	}
	
	public void removeCollection(String key, String value) {
		for (int i = 0; i < collection.size(); i++) {
			Variant v = (Variant)collection.get(i);
			if (v.get(key).equals(value))
				collection.remove(i);
		}				
	}
	
	public void removeCollection(String key, String value, String key1, String value1) {
		for (int i = 0; i < collection.size(); i++) {
			Variant v = (Variant)collection.get(i);
			if (v.get(key).equals(value) && v.get(key1).equals(value1))
				collection.remove(i);
		}				
	}

	public void removeElement(int i) {
		if (i>=0 && i < collection.size())
			collection.remove(i);	
	}

	public void swap(int i, int j) {
		Collections.swap(collection,i,j);	
	}
	
	public void addCollection(Variant v) {
		collection.add(v);
	}
	
	public Variant elementAt(int t) {
		if (t < collection.size())
			return (Variant)collection.get(t);
		
		return null;
	}
	
	public Variant lastElement() {
		if (collection.size() > 0) 
			return (Variant)collection.get(collection.size()-1);
		
		return new Variant();
	}
	
	public void setElementAt(int t, Variant v) {
		collection.set(t, v);
	}
	
	public String[] toArray(String key) {
		String[] array = new String[collection.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = collection.get(i).get(key);
		
		return array;
	}		
	
	public String[] toArray(String key, String key1) {
		String[] array = new String[collection.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = collection.get(i).get(key)+":"+collection.get(i).get(key1);
		
		return array;
	}	
	
	public void updateQuery(int value, String key, String key_find, String nextkey) {
		for (int i = 0; i < collection.size(); i++) {
			Variant v = (Variant)collection.get(i);
			if (v.get(key).equals(key_find)) {
				v.setInt(key, nextkey, value);
				setElementAt(i, v);
			}				
		}
	}	
	
	public Collection toCollection(String key, String value) {
		Collection array = new Collection();
		for (int i = 0; i < collection.size(); i++)
			if (collection.get(i).get(key).equals(value))
				array.addCollection(collection.get(i));
		
		return array;
	}		
	
	public Variant query(String key, String key_find) {		
		for (int i = 0; i < collection.size(); i++) {
			Variant v = (Variant)collection.get(i);
			if (v.get(key).equals(key_find))
				return v;
		}
		
		return new Variant();
	}
	
	public Variant queryString(String key, String key_find) {		
		for (int i = 0; i < collection.size(); i++) {
			Variant v = (Variant)collection.get(i);
			if (v.getString(key).equals(key_find))
				return v;
		}
		
		return new Variant();
	}
	
	public boolean queryHave(String key, String key_find) {		
		for (int i = 0; i < collection.size(); i++) {
			Variant v = (Variant)collection.get(i);
			if (v.get(key).equals(key_find))
				return true;
		}
		
		return false;
	}
	
	public boolean queryHaveIndexOf(String key, String key_find) {		
		for (int i = 0; i < collection.size(); i++) {
			Variant v = (Variant)collection.get(i);
			if (v.get(key).indexOf(key_find) != -1)
				return true;
		}
		
		return false;
	}
	
	public boolean queryHave(String key, String key_find, String key1, String key_find1) {		
		for (int i = 0; i < collection.size(); i++) {
			Variant v = (Variant)collection.get(i);
			if (v.get(key).equals(key_find) && v.get(key1).equals(key_find1))
				return true;
		}
		
		return false;
	}
	
	public Variant query(String key, String key_find, String key1, long key_find1) {		
		for (int i = 0; i < collection.size(); i++) {
			Variant v = (Variant)collection.get(i);
			if (v.get(key).equals(key_find) && v.getInt(key1) == key_find1) {				
				return v;
			}
		}
		
		return new Variant();
	}
	
	public Variant query(String key, String key_find, String key1, String key_find1) {		
		for (int i = 0; i < collection.size(); i++) {
			Variant v = (Variant)collection.get(i);
			if (v.get(key).equals(key_find) && v.get(key1).equals(key_find1))
				return v;
		}
		
		return new Variant();
	}
	
	public LinkedList<Variant> queryList(String key, String key_find) {
		LinkedList<Variant> list = new LinkedList<Variant>();
		for (int i = 0; i < collection.size(); i++) {
			Variant v = (Variant)collection.get(i);
			if (v.get(key).equals(key_find))
				list.add(v);
		}
		
		return list;
	}
}
