package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.mbean.GlobalEntityLists;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

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

    private TreeNode selAtcTree;

    public TreeNode getSelAtcTree() {
        if (selAtcTree == null) {
            populateSelAtcTree();
        }
        return selAtcTree;
    }

    private void populateSelAtcTree() {
        selAtcTree = new DefaultTreeNode("selAtcTree", null);
        selAtcTree.setExpanded(true);
        if (atc != null) {
            List<Atc> parentList = atc.getParentsTreeList(true);
            TreeNode[] nodes = new TreeNode[parentList.size()];
            for (int i = 0; i < parentList.size(); i++) {
                if (i == 0) {
                    nodes[i] = new DefaultTreeNode(parentList.get(i).getAtcCode() + ": " + parentList.get(i).getAtcName(), selAtcTree);
                    nodes[i].setExpanded(true);
                } else {
                    nodes[i] = new DefaultTreeNode(parentList.get(i).getAtcCode() + ": " + parentList.get(i).getAtcName(), nodes[i - 1]);
                    nodes[i].setExpanded(true);
                }
            }
        }
    }

    public void updateAtc() {
        populateSelAtcTree();
    }

    public List<Atc> completeAtcNames(String query) {
        return JsfUtils.completeSuggestions(query, globalEntityLists.getAtcs());
    }

    public List<Atc> completeAtcCodes(String query) {
        List<Atc> suggestions = new ArrayList<Atc>();

        if (query == null || query.equalsIgnoreCase(""))
            return globalEntityLists.getAtcs();

        for (Atc eachAtc : globalEntityLists.getAtcs()) {
            if (eachAtc.getAtcCode().toLowerCase().startsWith(query.toLowerCase()))
                suggestions.add(eachAtc);
        }
        System.out.println("Suggestions size == " + suggestions.size());
        return suggestions;
    }


}
