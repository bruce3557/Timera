package codevenger.timera.authorization;

public class PhotoInform {
	private int farm;
	private String id,owner,title,server,secret;
	public PhotoInform(String id,String owner,String title,String server,String secret,int farm){
		this.farm = farm;
		this.id = id;
		this.owner = owner;
		this.title = title;
		this.server = server;
		this.secret = secret;
	}
	public String getOwner(){
		return owner;
	}
	public String getTitle(){
		return title;
	}
	public String getPhotoUrl(String size){
		return "http://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+"_"+size+".jpg";
	}
	public String getPhotoUrl(){
		return "http://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+".jpg";
	}
}
