package com.lste.trades.data;

public class StartTimeCounter {
	private String startTime;
	private int orderCount;
		
	public StartTimeCounter(String startTime, int orderCount) {
		this.startTime = startTime;
		this.orderCount = orderCount;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public int getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

}
