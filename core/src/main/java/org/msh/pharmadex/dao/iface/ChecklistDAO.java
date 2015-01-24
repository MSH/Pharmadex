package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */

public interface ChecklistDAO extends JpaRepository<Checklist, Long> {

    public List<Checklist> findByHeader(boolean header);

    //Used in Ethiopia
    public List<Checklist> findByGenMedAndRecognizedMed(boolean genMed, boolean sra);

    //Used in Ethiopia
    public List<Checklist> findByNewMed(boolean newMed);

    public List<Checklist> findByGenMedAndHeader(boolean genMed, boolean header);

    public List<Checklist> findByNewMedAndHeader(boolean newMed, boolean header);

    public List<Checklist> findByHeaderAndRecognizedMed(boolean header, boolean recognizedMed);


}

