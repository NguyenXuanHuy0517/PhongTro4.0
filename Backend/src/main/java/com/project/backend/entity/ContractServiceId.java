package com.project.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractServiceId implements Serializable {
    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "service_id")
    private Long serviceId;
}
