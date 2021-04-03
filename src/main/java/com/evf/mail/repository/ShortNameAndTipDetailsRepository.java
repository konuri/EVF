package com.evf.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evf.mail.domain.ShortNameAndTipDetails;

@Repository
public interface ShortNameAndTipDetailsRepository extends JpaRepository<ShortNameAndTipDetails, String> {

}
