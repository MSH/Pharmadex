package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Mail;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface MailDAO extends CrudRepository<Mail, Long>{

    public List<Mail> findByProdApplications_IdOrderByDateDesc(Long prodApplications_Id);

}
