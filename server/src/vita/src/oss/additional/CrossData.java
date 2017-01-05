package oss.additional;

import java.util.LinkedList;

public class CrossData {
	public String userCode;
	public String _group;
	public int group;
	public String _dateStamp;
	public LinkedList<String> products;
	public LinkedList<Float> values;
	public LinkedList<Float> amounts;
	public LinkedList<Float> rents;
	public LinkedList<Float> ramounts;
	public LinkedList<Float> packages;
	public int rentAmount = 0;
	public int orderAmount = 0;
	public int level = 0;
	public CrossData() {
		products = new LinkedList<String>();
		values = new LinkedList<Float>();
		amounts = new LinkedList<Float>();
		rents = new LinkedList<Float>();
		ramounts = new LinkedList<Float>();
		packages = new LinkedList<Float>();
	}
}
