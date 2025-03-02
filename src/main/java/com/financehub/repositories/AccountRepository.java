package com.financehub.repositories;

import com.financehub.models.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository <Account, String> {
    Optional<Account> findById(String id);
    Optional<Account> findByIdOwnerId(String ownerId);
}
