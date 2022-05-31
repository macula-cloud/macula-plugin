package org.macula.plugin.dataset.query;

/**
 * <p>
 * <b>TokenHolder</b> 是占位符
 * </p>
 */
public class TokenHolder {

	private final String openToken;
	private final String closeToken;

	public TokenHolder(String openToken, String closeToken) {
		this.openToken = openToken;
		this.closeToken = closeToken;
	}

	/**
	 * @return the openToken
	 */
	public String getOpenToken() {
		return openToken;
	}

	/**
	 * @return the closeToken
	 */
	public String getCloseToken() {
		return closeToken;
	}
}
