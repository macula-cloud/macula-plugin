package org.macula.plugin.dataset.handle;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import org.macula.cloud.api.context.CloudApplicationContext;

import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

/**
 * <p> <b>HandlerChainSerializer</b> 是DataProcessorChain的构建工厂. </p>
 * 
 * @since 2011-1-28
 * @author Wilson Luo
 * @version $Id: HandlerChainSerializer.java 3807 2012-11-21 07:31:51Z wilson $
 */
@Component
public class HandlerChainSerializer {

	private final XStream xstream = new XStream();

	public HandlerChainSerializer() {
		xstream.alias("HandlerEntry", HandlerEntry.class);
	}

	/**
	 * 将持久化的XML数据转化为所需的对象.
	 * 
	 * @param xml
	 * @return dataHandlerChain
	 * @throws LinkageError
	 * @throws ClassNotFoundException
	 * @throws BeansException
	 * @throws Exception
	 */
	public DataHandlerChain deserialize(String xml) throws BeansException, ClassNotFoundException, LinkageError {
		if (xml == null || xml.isEmpty()) {
			return null;
		}
		@SuppressWarnings("unchecked")
		List<HandlerEntry> entrys = (List<HandlerEntry>) xstream.fromXML(xml);
		List<DataHandler> proccessores = new ArrayList<DataHandler>();
		for (HandlerEntry entry : entrys) {
			DataHandler processor = (DataHandler) CloudApplicationContext
					.getBean(ClassUtils.forName(entry.getClassName(), getClass().getClassLoader()));
			processor.initialize(entry.getProperties());
			proccessores.add(processor);
		}
		return new DataHandlerChain(proccessores.toArray(new DataHandler[proccessores.size()]));
	}

	/**
	 * 将数据处理链转化为XML数据.
	 * 
	 * @param chain
	 * @return serialize string
	 */
	public String serialize(DataHandlerChain chain) {
		if (chain == null) {
			return null;
		}
		DataHandler[] proccessores = chain.getHandlers();
		List<HandlerEntry> entrys = new ArrayList<HandlerEntry>();
		for (DataHandler objectEntry : proccessores) {
			HandlerEntry entry = new HandlerEntry(objectEntry.getClass().getName(), objectEntry.getProperties());
			entrys.add(entry);
		}
		StringWriter sw = new StringWriter();
		xstream.toXML(entrys, sw);
		return sw.toString();
	}

}
