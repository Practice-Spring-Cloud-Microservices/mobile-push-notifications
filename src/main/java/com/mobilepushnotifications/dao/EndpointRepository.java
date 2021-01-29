package com.mobilepushnotifications.dao;

import com.mobilepushnotifications.entity.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EndpointRepository extends JpaRepository<Endpoint, String> {

    List<Endpoint> findByUserId(int userId);

}
