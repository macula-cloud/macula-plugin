package org.macula.plugins.datalog.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import org.springframework.util.CollectionUtils;

public class SQLParseUtils {

	private static Pattern pattern1 = Pattern.compile("(([A-Za-z0-9_]+)(?:\\.))?`?([A-Za-z0-9_]+)`?");

	private static Pattern pattern2 = Pattern.compile("`\\.`");

	private static Pattern pattern3 = Pattern.compile("[\\s]+");

	private static final Pattern ParameterizedPattern1 = Pattern.compile("\\?(?=\\s*[^']*\\s*,?\\s*(\\w|$))");

	private static final Pattern ParameterizedPattern2 = Pattern.compile("[\\s]+");

	public static String[] separateAliasAndColumn(String combinedColumn) {
		String alias = null;
		String column = null;
		Matcher matcher = pattern1.matcher(combinedColumn);
		if (matcher.matches()) {
			switch (matcher.groupCount()) {
			case 3:
				alias = matcher.group(2);
				column = matcher.group(3);
				break;
			case 1:
				column = matcher.group(1);
				break;
			default:
				break;
			}
		}
		return new String[] {
				alias,
				column };
	}

	public static String normalize(String name) {
		if (name == null) {
			return null;
		}

		if (name.length() > 2) {
			char c0 = name.charAt(0);
			char x0 = name.charAt(name.length() - 1);
			if ((c0 == '"' && x0 == '"') || (c0 == '`' && x0 == '`')) {
				String normalizeName = name.substring(1, name.length() - 1);
				if (c0 == '`') {
					normalizeName = pattern2.matcher(normalizeName).replaceAll(".");
				}
				return normalizeName;
			}
		}

		return name;
	}

	public static String trimSQLWhitespaces(String sql) {
		return pattern3.matcher(sql).replaceAll(" ");
	}

	public static String getParameterizedSql(Configuration configuration, BoundSql boundSql) {
		Object parameterObject = boundSql.getParameterObject();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		String sql = ParameterizedPattern2.matcher(boundSql.getSql()).replaceAll(" ");
		if (!CollectionUtils.isEmpty(parameterMappings) && parameterObject != null) {
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
				sql = ParameterizedPattern1.matcher(sql)
						.replaceFirst(Matcher.quoteReplacement(getParameterValue(parameterObject)));
			} else {
				MetaObject metaObject = configuration.newMetaObject(parameterObject);
				for (ParameterMapping parameterMapping : parameterMappings) {
					String propertyName = parameterMapping.getProperty();
					if (metaObject.hasGetter(propertyName)) {
						Object obj = metaObject.getValue(propertyName);
						sql = ParameterizedPattern1.matcher(sql)
								.replaceFirst(Matcher.quoteReplacement(getParameterValue(obj)));
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						Object obj = boundSql.getAdditionalParameter(propertyName);
						sql = ParameterizedPattern1.matcher(sql)
								.replaceFirst(Matcher.quoteReplacement(getParameterValue(obj)));
					} else {
						sql = ParameterizedPattern1.matcher(sql).replaceFirst("[UNSET]");
					}
				}
			}
		}
		return sql;
	}

	public static String getParameterValue(Object obj) {
		String value;
		if (obj instanceof String) {
			value = "'" + obj.toString() + "'";
		} else if (obj instanceof Date) {
			DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
			value = "'" + formatter.format(new Date()) + "'";
		} else {
			if (obj != null) {
				value = obj.toString();
			} else {
				value = "null";
			}

		}
		return value;
	}
}
