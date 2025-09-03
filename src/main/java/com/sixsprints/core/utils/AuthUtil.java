
package com.sixsprints.core.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

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
import com.sixsprints.core.constants.ExceptionConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthUtil {

  private static final JWSHeader JWT_HEADER = new JWSHeader(JWSAlgorithm.HS256);

  public static String decodeToken(String authHeader) throws NotAuthorizedException {
    return xor(decode(authHeader).getSubject().getBytes());
  }

  public static String createToken(String subject) {
    return createToken(subject, EnvConstants.TOKEN_EXPIRY_IN_DAYS);
  }

  public static String createToken(String subject, int expiryDays) {

    LocalDateTime now = LocalDateTime.now();
    ZonedDateTime zdt = now.atZone(ZoneId.systemDefault());
    Date nowDate = Date.from(zdt.toInstant());

    JWSSigner signer = null;
    try {
      signer = new MACSigner(EnvConstants.SHARED_SECRET);
    } catch (KeyLengthException e) {
      log.error(e.getMessage(), e);
    }
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(xor(subject.getBytes()))
        .issuer(EnvConstants.ISSUER).issueTime(nowDate)
        .expirationTime(Date.from(zdt.plusDays(expiryDays).toInstant())).build();
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
      JWSVerifier verifier = new MACVerifier(EnvConstants.SHARED_SECRET);
      boolean verify = jwtClaimsSet.verify(verifier);
      if (!verify) {
        throw NotAuthorizedException.childBuilder().error(ExceptionConstants.UNABLE_TO_VERIFY_TOKEN)
            .build();
      }
      claimsSet = jwtClaimsSet.getJWTClaimsSet();
    } catch (Exception e) {
      throw NotAuthorizedException.childBuilder().error(ExceptionConstants.INVALID_TOKEN).build();
    }

    Date expiryDate = claimsSet.getExpirationTime();
    LocalDateTime now = LocalDateTime.now();
    ZonedDateTime zdt = now.atZone(ZoneId.systemDefault());
    Date nowDate = Date.from(zdt.toInstant());

    if (nowDate.after(expiryDate)) {
      throw NotAuthorizedException.childBuilder().error(ExceptionConstants.TOKEN_EXPIRED).build();
    }
    return claimsSet;
  }

  private static String xor(final byte[] input) {
    final byte[] output = new byte[input.length];
    final byte[] secret = EnvConstants.TOKEN_SECRET.getBytes();
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
