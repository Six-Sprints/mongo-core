package com.sixsprints.core.interceptor;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sixsprints.core.auth.AuthAnnotationDataDto;
import com.sixsprints.core.auth.BasicAuth;
import com.sixsprints.core.auth.BasicModuleEnum;
import com.sixsprints.core.auth.BasicPermissionEnum;
import com.sixsprints.core.interceptors.AuthInterceptor;
import com.sixsprints.core.mock.service.UserService;
import com.sixsprints.core.mock.service.impl.UserServiceImpl;

public class AbstractAuthenticationInterceptorAnnotationMergeTest {

  private AuthInterceptor authInterceptor;
  private Method annotationDataMethod;

  @BeforeEach
  public void setUp() throws Exception {
    // Create a stub UserService - we only need it for instantiation, annotationData doesn't use it
    // Using UserServiceImpl as a base - the annotationData method doesn't call any UserService methods
    UserService userService = new UserServiceImpl();
    authInterceptor = new AuthInterceptor(userService);

    // Use reflection to access the private annotationData method
    annotationDataMethod =
        AbstractAuthenticationInterceptor.class.getDeclaredMethod("annotationData", Method.class);
    annotationDataMethod.setAccessible(true);
  }

  @Test
  public void shouldReturnNullWhenNoAnnotations() throws Exception {
    Method method = TestClassNoAnnotations.class.getMethod("testMethod");
    AuthAnnotationDataDto result =
        (AuthAnnotationDataDto) annotationDataMethod.invoke(authInterceptor, method);

    assertThat(result).isNull();
  }

  @Test
  public void shouldUseMethodAnnotationOnly() throws Exception {
    Method method = TestClassNoAnnotations.class.getMethod("methodWithAnnotation");
    AuthAnnotationDataDto result =
        (AuthAnnotationDataDto) annotationDataMethod.invoke(authInterceptor, method);

    assertThat(result).isNotNull();
    assertThat(result.getModule()).isEqualTo(BasicModuleEnum.USER);
    assertThat(result.getPermission()).isEqualTo(BasicPermissionEnum.CREATE);
    assertThat(result.isRequired()).isTrue();
  }

  @Test
  public void shouldUseClassAnnotationOnly() throws Exception {
    Method method = TestClassWithAnnotation.class.getMethod("testMethod");
    AuthAnnotationDataDto result =
        (AuthAnnotationDataDto) annotationDataMethod.invoke(authInterceptor, method);

    assertThat(result).isNotNull();
    assertThat(result.getModule()).isEqualTo(BasicModuleEnum.USER);
    assertThat(result.getPermission()).isEqualTo(BasicPermissionEnum.ANY);
    assertThat(result.isRequired()).isTrue();
  }

  @Test
  public void shouldUseMethodWhenBothHaveDefinedValues() throws Exception {
    Method method = TestClassWithAnnotation.class.getMethod("methodOverridingClass");
    AuthAnnotationDataDto result =
        (AuthAnnotationDataDto) annotationDataMethod.invoke(authInterceptor, method);

    assertThat(result).isNotNull();
    // Method should override class
    assertThat(result.getModule()).isEqualTo(BasicModuleEnum.USER);
    assertThat(result.getPermission()).isEqualTo(BasicPermissionEnum.READ);
    assertThat(result.isRequired()).isFalse(); // Method required takes precedence
  }

  @Test
  public void shouldFallBackToClassWhenMethodHasUndefined() throws Exception {
    Method method = TestClassWithAnnotation.class.getMethod("methodWithUndefined");
    AuthAnnotationDataDto result =
        (AuthAnnotationDataDto) annotationDataMethod.invoke(authInterceptor, method);

    assertThat(result).isNotNull();
    // Should use class values since method has UNDEFINED
    assertThat(result.getModule()).isEqualTo(BasicModuleEnum.USER);
    assertThat(result.getPermission()).isEqualTo(BasicPermissionEnum.ANY);
    assertThat(result.isRequired()).isFalse(); // Method required still takes precedence
  }

  @Test
  public void shouldUseAnyWhenClassHasUndefinedAndMethodHasUndefined() throws Exception {
    Method method = TestClassWithUndefined.class.getMethod("methodWithUndefined");
    AuthAnnotationDataDto result =
        (AuthAnnotationDataDto) annotationDataMethod.invoke(authInterceptor, method);

    assertThat(result).isNotNull();
    // Both have UNDEFINED, should default to ANY
    assertThat(result.getModule()).isEqualTo(BasicModuleEnum.ANY);
    assertThat(result.getPermission()).isEqualTo(BasicPermissionEnum.ANY);
    assertThat(result.isRequired()).isFalse(); // Method required still takes precedence
  }

  @Test
  public void shouldUseAnyWhenClassHasUndefinedAndNoMethodAnnotation() throws Exception {
    Method method = TestClassWithUndefined.class.getMethod("testMethod");
    AuthAnnotationDataDto result =
        (AuthAnnotationDataDto) annotationDataMethod.invoke(authInterceptor, method);

    assertThat(result).isNotNull();
    // Class has UNDEFINED, should default to ANY
    assertThat(result.getModule()).isEqualTo(BasicModuleEnum.ANY);
    assertThat(result.getPermission()).isEqualTo(BasicPermissionEnum.ANY);
    assertThat(result.isRequired()).isTrue();
  }

  @Test
  public void shouldUseMethodWhenClassHasUndefined() throws Exception {
    Method method = TestClassWithUndefined.class.getMethod("methodWithDefinedValues");
    AuthAnnotationDataDto result =
        (AuthAnnotationDataDto) annotationDataMethod.invoke(authInterceptor, method);

    assertThat(result).isNotNull();
    // Method has defined values, class has UNDEFINED, should use method
    assertThat(result.getModule()).isEqualTo(BasicModuleEnum.USER);
    assertThat(result.getPermission()).isEqualTo(BasicPermissionEnum.CREATE);
    assertThat(result.isRequired()).isTrue();
  }

  @Test
  public void shouldUseMethodUndefinedModuleButClassDefinedPermission() throws Exception {
    Method method = TestClassWithAnnotation.class.getMethod("methodWithPartialUndefined");
    AuthAnnotationDataDto result =
        (AuthAnnotationDataDto) annotationDataMethod.invoke(authInterceptor, method);

    assertThat(result).isNotNull();
    // Method module is UNDEFINED, should use class module (USER)
    // Method permission is defined (UPDATE), should use method permission
    assertThat(result.getModule()).isEqualTo(BasicModuleEnum.USER);
    assertThat(result.getPermission()).isEqualTo(BasicPermissionEnum.UPDATE);
    assertThat(result.isRequired()).isTrue();
  }

  @Test
  public void shouldUseClassModuleButMethodPermission() throws Exception {
    Method method = TestClassWithAnnotation.class.getMethod("methodWithUndefinedModuleOnly");
    AuthAnnotationDataDto result =
        (AuthAnnotationDataDto) annotationDataMethod.invoke(authInterceptor, method);

    assertThat(result).isNotNull();
    // Method module is UNDEFINED, should use class module (USER)
    // Method permission is UNDEFINED, should use class permission (ANY)
    assertThat(result.getModule()).isEqualTo(BasicModuleEnum.USER);
    assertThat(result.getPermission()).isEqualTo(BasicPermissionEnum.ANY);
    assertThat(result.isRequired()).isTrue();
  }

  @Test
  public void shouldUseClassModuleAndMethodPermissionWhenClassHasUndefinedPermission()
      throws Exception {
    Method method = TestClassWithUndefinedPermission.class.getMethod("methodWithCreatePermission");
    AuthAnnotationDataDto result =
        (AuthAnnotationDataDto) annotationDataMethod.invoke(authInterceptor, method);

    assertThat(result).isNotNull();
    // Class module is USER, method module is UNDEFINED → use class module (USER)
    // Class permission is UNDEFINED, method permission is CREATE → use method permission (CREATE)
    assertThat(result.getModule()).isEqualTo(BasicModuleEnum.USER);
    assertThat(result.getPermission()).isEqualTo(BasicPermissionEnum.CREATE);
    assertThat(result.isRequired()).isTrue();
  }

  // Test classes with various annotation configurations

  @SuppressWarnings("unused")
  private static class TestClassNoAnnotations {
    public void testMethod() {}

    @BasicAuth(module = BasicModuleEnum.USER, permission = BasicPermissionEnum.CREATE)
    public void methodWithAnnotation() {}
  }

  @BasicAuth(module = BasicModuleEnum.USER, permission = BasicPermissionEnum.ANY)
  @SuppressWarnings("unused")
  private static class TestClassWithAnnotation {
    public void testMethod() {}

    @BasicAuth(module = BasicModuleEnum.USER, permission = BasicPermissionEnum.READ,
        required = false)
    public void methodOverridingClass() {}

    @BasicAuth(module = BasicModuleEnum.UNDEFINED, permission = BasicPermissionEnum.UNDEFINED,
        required = false)
    public void methodWithUndefined() {}

    @BasicAuth(module = BasicModuleEnum.UNDEFINED, permission = BasicPermissionEnum.UPDATE)
    public void methodWithPartialUndefined() {}

    @BasicAuth(module = BasicModuleEnum.UNDEFINED, permission = BasicPermissionEnum.UNDEFINED)
    public void methodWithUndefinedModuleOnly() {}
  }

  @BasicAuth(module = BasicModuleEnum.UNDEFINED, permission = BasicPermissionEnum.UNDEFINED)
  @SuppressWarnings("unused")
  private static class TestClassWithUndefined {
    public void testMethod() {}

    @BasicAuth(module = BasicModuleEnum.UNDEFINED, permission = BasicPermissionEnum.UNDEFINED,
        required = false)
    public void methodWithUndefined() {}

    @BasicAuth(module = BasicModuleEnum.USER, permission = BasicPermissionEnum.CREATE)
    public void methodWithDefinedValues() {}
  }

  @BasicAuth(module = BasicModuleEnum.USER, permission = BasicPermissionEnum.UNDEFINED)
  private static class TestClassWithUndefinedPermission {
    @BasicAuth(module = BasicModuleEnum.UNDEFINED, permission = BasicPermissionEnum.CREATE)
    public void methodWithCreatePermission() {}
  }

}
