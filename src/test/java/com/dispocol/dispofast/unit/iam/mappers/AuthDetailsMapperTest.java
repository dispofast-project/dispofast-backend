package com.dispocol.dispofast.unit.iam.mappers;

import com.dispocol.dispofast.modules.iam.api.mappers.AuthUserDetailsMapper;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {AuthUserDetailsMapper.class})
@DisplayName("AuthUserDetailsMapper - Unit Test")
public class AuthDetailsMapperTest {

  @Autowired private AuthUserDetailsMapper authUserDetailsMapper;

  private AppUser testUser;

  @BeforeEach
  void setUp() {

    testUser = new AppUser();
    testUser.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000001"));
    testUser.setFullName("Jane Smith");
    testUser.setEmail("jane.smith@example.com");
    // Ensure password is non-null for Spring Security User builder
    testUser.setPasswordHash("securePassword");
  }

  @Test
  void shouldMapToUserDetails() {
    var userDetails = authUserDetailsMapper.toUserDetails(testUser);

    assert userDetails.getUsername().equals(testUser.getEmail());
    assert userDetails.getPassword().equals(testUser.getPasswordHash());
    assert userDetails.getAuthorities().isEmpty();
    assert userDetails.isEnabled();
  }
}
