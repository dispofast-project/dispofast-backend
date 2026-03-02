package com.dispocol.dispofast.modules.temp.account.infra.persistence;

import com.dispocol.dispofast.modules.temp.account.Person;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {}
