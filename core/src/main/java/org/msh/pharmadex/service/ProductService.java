package org.msh.pharmadex.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.msh.pharmadex.dao.ApplicantDAO;
import org.msh.pharmadex.dao.InnDAO;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.dao.iface.AtcDAO;
import org.msh.pharmadex.dao.iface.DrugPriceDAO;
import org.msh.pharmadex.dao.iface.PricingDAO;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.Atc;
import org.msh.pharmadex.domain.DrugPrice;
import org.msh.pharmadex.domain.Pricing;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.Workspace;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    public String getMinRegistrationYearInDB() {
    	return productDAO.getMinRegistrationYearInDB();
    }
    
    public List<ProdTable> findRegisteredProduct(Long innId, Date start, Date end) {
        return productDAO.findProductsByFilter(RegState.REGISTERED, innId, start, end);
    }

    @Transactional
    public Product updateProduct(Product prod) {
//        prod.getProdApplications().setApplicant(applicantDAO.findApplicant(prod.getApplicant().getApplcntId()));
//        prod.getProdApplications().setUser(userService.findUser(prod.getProdApplications().getUser().getUserId()));
        String manufName = prod.getManufName();
        if (manufName==null){
            List<ProdCompany> companyList = prod.getProdCompanies();
            if (companyList!=null){
                for(ProdCompany company:companyList){
                    if (company.getCompanyType().equals(CompanyType.FIN_PROD_MANUF)){
                        manufName = company.getCompany().getContactName();
                        prod.setManufName(manufName);
                        break;
                    }

                }
            }
        }
        return productDAO.updateProduct(prod);
    }

    public List<Product> findProductByFilter(ProductFilter filter) {
        HashMap<String, Object> params = filter.getFilters();
        return productDAO.findProductByFilter(params);
    }

    public Product findOneByName(String name){
        List<Product> found = productDAO.findByName(name);
        if (found!=null)
            return  found.get(0);
        return null;
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
                if (!checkFee(prodApplications)) {
                    issues.add("no_fee");
                    issue = true;
                }
            }

            if (prodApplications.getApplicant() == null) {
                issues.add("no_applicant");
                issue = true;
            }
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
            if (prodAppChkLst != null) {
                for (ProdAppChecklist prodAppChecklist : prodAppChkLst) {
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
    /**
     * Check fee
     * @param prodApplications
     * @return
     */
	public boolean checkFee(ProdApplications prodApplications) {
		String bankName = "";
		Date feeSubmited = null;
		if(prodApplications.getBankName() != null){
			bankName = prodApplications.getBankName();
			feeSubmited = prodApplications.getFeeSubmittedDt();
		}else{
			if(prodApplications.getPrescreenBankName() != null){
				bankName = prodApplications.getPrescreenBankName();
				feeSubmited = prodApplications.getPrescreenfeeSubmittedDt();
			}
		}
		return (bankName.length()>0) && (feeSubmited != null);
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

    public void removeDrugPricing(DrugPrice drprice){
    	drugPriceDAO.delete(drprice);
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
