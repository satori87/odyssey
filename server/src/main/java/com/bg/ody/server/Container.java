package com.bg.ody.server;

import java.util.ArrayList;

public class Container extends ArrayList<Item> {

	private static final long serialVersionUID = 1L;

	private double capacity = 0; // total weight capacity
	private double weight = 0; // current weight of all contents
	public double factor = 1; // external weight factor 0-1

	public int maxItems = 0;
	
	public boolean allowOverweight = false; // enable to allow container to exceed capacity

	public Container(double capacity, double factor, boolean allowOverweight, int maxItems) {
		super();
		this.capacity = capacity;
		this.factor = factor;
		this.allowOverweight = allowOverweight;
		this.maxItems = maxItems;
	}

	@Override
	public boolean add(Item i) {
		try {
			double w = i.getWeight();
			if (w + weight <= capacity || allowOverweight || capacity == 0) {
				if (super.add(i)) {
					weight += w;
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean remove(Object i) {
		try {
			if (super.remove((Item) i)) {
				weight -= ((Item) i).getWeight();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void clear() {
		super.clear();
		weight = 0;
	}

	public double getWeight() { // returns the EXTERNAL weight
		return weight * factor;
	}

}