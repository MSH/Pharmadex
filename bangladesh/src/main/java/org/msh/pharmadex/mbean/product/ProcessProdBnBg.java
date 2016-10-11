package org.msh.pharmadex.mbean.product;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.ProdApplicationsServiceAdd;
import org.msh.pharmadex.service.ReviewService;




/**
 * @author Admin
 *
 */
@ManagedBean
@ViewScoped
public class ProcessProdBnBg implements Serializable {
   

	@ManagedProperty(value = "#{processProdBn}")
    public ProcessProdBn processProdBn;
    @ManagedProperty(value = "#{userSession}")
    public UserSession userSession;
    
    @ManagedProperty(value = "#{prodApplicationsServiceAdd}")
    public ProdApplicationsServiceAdd prodApplicationsServiceAdd;
    @ManagedProperty(value = "#{reviewService}")
    public ReviewService reviewService;

    private String changedFields;
    protected boolean displayVerify = false;
   // private Logger logger = LoggerFactory.getLogger(ProcessProdBn.class);
    //private JasperPrint jasperPrint;
   private boolean showFeedBackButton;
    private List<ProdApplications> allAncestors;
    private String backTo = "";
    private FacesContext facesContext = FacesContext.getCurrentInstance();

    @PostConstruct
    private void init() {
        //Long id = Scrooge.beanParam("Id");
   	    ProdApplications pa= processProdBn.getProdApplications();
        if (pa!=null)changedFields=pa.getAppComment();
    	if (changedFields==null) changedFields="";
    }
    public boolean isFieldChanged(String fieldname){
  	  //получим список из review_info.changedFields  если в списке нет, то false
   //   	 String fieldname = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("fieldvalue");
      	   
      	if (changedFields.contains(fieldname)) return true;
      	return false;
      }
      
     public boolean findInnChanged(){
    	  //получим список из review_info.changedFields  если в списке нет, то false
         	if (changedFields.contains("inns")) return true;
        	return false;
      }
     
     public boolean findExcipientChanged(){
  	  	  //получим список из review_info.changedFields  если в списке нет, то false
  	       	if (changedFields.contains("excipients"))
                  return true;
              else
  	      	    return false;
  	    }
     public boolean findAtcChanged(){
  	  	  //получим список из review_info.changedFields  если в списке нет, то false
  	       	if (changedFields.contains("Atc")) return true;
  	      	return false;
  	    }
      
     public boolean findCompaniesChanged(){
 		if (changedFields.contains("ProdCompanies")) return true;
       	return false;
    }
     
     public void setShowFeedBackButton(boolean showFeedBackButton) {
          this.showFeedBackButton = showFeedBackButton;
      }

      public List<ProdApplications> getAllAncestors(){
          if (allAncestors==null){
          ProdApplications prod = processProdBn.getProdApplications();
          allAncestors = getProdApplicationsServiceAdd().getAllAncestor(prod);
          }
          return allAncestors;
      }


      public void setAllAncestors(List<ProdApplications> allAncestors) {
          this.allAncestors = allAncestors;
      }

      public ProcessProdBn getProcessProdBn() {
          return processProdBn;
      }

      public void setProcessProdBn(ProcessProdBn processProdBn) {
          this.processProdBn = processProdBn;
      }

      public UserSession getUserSession() {
          return userSession;
      }

      public void setUserSession(UserSession userSession) {
          this.userSession = userSession;
      }

      public ProdApplicationsServiceAdd getProdApplicationsServiceAdd() {
          return prodApplicationsServiceAdd;
      }

      public void setProdApplicationsServiceAdd(ProdApplicationsServiceAdd prodApplicationsServiceNA) {
          this.prodApplicationsServiceAdd = prodApplicationsServiceNA;
      }
      public ReviewService getReviewService() {
  		return reviewService;
  	}
  	public void setReviewService(ReviewService reviewService) {
  		this.reviewService = reviewService;
  	}
}