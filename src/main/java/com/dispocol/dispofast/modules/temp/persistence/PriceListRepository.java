package com.dispocol.dispofast.modules.temp.persistence;

import com.dispocol.dispofast.modules.temp.PriceList;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, UUID> {}
