package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Atc;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface CustomAtcDao {
    public List<Atc> findAtcByProduct(Long prodId);
}
