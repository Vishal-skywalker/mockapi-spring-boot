package com.vishal.mockapi.repository;

import com.vishal.mockapi.entity.Resource;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ResourceRepo extends CrudRepository<Resource, Long> {

    @Query("SELECT DISTINCT r FROM Resource r LEFT JOIN FETCH r.paths WHERE r.owner.id = :ownerId AND r.uuid = :uuid")
    Optional<Resource> findByOwnerIdAndUUIDWithPaths(@Param("ownerId") Long ownerId, @Param("uuid") String uuid);

    List<Resource> findByOwnerId(Long ownerId);

    List<Resource> findByOwnerIdAndUuid(Long ownerId, String uuid);

}
