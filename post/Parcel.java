package post;

public class Parcel extends PostObject{

	private static final int MAX_DIM_NORMAL = 60;
	private double tax;
	private boolean fragile;
	private double x;
	private double y;
	private double z;
	
	Parcel(Citizen sender, Citizen receiver, double x, double y, double z, boolean fragile){
		super(sender, receiver);
		this.fragile = fragile;
		//TODO validate dim
		this.x = x;
		this.y = y;
		this.z = z;
		tax = calcTax();
	}
	
	private double calcTax() {
		if(x < MAX_DIM_NORMAL && y < MAX_DIM_NORMAL && z < MAX_DIM_NORMAL){
			tax = 2;
		}
		else{
			tax = 3;
		}
		if(isFragile()){
			tax = tax*1.5;
		}
		return tax;
	}

	@Override
	protected boolean isLetter() {
		return false;
	}

	@Override
	protected boolean isFragile() {
		return fragile;
	}

	@Override
	public long getDeliverDuration() {
		return 5000;
	}
	
	@Override
	public String toString() {
		return (fragile ? "Fragile" : "Ordinary") + " kolet";
	}

}
