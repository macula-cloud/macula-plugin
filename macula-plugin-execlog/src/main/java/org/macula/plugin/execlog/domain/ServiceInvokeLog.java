package org.macula.plugin.execlog.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Setter
@Getter
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "MC_SERVICE_INVOKE_LOG")
public class ServiceInvokeLog extends org.macula.engine.commons.domain.ServiceInvokeLog {

	private static final long serialVersionUID = 1L;

}
