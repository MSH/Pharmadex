package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface AttachmentDAO extends JpaRepository<Attachment, Long> {

    public List<Attachment> findByProdApplications_Id(Long prodApplications_Id);
}
