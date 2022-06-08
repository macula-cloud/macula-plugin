package org.macula.cloud.social.controller;

import org.macula.cloud.social.command.WechatLoginCredential;
import org.macula.cloud.social.service.WeChatService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/wechat")
public class WeChatController {

	private WeChatService weChatService;

	@Autowired
	public WeChatController(ObjectProvider<WeChatService> weChatService) {
		this.weChatService = weChatService.getIfAvailable();
	}

	@PostMapping(value = "/login")
	public ResponseEntity<String> login(@RequestBody WechatLoginCredential loginCredential) {
		return new ResponseEntity<String>(weChatService.bindAndLogin(loginCredential), HttpStatus.OK);
	}

	@PostMapping(value = "/token")
	public ResponseEntity<String> token(@RequestBody WechatLoginCredential loginCredential) {
		return new ResponseEntity<String>(weChatService.token(loginCredential), HttpStatus.OK);
	}

	@DeleteMapping(value = "/unbind")
	public ResponseEntity<?> unBind(@RequestBody WechatLoginCredential loginCredential) {
		weChatService.unbind(loginCredential);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = "/access_token")
	public ResponseEntity<String> getAccessToken(@RequestBody WechatLoginCredential loginCredential) {
		return new ResponseEntity<String>(weChatService.getAccessToken(loginCredential), HttpStatus.OK);
	}
}
