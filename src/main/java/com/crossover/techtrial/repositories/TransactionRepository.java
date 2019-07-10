/**
 * 
 */
package com.crossover.techtrial.repositories;

import com.crossover.techtrial.model.Transaction;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * @author crossover
 *
 */
@RestResource(exported = false)
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
	Optional<Transaction> getByBookIdAndDateOfReturnIsNull(Long bookId);

    Optional<Transaction> getByIdAndDateOfReturnNotNull(Long transactionId);

    long countByMemberIdAndDateOfReturnIsNull(Long memberId);

}
