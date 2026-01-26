package com.dispocol.dispofast.modules.iam.application.interfaces;

import java.util.List;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserResponseDTO;
import com.dispocol.dispofast.modules.iam.domain.AppUser;

public interface UserService {
    
    /**
     * Register a user in the application
     * 
     * @param user the user that is going to be registered in the app
     * @return the information of the user that has been registered
     */
    UserResponseDTO register(CreateUserRequestDTO user);

    /**
     * Search the users by name matching the parameter that the user is entering
     * 
     * @param search the input that user uses to do the searching
     * @return list of users that match the search parameter 
     */
    List<AppUser> searchUsers(String search);

    /**
     * Deletes a determined user
     * 
     * @param user user to be deleted
     */
    void deleteUser(AppUser user);

    /**
     * Gets all users that have been registered
     * 
     * @return all the users in the app
     */
    List<AppUser> getUsers();

    /**
     * Search a user given by the email
     * 
     * @param email for searching a specific user
     * @return the user that matches that email
     */
    AppUser getUserByEmail(String email);

    

}
