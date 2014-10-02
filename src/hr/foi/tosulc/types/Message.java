package hr.foi.tosulc.types;

import java.util.Date;

public class Message {
	private int id_poruke;
	private String tekst;
	private Date vrijeme;
	private String sponsor;

	public Message(int id_poruke, String tekst, Date vrijeme, String sponsor) {
		this.id_poruke = id_poruke;
		this.tekst = tekst;
		this.vrijeme = vrijeme;
		this.sponsor = sponsor;
	}

	public int getId_poruke() {
		return id_poruke;
	}

	public void setId_poruke(int id_poruke) {
		this.id_poruke = id_poruke;
	}

	public String getTekst() {
		return tekst;
	}

	public void setTekst(String tekst) {
		this.tekst = tekst;
	}

	public Date getVrijeme() {
		return vrijeme;
	}

	public void setVrijeme(Date vrijeme) {
		this.vrijeme = vrijeme;
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

}
