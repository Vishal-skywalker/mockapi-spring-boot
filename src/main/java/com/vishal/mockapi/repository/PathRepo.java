package com.vishal.mockapi.repository;

import com.vishal.mockapi.entity.Path;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface PathRepo extends CrudRepository<Path, Long> {

    Optional<Path> findByResourceUuidAndId(String uuid, Long id);

    Optional<Path> findByResourceUuidAndPathNameAndMethod(String uuid, String path, String method);

}
