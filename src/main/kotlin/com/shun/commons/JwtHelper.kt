package com.shun.commons

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter


class JwtHelper {

    companion object {
        fun parseJWT(jsonWebToken: String, base64Security: String): Claims? {
            try {
                val claims = Jwts.parser()
                        .setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))
                        .parseClaimsJws(jsonWebToken).body
                return claims
            } catch (ex: Exception) {
                return null
            }
        }

        fun createJWT(name: String, userId: String, role: String, audience: String, issuer: String, TTLMillis: Long, base64Security: String): String {

            val signatureAlgorithm = SignatureAlgorithm.HS256

            val nowMillis = System.currentTimeMillis()

            val now = Date(nowMillis)

            //生成签名秘钥
            val apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Security)

            val signingKey: Key = SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.jcaName)

            //添加构成JWT的参数
            val builder = Jwts.builder().setHeaderParam("type", "JWT")
                    .claim("role", role)
                    .claim("unique_name", name)
                    .claim("userid", userId)
                    .setIssuer(issuer)
                    .setAudience(audience)
                    .signWith(signatureAlgorithm, signingKey)

            //添加token过期时间
            if (TTLMillis >= 0) {
                val expMillis = nowMillis + TTLMillis
                val expire = Date(expMillis)
                builder.setExpiration(expire).setNotBefore(now)
            }
            //生成JWT
            return builder.compact()
        }

    }
}