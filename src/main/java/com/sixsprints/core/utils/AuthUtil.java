
package com.sixsprints.core.utils;

import java.text.ParseException;
import java.util.Date;

import org.joda.time.DateTime;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sixsprints.core.exception.NotAuthorizedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthUtil {

  private static final JWSHeader JWT_HEADER = new JWSHeader(JWSAlgorithm.HS256);

  private static final String TOKEN_SECRET = "dV(\\2?TTG:p7N$-/";

  private static final String ISSUER = "https://www.website.com";

  public static String decodeToken(String authHeader) throws NotAuthorizedException {
    return xor(decode(authHeader).getSubject().getBytes());
  }

  public static String createToken(String subject) {
    return createToken(subject, Constants.TOKEN_EXPIRY_IN_DAYS);
  }

  public static String createToken(String subject, int expiryDays) {
    JWTClaimsSet claim = new JWTClaimsSet();
    claim.setSubject(xor(subject.getBytes()));
    claim.setIssuer(ISSUER);
    claim.setIssueTime(DateTime.now().toDate());
    claim.setExpirationTime(DateTime.now().plusDays(expiryDays).toDate());
    JWSSigner signer = new MACSigner(TOKEN_SECRET);
    SignedJWT jwt = new SignedJWT(JWT_HEADER, claim);
    try {
      jwt.sign(signer);
    } catch (JOSEException e) {
      log.error(e.getMessage(), e);
    }

    return jwt.serialize();
  }

  private static ReadOnlyJWTClaimsSet decode(String authHeader) throws NotAuthorizedException {

    ReadOnlyJWTClaimsSet jwtClaimsSet = null;
    try {
      jwtClaimsSet = SignedJWT.parse(getSerializedToken(authHeader)).getJWTClaimsSet();
    } catch (ParseException e) {
      throw NotAuthorizedException.childBuilder().error("Invalid Token provided!").build();
    }
    Date expiryDate = jwtClaimsSet.getExpirationTime();
    Date now = DateTime.now().toDate();

    if (now.after(expiryDate)) {
      throw NotAuthorizedException.childBuilder().error("Token expired").build();
    }
    return jwtClaimsSet;
  }

  private static String getSerializedToken(String authHeader) {
    return authHeader;
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
