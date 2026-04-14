package com.dispocol.dispofast.modules.customers.infra.persistence;

import com.dispocol.dispofast.modules.customers.domain.Client;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository
    extends JpaRepository<Client, UUID>, JpaSpecificationExecutor<Client> {
  boolean existsByIdentificationNumber(String identificationNumber);

  boolean existsByEmailIgnoreCase(String email);

  boolean existsByIdentificationNumberAndIdNot(String identificationNumber, UUID id);

  boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
}
