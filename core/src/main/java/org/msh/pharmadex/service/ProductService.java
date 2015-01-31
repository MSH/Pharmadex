package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.ApplicantDAO;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.dao.iface.AtcDAO;
import org.msh.pharmadex.dao.iface.InnDAO;
import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
@Transactional
public class ProductService implements Serializable {


    private static final long serialVersionUID = -5511467617579154680L;
    @Autowired
    ApplicantDAO applicantDAO;
    @Autowired
    UserService userService;
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private InnDAO innDAO;
    @Autowired
    private AtcDAO atcDAO;
    @Autowired
    private ProdApplicationsService prodApplicationsService;

    public List<Product> findAllRegisteredProduct() {
        return productDAO.findRegProducts();
    }

    @Transactional
    public Product updateProduct(Product prod) {
        prod.setApplicant(applicantDAO.findApplicant(prod.getApplicant().getApplcntId()));
        prod.getProdApplications().setUser(userService.findUser(prod.getProdApplications().getUser().getUserId()));
        return productDAO.updateProduct(prod);
    }


    public List<Product> findProductByFilter(ProductFilter filter) {
        HashMap<String, Object> params = filter.getFilters();
        return productDAO.findProductByFilter(params);

    }

    public List<Atc> findAtcsByProduct(Long id) {
        return productDAO.findAtcsByProduct(id);
    }


    /* Eager fetches the product details
    *  @Param long id product id
    *
    * */
    @Transactional
    public Product findProduct(Long prodId) {
        Product prod = productDAO.findProductEager(prodId);
        return prod;
    }

    public RetObject validateProduct(Product product) {
        RetObject retObject = new RetObject();
        List<String> issues = new ArrayList<String>();
        try {
            boolean issue = false;
            if (product.getApplicant() == null) {
                issues.add("no_applicant");
                issue = true;
            }
            if (product.getExcipients() == null || product.getExcipients().size() < 1) {
                issues.add("no_excipient");
                issue = true;
            }
            if (product.getInns() == null || product.getInns().size() < 1) {
                issues.add("no_inns");
                issue = true;
            }
            if (product.getProdCompanies() == null || product.getProdCompanies().size() < 1) {
                issues.add("no_manufacturer");
                issue = true;
            }else{
                List<ProdCompany> prodCompanies = product.getProdCompanies();
                boolean finProdManuf = false;
                for(ProdCompany pc : prodCompanies){
                    if(pc.getCompanyType().equals(CompanyType.FIN_PROD_MANUF))
                        finProdManuf = true;
                    else
                        finProdManuf = false;
                    if(!finProdManuf) {
                        issues.add("no_fin_prod_manuf");
                        issue = true;
                    }

                }
            }
            if (product.getProdApplications().getPrescreenBankName().equalsIgnoreCase("") || product.getProdApplications().getPrescreenfeeSubmittedDt().equals(null)) {
                issues.add("no_fee");
                issue = true;
            }
            List<ProdAppChecklist> prodAppChkLst = product.getProdApplications().getProdAppChecklists();
            if (prodAppChkLst != null) {
                for (ProdAppChecklist prodAppChecklist : prodAppChkLst) {
                    if (prodAppChecklist.getChecklist().isHeader()&&!prodAppChecklist.isValue()) {
                        issues.add("checklist_incomplete");
                        issue = true;
                        break;
                    }
                }
            } else {
                issues.add("checklist_incomplete");
                issue = true;
            }

            if (issue) {
                retObject.setObj(issues);
                retObject.setMsg("error");
            } else {
                retObject.setMsg("persist");
                retObject.setObj(null);
            }

            return retObject;
        } catch (Exception ex) {
            ex.printStackTrace();
            retObject.setMsg("error");
            issues.add(ex.getMessage());
            retObject.setObj(issues);
            return retObject;

        }


    }
}
