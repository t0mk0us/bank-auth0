package com.tamara.authorization.bankappliauthorization.repository;

import java.util.List;

public interface UserRepository {

	String checkUserScopes(String scopes, List<String> requestedScope);

}
