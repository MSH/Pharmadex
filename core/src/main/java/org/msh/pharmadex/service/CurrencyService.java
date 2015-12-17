package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.CurrencyDAO;
import org.msh.pharmadex.domain.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class CurrencyService implements Serializable {

    @Autowired
    private CurrencyDAO currencyDAO;

    @Autowired
    private GlobalEntityLists globalEntityLists;

    public Currency findCurrency(Long id) {
        return currencyDAO.findOne(id);
    }

    public List<Currency> findAllCurrency() {
        return currencyDAO.findAll();
    }


}
