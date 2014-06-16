package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.List;

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
