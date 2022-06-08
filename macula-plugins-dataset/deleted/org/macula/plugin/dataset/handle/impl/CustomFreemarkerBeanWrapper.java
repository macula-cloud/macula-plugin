package org.macula.plugin.dataset.handle.impl;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;

/**
 * <p> <b>CustomFreemarkerBeanWrapper</b> 是个性化的FreemarkerBeanWrapper. </p>
 * 
 */
public class CustomFreemarkerBeanWrapper extends BeansWrapper {

	private static final BeansWrapper INSTANCE = new CustomFreemarkerBeanWrapper(
			Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

	public static final BeansWrapper getWrapperInstance() {
		return INSTANCE;
	}

	public CustomFreemarkerBeanWrapper(Version incompatibleImprovements) {
		super(incompatibleImprovements);
	}

	@Override
	protected freemarker.ext.util.ModelFactory getModelFactory(@SuppressWarnings("rawtypes") Class clazz) {
		freemarker.ext.util.ModelFactory factory;
		if (Object.class.isAssignableFrom(clazz)) {
			factory = userPrincipalFactory;
		} else if (Object.class.isAssignableFrom(clazz)) {
			factory = userContextFactory;
		} else {
			factory = super.getModelFactory(clazz);
		}
		return factory;
	}

	private static final freemarker.ext.util.ModelFactory userContextFactory = new freemarker.ext.util.ModelFactory() {

		@Override
		public TemplateModel create(Object object, ObjectWrapper wrapper) {
			if (object instanceof Object) {
				return new ObjectModel((Object) object, (BeansWrapper) wrapper);
			}
			return null;
		}
	};

	private static final freemarker.ext.util.ModelFactory userPrincipalFactory = new freemarker.ext.util.ModelFactory() {

		@Override
		public TemplateModel create(Object object, ObjectWrapper wrapper) {
			if (object instanceof Object) {
				return new ObjectModel((Object) object, (BeansWrapper) wrapper);
			}
			return null;
		}

	};

	private static final class ObjectModel extends BeanModel {

		private final Object userContext;

		/**
		 * @param object
		 * @param wrapper
		 */
		public ObjectModel(Object userContext, BeansWrapper wrapper) {
			super(userContext, wrapper);
			this.userContext = userContext;
		}

		@Override
		public TemplateModel get(String key) throws TemplateModelException {
			TemplateModel model = super.get(key);
			if (model == null) {
				model = wrapper.wrap(userContext.resolve(key));
			}
			return model;
		}
	}
}
