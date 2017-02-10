package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.ReviewComment;
import org.msh.pharmadex.domain.lab.SampleComment;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by utkarsh on 12/19/14.
 */
public interface SampleTestDAO extends JpaRepository<SampleTest, Long> {

    public List<SampleTest> findByProdApplications_Id(Long prodApplications_Id);

    @Query("select rc from SampleComment rc left join fetch rc.user where rc.sampleTest.id = ?1 ")
    List<SampleComment> findSampleCommentsBySampleTest_id(Long id);
}
