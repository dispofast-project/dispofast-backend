package com.dispocol.dispofast.shared.location.infra.persistence;

import com.dispocol.dispofast.shared.location.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, String> {}
