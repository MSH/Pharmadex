package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.LicenseHolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by utkarsh on 12/19/14.
 */
public interface LicenseHolderDAO extends JpaRepository<LicenseHolder, Long> {
    public List<LicenseHolder> findByName(String name);
}
