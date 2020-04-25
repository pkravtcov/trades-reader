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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + orderCount;
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof StartTimeCounter)) {
			return false;
		}
		StartTimeCounter other = (StartTimeCounter) obj;
		if (orderCount != other.orderCount) {
			return false;
		}
		if (startTime == null) {
			if (other.startTime != null) {
				return false;
			}
		} else if (!startTime.equals(other.startTime)) {
			return false;
		}
		return true;
	}
}
