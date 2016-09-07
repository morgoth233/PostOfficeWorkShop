package post;

public class Letter extends PostObject{

	private static final double TAX = 0.5;
	private double tax = TAX;
	
	Letter(Citizen sender, Citizen receiver) {
		super(sender, receiver);
	}
	
	@Override
	protected boolean isLetter() {
		return true;
	}
	@Override
	protected boolean isFragile() {
		return false;
	}

	@Override
	public long getDeliverDuration() {
		return 3000;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Pismo, nqma vreme";
	}
}
