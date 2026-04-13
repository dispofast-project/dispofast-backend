package com.dispocol.dispofast.shared.MediaAsset.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dispocol.dispofast.shared.MediaAsset.domain.MediaAsset;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {

    Optional<MediaAsset> findById(UUID id);
    
}
