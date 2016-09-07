package post;

public abstract class PostObject {

	private Citizen sender;
	private Citizen receiver;
	protected abstract boolean isLetter();
	protected abstract boolean isFragile();
	
	PostObject(Citizen sender, Citizen receiver) {
		this.sender = sender;
		this.receiver = receiver;
	}
	public abstract long getDeliverDuration();
	
	public String getSenderName() {
		return sender.getCitizenName();
	}
	
	public String getReceiverName() {
		return receiver.getCitizenName();
	}
}
