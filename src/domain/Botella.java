package domain;

public class Botella {
	
	private String color;
	private String flavor; 
	
	
	public Botella(String color, String flavor) {
		super();
		this.color = color;
		this.flavor = flavor;
	}

	public String getFlavor() {
		return flavor;
	}

	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	

}
