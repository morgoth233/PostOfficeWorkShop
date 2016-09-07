package post;

import java.util.ArrayList;

public class Postman extends Citizen{

	private int exp;
	private boolean free = true;
	private int razprateni = 0;
	private ArrayList<PostObject> pratkiNaToziPostman = new ArrayList<>();
	
	Postman(String name, String family, String address, int exp) {
		super(name, family, address);
		this.exp = exp;
	}
	
	public int getRazprateni(){
		return razprateni;
	}

	public boolean isFree() {
		return free;
	}

	public void razdai(ArrayList<PostObject> pratkiNaToziPostman) {
		this.pratkiNaToziPostman = pratkiNaToziPostman;
		razprateni+=pratkiNaToziPostman.size();
		if(!isAlive())
			start();
	}

	@Override
	public void run() {
		while(true){
			if(pratkiNaToziPostman.isEmpty()){
				continue;
			}
			free = false;
			for(PostObject obj : pratkiNaToziPostman){
				try {
					Thread.sleep(obj.getDeliverDuration());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			pratkiNaToziPostman.clear();
			free = true;
		}
	}
}
