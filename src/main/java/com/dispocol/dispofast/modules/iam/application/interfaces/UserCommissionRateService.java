package com.dispocol.dispofast.modules.iam.application.interfaces;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserCommissionRateDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserCommissionRateDTO;
import java.util.List;
import java.util.UUID;

public interface UserCommissionRateService {

  List<UserCommissionRateDTO> getCommissionRates(UUID userId);

  UserCommissionRateDTO createCommissionRate(UUID userId, CreateUserCommissionRateDTO dto);

  void deleteCommissionRate(UUID userId, UUID rateId);
}
