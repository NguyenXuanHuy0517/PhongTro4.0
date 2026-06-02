package com.project.backend.service.host;

import com.project.backend.entity.Room;
import com.project.backend.entity.User;
import com.project.backend.dto.host.contract.ContractInviteCreateDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class ContractInvitationTokenService {

    @Value("${rental.invite.secret}")
    private String inviteSecret;

    @Value("${rental.invite.expiration-hours:72}")
    private long inviteExpirationHours;

    public IssuedInvitation issueInvitation(User host, Room room, ContractInviteCreateDTO request) {
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiresAt = issuedAt.plusHours(inviteExpirationHours);

        String inviteCode = Jwts.builder()
                .setSubject("rental-contract-invitation")
                .claim("hostId", host.getUserId())
                .claim("roomId", room.getRoomId())
                .claim("startDate", request.getStartDate().toString())
                .claim("endDate", request.getEndDate().toString())
                .claim("actualRentPrice", request.getActualRentPrice().toPlainString())
                .claim("elecPriceOverride", request.getElecPriceOverride() != null
                        ? request.getElecPriceOverride().toPlainString()
                        : null)
                .claim("waterPriceOverride", request.getWaterPriceOverride() != null
                        ? request.getWaterPriceOverride().toPlainString()
                        : null)
                .claim("penaltyTerms", normalizeText(request.getPenaltyTerms()))
                .setIssuedAt(toDate(issuedAt))
                .setExpiration(toDate(expiresAt))
                .signWith(getSignKey())
                .compact();

        return new IssuedInvitation(inviteCode, expiresAt);
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Date toDate(LocalDateTime value) {
        return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(inviteSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public record IssuedInvitation(String inviteCode, LocalDateTime expiresAt) {
    }
}
