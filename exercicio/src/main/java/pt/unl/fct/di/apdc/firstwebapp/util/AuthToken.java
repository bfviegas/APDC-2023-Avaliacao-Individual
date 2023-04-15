package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.UUID;



public class AuthToken {

	private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2 hours

	public String username;
	public String role;
	public String tokenID;
	public long creationDate;
	public long expirationDate;
	public String verifier;

	public AuthToken() {
	}

	public AuthToken(String username, String role) {
		this.username = username;
		this.role = role;
		this.creationDate = System.currentTimeMillis();
		this.expirationDate = creationDate + AuthToken.EXPIRATION_TIME;
		this.verifier = UUID.randomUUID().toString();
	}
	
}