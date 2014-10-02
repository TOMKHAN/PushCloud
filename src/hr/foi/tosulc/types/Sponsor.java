package hr.foi.tosulc.types;


public class Sponsor {
	private int id_sponsor;
	private String sponsor_name;

	public Sponsor(int id_sponsor, String sponsor_name) {
		this.id_sponsor = id_sponsor;
		this.sponsor_name = sponsor_name;
	}

	public int getId_sponsor() {
		return id_sponsor;
	}

	public void setId_sponsor(int id_sponsor) {
		this.id_sponsor = id_sponsor;
	}

	public String getSponsor_name() {
		return sponsor_name;
	}

	public void setSponsor_name(String sponsor_name) {
		this.sponsor_name = sponsor_name;
	}

}
