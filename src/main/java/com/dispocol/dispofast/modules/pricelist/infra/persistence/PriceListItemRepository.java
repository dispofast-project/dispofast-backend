package com.dispocol.dispofast.modules.pricelist.infra.persistence;

import com.dispocol.dispofast.modules.pricelist.domain.PriceListItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceListItemRepository extends JpaRepository<PriceListItem, UUID> {

  Optional<PriceListItem> findByPriceList_IdAndProduct_Reference(UUID priceListId, String reference);

  List<PriceListItem> findByPriceList_Id(UUID priceListId);

  void deleteByPriceList_Id(UUID priceListId);
}
