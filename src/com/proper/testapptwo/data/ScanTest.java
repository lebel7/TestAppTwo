package com.proper.testapptwo.data;

public class ScanTest implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private long transactionId;
	private int productId;
	private long queryTime;
	private String testDoneBy;
	public ScanTest() {
	}
	public ScanTest(long transactionId, int productId, long queryTime,
			String testDoneBy) {
		this.transactionId = transactionId;
		this.productId = productId;
		this.queryTime = queryTime;
		this.testDoneBy = testDoneBy;
	}
	public long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public long getQueryTime() {
		return queryTime;
	}
	public void setQueryTime(long queryTime) {
		this.queryTime = queryTime;
	}
	public String getTestDoneBy() {
		return testDoneBy;
	}
	public void setTestDoneBy(String testDoneBy) {
		this.testDoneBy = testDoneBy;
	}
}
