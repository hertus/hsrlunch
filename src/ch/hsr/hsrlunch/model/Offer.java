package ch.hsr.hsrlunch.model;

public class Offer {
	
	String title;
	String content;
	String date;

	double price;
	
	

	
	public Offer(String title, String content, double price, String date) {
		super();
		this.title = title;
		this.content = content;
		this.price = price;
		this.date = date;
	}
	
	public String getMenuText(){
		return content;
	}
//	public String getMenuText(){
//		return title +"\n" + date + "\n" + content+ "\n"+price;
//	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	

}
