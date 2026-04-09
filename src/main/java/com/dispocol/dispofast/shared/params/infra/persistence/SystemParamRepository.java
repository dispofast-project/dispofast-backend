package com.dispocol.dispofast.shared.params.infra.persistence;

import com.dispocol.dispofast.shared.params.domain.SystemParam;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemParamRepository extends JpaRepository<SystemParam, Integer> {

  Optional<SystemParam> findByClave(String clave);
}
