package post;

import java.util.Random;

public class Citizen extends Thread {

	public static PostOffice post;
	
	private String name;
	private String family;
	private String address;
	
	Citizen(String name, String family, String address) {
		this.name = name;
		this.family = family;
		this.address = address;
	}
	
	public void postInBox(Citizen receiver){
		int box = new Random().nextInt(PostOffice.NUMBER_OF_BOXES);
		post.postLetterInBox(box, new Letter(this, receiver));
		System.out.println("Letter posted in box " + box);
	}
	
	public void postInOffice(Citizen receiver){
		PostObject obj = null;
		if(new Random().nextBoolean()){
			obj = new Letter(this, receiver);
		}
		else{
			obj = new Parcel(this, receiver, 10, 15, 20, new Random().nextBoolean());
		}
		post.post(obj);
	}
	
	public String getCitizenName() {
		return name;
	}
	
	public String getAddress() {
		return address;
	}
	
	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(new Random().nextBoolean()){
				postInBox(new Citizen("Pesho", "Peshov", "Plovdiv"));
			}
			else{
				postInOffice(new Citizen("Niki", "Tomitov", "Gabrovo"));
			}
		}
	}
	
}
