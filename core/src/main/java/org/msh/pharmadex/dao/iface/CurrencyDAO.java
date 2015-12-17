package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by utkarsh on 12/19/14.
 */
public interface CurrencyDAO extends JpaRepository<Currency, Long> {


}
