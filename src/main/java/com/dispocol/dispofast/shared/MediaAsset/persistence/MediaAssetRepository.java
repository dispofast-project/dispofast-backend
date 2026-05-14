package com.dispocol.dispofast.shared.MediaAsset.persistence;

import com.dispocol.dispofast.shared.MediaAsset.domain.MediaAsset;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {

  Optional<MediaAsset> findById(UUID id);
}
