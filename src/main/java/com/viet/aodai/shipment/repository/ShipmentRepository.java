package com.viet.aodai.shipment.repository;

import com.viet.aodai.shipment.domain.entity.Shipment;
import com.viet.aodai.shipment.repository.custom.ShipmentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long>, ShipmentRepositoryCustom {
}
