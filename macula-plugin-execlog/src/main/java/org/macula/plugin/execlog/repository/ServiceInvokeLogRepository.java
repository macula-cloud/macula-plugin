package org.macula.plugin.execlog.repository;

import java.util.Optional;

import org.macula.plugin.execlog.domain.ServiceInvokeLog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceInvokeLogRepository extends JpaRepository<ServiceInvokeLog, Long> {

	Optional<ServiceInvokeLog> findByTransactionId(String transactionId);

}
