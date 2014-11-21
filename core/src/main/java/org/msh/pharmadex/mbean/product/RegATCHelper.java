package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.service.GlobalEntityLists;

/**
 * Created with IntelliJ IDEA.
 * User: utkarsh
 * Date: 3/3/14
 * Time: 10:48 PM
 * To change this template use File | Settings | File Templates.
 */

public class RegATCHelper {

    private Atc atc;
    private GlobalEntityLists globalEntityLists;

    public RegATCHelper(Atc atc, GlobalEntityLists globalEntityLists) {
        this.atc = atc;
        this.globalEntityLists = globalEntityLists;
    }


}
