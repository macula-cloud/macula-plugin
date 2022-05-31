package org.macula.cloud.social.command;

import lombok.Getter;
import lombok.Setter;
import org.macula.plugin.core.principal.LoginCredential;

@Getter
@Setter
public class WechatLoginCredential extends LoginCredential {

	private static final long serialVersionUID = 1L;

	private String code;
	private String encryptedData;
	private String iv;

	public String getBindUsername() {
		String bindName = this.getUsername();
		if (bindName == null) {
			bindName = this.getMobile();
		}
		return bindName;
	}
}
