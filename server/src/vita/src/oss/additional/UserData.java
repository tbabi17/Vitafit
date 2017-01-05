package oss.additional;

import java.util.Hashtable;
import java.util.LinkedList;

public class UserData {
	public String data;
	public String data1;
	public LinkedList<String> products;
	public LinkedList<Float> values1;
	public LinkedList<Float> amounts1;
	public LinkedList<Float> values2;
	public LinkedList<Float> values3;
	public LinkedList<Float> amounts2;
	
	public float []v1 = new float[32];
	public float []v2 = new float[32];
	public float []v3 = new float[32];
	
	public float []b1 = new float[1000];
	public float []b2 = new float[1000];
	
	public float [][]cc1 = new float[2000][4];
	public float [][]cc2 = new float[2000][4];
	
	public Hashtable<String, Float> cc0 = new Hashtable<String, Float>();
	
	public UserData() {
		products = new LinkedList<String>();
		values1 = new LinkedList<Float>();
		amounts1 = new LinkedList<Float>();
		values2 = new LinkedList<Float>();
		amounts2 = new LinkedList<Float>();
		values3 = new LinkedList<Float>();
		
		cc0 = new Hashtable();
	}
	
	public float getHashFloat(String key) {
		if (cc0.containsKey(key))
			return (float)cc0.get(key);
		
		return 0;
	}
}
