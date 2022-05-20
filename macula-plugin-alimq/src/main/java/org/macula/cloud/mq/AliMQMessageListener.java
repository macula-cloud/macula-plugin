package org.macula.cloud.mq;

import java.util.List;

import com.aliyun.openservices.ons.api.MessageListener;

public interface AliMQMessageListener extends MessageListener {

	String getTopic();

	List<String> getSubExpression();

}
