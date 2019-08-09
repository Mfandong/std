package org.std.model;

import java.io.Serializable;

public class OrderInfo implements Serializable{
	private static final long serialVersionUID = 6685614196809911024L;
	private String orderid;
	private String title;
	private String money;
	private String createtime;
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	@Override
	public String toString() {
		return "OrderInfo [orderid=" + orderid + ", title=" + title + ", money=" + money + ", createtime=" + createtime
				+ "]";
	}
}
