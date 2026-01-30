package com.dispocol.dispofast.unit.iam.mappers;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.dispocol.dispofast.modules.iam.api.dtos.UserResponseDTO;
import com.dispocol.dispofast.modules.iam.api.mappers.UserMapper;
import com.dispocol.dispofast.modules.iam.api.mappers.UserMapperImpl;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.domain.Role;

@SpringBootTest
@ContextConfiguration(
    classes = {
        UserMapperImpl.class
        }
)
@DisplayName("UserMapper - Unit Test")
public class UserMapperTest {
    
    @Autowired
    private UserMapper userMapper;

    private AppUser testUser;
    private Role role1;
    private Role role2;

    @BeforeEach
    void setUp(){

        role1 = new Role();
        role1.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000001"));
        role1.setName("ROLE_USER");

        role2 = new Role();
        role2.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000002"));
        role2.setName("ROLE_ADMIN");

        testUser = new AppUser();
        testUser.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000003"));
        testUser.setFullName("John Doe");
        testUser.setEmail("john.doe@example.com");
    }

    @Test
    @DisplayName("should map AppUser to UserResponseDTO correctly")
    void shouldMapUserToUserResponseDTO() {
        UserResponseDTO userResponseDTO = userMapper.toUserResponseDTO(testUser);

        assert userResponseDTO.getId().equals(testUser.getId().toString());
        assert userResponseDTO.getName().equals(testUser.getFullName());
        assert userResponseDTO.getEmail().equals(testUser.getEmail());
    }

    @Test
    @DisplayName("should map roles correctly")
    void shouldMapRolesCorrectly() {
        List<String> roleNames = userMapper
            .mapRoles(Set.of(role1, role2));

        assert roleNames.size() == 2;
        assert roleNames.contains("ROLE_USER");
        assert roleNames.contains("ROLE_ADMIN");
    }

    @Test
    @DisplayName("should format date-time correctly")
    void shouldFormatDateTimeCorrectly() {
       
    }
}
