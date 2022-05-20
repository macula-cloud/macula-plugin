package org.macula.plugin.datalog.interceptor;

import java.sql.Connection;

import lombok.Setter;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.macula.plugin.datalog.event.EventContext;
import org.macula.plugin.datalog.event.EventContextAware;
import org.macula.plugin.datalog.handler.MappedAuditHandler;
import org.macula.plugin.datalog.meta.MetadataReader;
import org.macula.plugin.datalog.util.MappedAuditHandlerFactory;
import org.macula.plugin.datalog.util.OperationUtils;
import org.macula.plugin.datalog.util.SQLParseUtils;

@Intercepts({
		@Signature(type = Executor.class, method = "update", args = {
				MappedStatement.class,
				Object.class }) })
public class MappedAuditInterceptor implements Interceptor {

	@Setter
	private EventContextAware eventContextAware;
	@Setter
	private MetadataReader metadataReader;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (invocation.getArgs()[0] instanceof MappedStatement && invocation.getArgs().length > 1) {
			MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
			String sqlCommandType = mappedStatement.getSqlCommandType().name();
			Object parameter = invocation.getArgs()[1];

			if (OperationUtils.isOperation(sqlCommandType)) {
				Executor executor = (Executor) invocation.getTarget();
				Connection connection = executor.getTransaction().getConnection();
				BoundSql boundSql = mappedStatement.getBoundSql(parameter);
				Configuration configuration = mappedStatement.getConfiguration();
				String sql = SQLParseUtils.getParameterizedSql(configuration, boundSql);
				MappedAuditHandler handler = MappedAuditHandlerFactory.createEntityAuditHandler(connection,
						sqlCommandType, sql, metadataReader);

				EventContext eventContext = eventContextAware.getEventContext();
				handler.setEventContext(eventContext);

				if (handler != null) {
					handler.beforeHandle();
				}
				Object result = invocation.proceed();
				if (handler != null && result instanceof Integer && ((Integer) result) > 0) {
					handler.afterHandle();
					eventContextAware.processEvent(eventContext);
				}
				return result;
			}
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

}
