package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.ApplicantDAO;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.dao.iface.*;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.YesNoNA;
import org.msh.pharmadex.mbean.product.ProdTable;
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
    @Autowired
    private WorkspaceDAO workspaceDAO;
    @Autowired
    private PricingDAO pricingDAO;
    @Autowired
    private DrugPriceDAO drugPriceDAO;

    public List<ProdTable> findAllRegisteredProduct() {
        return productDAO.findProductsByState(RegState.REGISTERED);
    }

    @Transactional
    public Product updateProduct(Product prod) {
//        prod.getProdApplications().setApplicant(applicantDAO.findApplicant(prod.getApplicant().getApplcntId()));
//        prod.getProdApplications().setUser(userService.findUser(prod.getProdApplications().getUser().getUserId()));
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

    public RetObject validateProduct(ProdApplications prodApplications) {
        RetObject retObject = new RetObject();
        List<String> issues = new ArrayList<String>();
        Product product = prodApplications.getProduct();
        boolean issue = false;
        try {
            Workspace workspace = workspaceDAO.findAll().get(0);
            if(workspace.getName().equals("Ethiopia")){
                if (prodApplications.getPrescreenBankName().equalsIgnoreCase("") || prodApplications.getPrescreenfeeSubmittedDt() == null) {
                    issues.add("no_fee");
                    issue = true;
                }
            }else {
                if (prodApplications.getBankName().equalsIgnoreCase("") || prodApplications.getFeeSubmittedDt() == null) {
                    issues.add("no_fee");
                    issue = true;
                }
            }

            if (prodApplications.getApplicant() == null) {
                issues.add("no_applicant");
                issue = true;
            }
//            if (product.getExcipients() == null || product.getExcipients().size() < 1) {
//                issues.add("no_excipient");
//                issue = true;
//            }
            if (product.getInns() == null || product.getInns().size() < 1) {
                issues.add("no_inns");
                issue = true;
            }
            if (product.getShelfLife() == null || product.getShelfLife().equals("")) {
                issues.add("no_shelflife");
                issue = true;
            }
            if (product.getPosology() == null || product.getPosology().equals("")) {
                issues.add("no_posology");
                issue = true;
            }
            if (product.getIndications() == null || product.getIndications().equals("")) {
                issues.add("no_indications");
                issue = true;
            }
//            if (product.getIngrdStatment() == null || product.getIngrdStatment().equals("")) {
//                issues.add("no_ingrdStatment");
//                issue = true;
//            }
            if (product.getProdCompanies() == null || product.getProdCompanies().size() < 1) {
                issues.add("no_manufacturer");
                issue = true;
            }else{
                List<ProdCompany> prodCompanies = product.getProdCompanies();
                boolean finProdManuf = false;
                for(ProdCompany pc : prodCompanies){
                    if(pc.getCompanyType().equals(CompanyType.FIN_PROD_MANUF)) {
                        finProdManuf = true;
                        break;
                    }else {
                        finProdManuf = false;
                    }
                }
                if(!finProdManuf) {
                    issues.add("no_fin_prod_manuf");
                    issue = true;
                }

            }


            List<ProdAppChecklist> prodAppChkLst = prodApplicationsService.findAllProdChecklist(prodApplications.getId());
            //List<ProdAppChecklist> prodAppChkLst = prodApplications.getProdAppChecklists();
            if (prodAppChkLst != null) {
                for (ProdAppChecklist prodAppChecklist : prodAppChkLst) {
                    //if (prodAppChecklist.getChecklist().isHeader()&&(!prodAppChecklist.getValue().toString()==null)) {
                    if (prodAppChecklist.getChecklist().isHeader()){
                        if (prodAppChecklist.getValue()==null){
                            issues.add("checklist_incomplete");
                            issue = true;
                            break;
                        }
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

    public RetObject findDrugPriceByProd(Long prodID) {
        RetObject retObject;

        try {
            retObject = new RetObject("persist", pricingDAO.findByProduct_Id(prodID));
        }catch(Exception ex){
            ex.printStackTrace();
            retObject = new RetObject(ex.getMessage(), null);
        }
        return retObject;
    }

    public DrugPrice saveDrugPrice(DrugPrice selectedDrugPrice) {
         return drugPriceDAO.save(selectedDrugPrice);

    }

    public Pricing savePricing(Pricing pricing) {
        return pricingDAO.save(pricing);
    }

    public List<ProdTable> findSuspendedProducts() {
        return productDAO.findProductsByState(RegState.SUSPEND);
    }

    public List<ProdTable> findRevokedProds() {
        return productDAO.findProductsByState(RegState.CANCEL);

    }
}
