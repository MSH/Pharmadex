package org.msh.pharmadex.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.util.RegistrationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ProdApplicationsServiceNA extends ProdApplicationsService {
	   @Autowired
	    private ProdApplicationsDAO prodApplicationsDAO;
	   @Transactional   
	public List<ProdApplications> getAllAncestor(ProdApplications proda) {
		ProdApplications an=proda;
        List<ProdApplications> ans = null;
        if (an.getParentApplication()!=null){
            ans=new ArrayList<ProdApplications> ();
            while (an!=null) {
            if (an.getParentApplication()==null)  return ans;
                an=	prodApplicationsDAO.findProdApplications(an.getParentApplication().getId());
                  if (an!=null){
                		Hibernate.initialize(an);
                	  ans.add((ProdApplications) an);
                    an=an.getParentApplication();
                }
            }
        }
		return ans;
	}
	   
	   @Override
	    public String generateAppNo(ProdApplications prodApp) {
	        RegistrationUtil registrationUtil = new RegistrationUtil();
	        String appType;
	        if (prodApp.getProdAppType().equals(ProdAppType.NEW_CHEMICAL_ENTITY)) {
	            appType = "NMR";
	        }
	        if (prodApp.getProdAppType().equals(ProdAppType.GENERIC) ) {
	            appType = "GEN";
	        }
	        if (prodApp.getProdAppType().equals(ProdAppType.RENEW)) {
	            appType = "REN";
	        }
	        if (prodApp.getProdAppType().equals(ProdAppType.VARIATION)) {
	            appType = "VAR";
	        }


	        return registrationUtil.generateAppNo(prodApp.getId(), "NMR");

	    }
}
