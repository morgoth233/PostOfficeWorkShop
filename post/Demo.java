package post;

public class Demo {

	
	public static void main(String[] args) {
		
		Citizen c1 = new Citizen("Krasi", "Stoev", "Sofia");
		Citizen c2 = new Citizen("Krasi", "Stoev", "Sofia");
		Citizen c3 = new Citizen("Krasi", "Stoev", "Sofia");
		Citizen c4 = new Citizen("Krasi", "Stoev", "Sofia");
		Citizen c5 = new Citizen("Krasi", "Stoev", "Sofia");
		
		Citizen.post = new PostOffice();
		c1.start();
		c2.start();
		c3.start();
		c4.start();
		c5.start();
		Citizen.post.startWork();
		
	}
}
