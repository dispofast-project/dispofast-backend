package com.dispocol.dispofast.modules.iam.application.interfaces;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UpdateUserPermissionRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UpdateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserPermissionsDetailDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserResponseDTO;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.infra.exceptions.UserNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  /**
   * Register a user in the application
   *
   * @param user the user that is going to be registered in the app
   * @return the information of the user that has been registered
   */
  UserResponseDTO register(CreateUserRequestDTO user);

  /**
   * Update the information of a user in the application
   *
   * @param id the id of the user to be updated
   * @param user the new information of the user to be updated
   * @return the information of the user that has been updated
   * @throws UserNotFoundException if the user with the given id does not exist
   */
  UserResponseDTO updatedUser(String id, UpdateUserRequestDTO user);

  /**
   * Update the permissions of a user in the application
   *
   * @param id the id of the user to be updated
   * @param request the new permissions of the user to be updated
   * @return the information of the user with the updated permissions
   * @throws UserNotFoundException if the user with the given id does not exist
   */
  UserPermissionsDetailDTO updateUserPermissions(UUID id, UpdateUserPermissionRequestDTO request);

  /**
   * Deletes a determined user
   *
   * @param user user to be deleted
   */
  void deleteUser(String email);

  /**
   * Search the users by name matching the parameter that the user is entering
   *
   * @param search the input that user uses to do the searching
   * @return list of users that match the search parameter
   */
  Page<AppUser> searchUsers(String search, Pageable pageable);

  /**
   * Gets all users that have been registered
   *
   * @return all the users in the app
   */
  List<AppUser> getUsers();

  /**
   * Get all users in a paged way
   *
   * @param pageable pagination information
   * @return paged users
   */
  Page<UserResponseDTO> getUsersPaged(Pageable pageable);

  /**
   * Search a user given by the email
   *
   * @param email for searching a specific user
   * @return the user that matches that email
   */
  AppUser getUserByEmail(String email);

  /**
   * Get the permissions of a user given by the id
   *
   * @param id the id of the user to get the permissions
   * @return the permissions of the user with the given id
   * @throws UserNotFoundException if the user with the given id does not exist
   */
  UserPermissionsDetailDTO getUserPermissions(UUID id);
}
