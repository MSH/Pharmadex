package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.ProdInn;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */

public interface ProdInnDAO extends CrudRepository<ProdInn, Long> {

    public List<ProdInn> findByProduct_Id(Long product_Id);


}

