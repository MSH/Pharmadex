package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface MailDAO extends JpaRepository<Mail, Long> {

    public List<Mail> findByProdApplications_IdOrderByDateDesc(Long prodApplications_Id);

}
