
package com.sixsprints.core.utils;

import java.security.SecureRandom;
import java.util.Date;

import org.joda.time.DateTime;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sixsprints.core.exception.NotAuthorizedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthUtil {

  private static final JWSHeader JWT_HEADER = new JWSHeader(JWSAlgorithm.HS256);

  private static final String TOKEN_SECRET = "dV(\\2?TTG:p7N$-/";

  private static final String ISSUER = "https://www.website.com";

  private static SecureRandom random = new SecureRandom();

  private static byte[] sharedSecret = new byte[32];

  static {
    random.nextBytes(sharedSecret);
  }

  public static String decodeToken(String authHeader) throws NotAuthorizedException {
    return xor(decode(authHeader).getSubject().getBytes());
  }

  public static String createToken(String subject) {
    return createToken(subject, AppConstants.TOKEN_EXPIRY_IN_DAYS);
  }

  public static String createToken(String subject, int expiryDays) {
    JWSSigner signer = null;
    try {
      signer = new MACSigner(sharedSecret);
    } catch (KeyLengthException e) {
      log.error(e.getMessage(), e);
    }
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
      .subject(xor(subject.getBytes()))
      .issuer(ISSUER)
      .issueTime(DateTime.now().toDate())
      .expirationTime(DateTime.now().plusDays(expiryDays).toDate())
      .build();
    SignedJWT signedJWT = new SignedJWT(JWT_HEADER, claimsSet);
    try {
      signedJWT.sign(signer);
    } catch (JOSEException e) {
      log.error(e.getMessage(), e);
    }
    return signedJWT.serialize();
  }

  private static JWTClaimsSet decode(String authHeader) throws NotAuthorizedException {
    SignedJWT jwtClaimsSet = null;
    JWTClaimsSet claimsSet = null;
    try {
      jwtClaimsSet = SignedJWT.parse(authHeader);
      JWSVerifier verifier = new MACVerifier(sharedSecret);
      boolean verify = jwtClaimsSet.verify(verifier);
      if (!verify) {
        throw NotAuthorizedException.childBuilder().error("Unable to verify the token").build();
      }
      claimsSet = jwtClaimsSet.getJWTClaimsSet();
    } catch (Exception e) {
      throw NotAuthorizedException.childBuilder().error("Invalid Token provided!").build();
    }

    Date expiryDate = claimsSet.getExpirationTime();
    Date now = DateTime.now().toDate();

    if (now.after(expiryDate)) {
      throw NotAuthorizedException.childBuilder().error("Token expired").build();
    }
    return claimsSet;
  }

  private static String xor(final byte[] input) {
    final byte[] output = new byte[input.length];
    final byte[] secret = TOKEN_SECRET.getBytes();
    int spos = 0;
    for (int pos = 0; pos < input.length; ++pos) {
      output[pos] = (byte) (input[pos] ^ secret[spos]);
      spos += 1;
      if (spos >= secret.length) {
        spos = 0;
      }
    }
    return new String(output);
  }

}
