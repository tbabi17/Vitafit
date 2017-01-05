package oss.additional;

public class GroupPlan {
	public String productCode;
	public int _group;
	public String section;
	public float[] values1, values2, values3;
	
	public GroupPlan() {
		values1 = new float[100];
		values2 = new float[100];
		values3 = new float[100];
	}
}

