package com.dispocol.dispofast.modules.pricelist.infra.persistence;

import com.dispocol.dispofast.modules.pricelist.domain.PriceList;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceListRepository extends JpaRepository<PriceList, UUID> {}
