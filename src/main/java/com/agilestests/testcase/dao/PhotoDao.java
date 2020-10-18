package com.agilestests.testcase.dao;

import com.agilestests.testcase.models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoDao extends JpaRepository<Photo, Long>, JpaSpecificationExecutor<Photo> {
}
