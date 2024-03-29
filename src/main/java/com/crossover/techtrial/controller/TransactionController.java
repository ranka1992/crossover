/**
 * 
 */
package com.crossover.techtrial.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.crossover.techtrial.model.Book;
import com.crossover.techtrial.model.MembershipStatus;
import com.crossover.techtrial.model.Transaction;
import com.crossover.techtrial.repositories.BookRepository;
import com.crossover.techtrial.repositories.MemberRepository;
import com.crossover.techtrial.repositories.TransactionRepository;

/**
 * @author kshah
 *
 */
@RestController
public class TransactionController {
  
  @Autowired TransactionRepository transactionRepository;
  
  @Autowired BookRepository bookRepository;
  
  @Autowired MemberRepository memberRepository;
  /*
   * PLEASE DO NOT CHANGE SIGNATURE OR METHOD TYPE OF END POINTS
   * Example Post Request :  { "bookId":1,"memberId":33 }
   */
  @PostMapping(path = "/api/transaction")
  public ResponseEntity<Transaction> issueBookToMember(@RequestBody Map<String, Long> params){
	  System.out.println("inside method ");
    Long bookId = params.get("bookId");
    Long memberId = params.get("memberId");
    Transaction transaction = new Transaction();
    Optional<Book> optionalBook = bookRepository.findById(bookId);
 // check if book ( bookId ) exists
    
    if (!optionalBook.isPresent()) {
    	System.out.println("inside  book present or not");
        return ResponseEntity.notFound().build();
    }
    transaction.setBook(bookRepository.findById(bookId).get());
 // check if already exists an active transaction for this book ( bookId )
    if (transactionRepository.getByBookIdAndDateOfReturnIsNull(bookId).isPresent()) {
    	System.out.println("inside check active trancsaction  ");
        return ResponseEntity.status(403).build();
    }
        
     // check if member already has reached the book issuance limit number
        if (transactionRepository.countByMemberIdAndDateOfReturnIsNull(memberId) >= 5) {
        	System.out.println("inside member reached limit");
            return ResponseEntity.status(403).build();
        }
        
        
    transaction.setMember(memberRepository.findById(memberId).get());
    
 // check if membership is active
    if (!transaction.getMember().getMembershipStatus().equals(MembershipStatus.ACTIVE)) {
    	System.out.println("inside membership active");
        return ResponseEntity.status(403).build();
        
    }
    transaction.setDateOfIssue(LocalDateTime.now());    
    return ResponseEntity.ok().body(transactionRepository.save(transaction));
  }
  /*
   * PLEASE DO NOT CHANGE SIGNATURE OR METHOD TYPE OF END POINTS
   */
  @PatchMapping(path= "/api/transaction/{transaction-id}/return")
  public ResponseEntity<Transaction> returnBookTransaction(@PathVariable(name="transaction-id") Long transactionId){
	  Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
	// check if transaction ( transactionId ) exists
      if (!optionalTransaction.isPresent()) {
    	  System.out.println("inside  transaction method ");
          return ResponseEntity.notFound().build();
      }
      // check if transaction ( transactionId ) is already closed
      if (transactionRepository.getByIdAndDateOfReturnNotNull(transactionId).isPresent()) {
    	  System.out.println("inside  transaction closed");
          return ResponseEntity.status(403).build();
      }
      Transaction transaction = optionalTransaction.get();
      transaction.setDateOfReturn(LocalDateTime.now());

    //transaction.setDateOfReturn(LocalDateTime.now());
    //return ResponseEntity.ok().body(transaction);
      return ResponseEntity.ok().body(transactionRepository.save(transaction));
  }

}
