package org.macula.plugins.execlog.event;

import java.util.Date;

import javax.persistence.Lob;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>Execution Log Event</p>
 */
@Getter
@Setter
@ToString
public class ExecutionEvent {

	/**
	 * 调用Key值（业务Id）
	 */
	private String dataKey;

	/**
	 * 调用描述
	 */
	private String description;

	/**
	 * 调用方服务名
	 */
	private String source;

	/**
	 * 调用方方法标记
	 */
	private String sourceMethod;

	/**
	 * 调用参数
	 */
	@Lob
	private String sourceMessage;

	/**
	 * 调用时间
	 */
	private Date sourceTimestamp;

	/**
	 * 目标服务名
	 */
	private String target;

	/**
	 * 目标服务方法
	 */
	private String targetMethod;

	/**
	 * 返回结果
	 */
	@Lob
	private String targetMessage;

	/**
	 * 返回时间
	 */
	private Date targetTimestamp;

	/**
	 * 服务实例
	 */
	private String node;

	/**
	 * 执行状态
	 */
	private String status;

	/**
	 * 异常信息
	 */
	@Lob
	private String exceptionMessage;

	/**
	 * 串联起来的ID
	 */
	private String transactionId;

	/**
	 * 备注信息
	 */
	private String comments;

}
