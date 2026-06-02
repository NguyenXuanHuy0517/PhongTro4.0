package com.project.backend.service.tenant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class TenantContractInvitationTokenService {

    @Value("${rental.invite.secret}")
    private String inviteSecret;

    public ContractInvitationPayload parseInvitation(String inviteCode) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(inviteCode)
                    .getBody();

            return new ContractInvitationPayload(
                    getLongClaim(claims, "hostId"),
                    getLongClaim(claims, "roomId"),
                    LocalDate.parse(claims.get("startDate", String.class)),
                    LocalDate.parse(claims.get("endDate", String.class)),
                    new BigDecimal(claims.get("actualRentPrice", String.class)),
                    getBigDecimalClaim(claims, "elecPriceOverride"),
                    getBigDecimalClaim(claims, "waterPriceOverride"),
                    claims.get("penaltyTerms", String.class),
                    toLocalDateTime(claims.getExpiration())
            );
        } catch (JwtException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Ma thue khong hop le hoac da het han");
        }
    }

    private Long getLongClaim(Claims claims, String name) {
        Object value = claims.get(name);
        return value == null ? null : Long.valueOf(String.valueOf(value));
    }

    private BigDecimal getBigDecimalClaim(Claims claims, String name) {
        Object value = claims.get(name);
        if (value == null || String.valueOf(value).isBlank()) {
            return null;
        }
        return new BigDecimal(String.valueOf(value));
    }

    private LocalDateTime toLocalDateTime(Date value) {
        return LocalDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault());
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(inviteSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public record ContractInvitationPayload(
            Long hostId,
            Long roomId,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal actualRentPrice,
            BigDecimal elecPriceOverride,
            BigDecimal waterPriceOverride,
            String penaltyTerms,
            LocalDateTime expiresAt
    ) {
    }
}
