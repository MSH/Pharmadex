package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.SampleTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by utkarsh on 12/19/14.
 */
public interface SampleTestDAO extends JpaRepository<SampleTest, Long> {

    public List<SampleTest> findByProdApplications_Id(Long prodApplications_Id);

}
