package com.sixsprints.core.interceptors;

import org.springframework.stereotype.Component;

import com.sixsprints.core.auth.ModuleDefinition;
import com.sixsprints.core.auth.PermissionDefinition;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.exception.NotAuthenticatedException;
import com.sixsprints.core.interceptor.AbstractAuthenticationInterceptor;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.service.UserService;

@Component
public class AuthInterceptor extends AbstractAuthenticationInterceptor<User> {

  public AuthInterceptor(UserService userService) {
    super(userService);
  }

  @Override
  protected String authTokenKey() {
    return "X-AUTH-TOKEN";
  }

  @Override
  protected void checkUserPermissions(User user, ModuleDefinition module, PermissionDefinition permission,
    boolean required) throws NotAuthenticatedException, EntityNotFoundException {
    System.out.println(module.name());
    System.out.println(permission.name());
    System.out.println(required);
  }

  @Override
  protected void checkIfTokenInvalid(User user, String token, boolean required) throws NotAuthenticatedException {
    System.out.println(user);
    System.out.println(token);
    System.out.println(required);
  }

}
