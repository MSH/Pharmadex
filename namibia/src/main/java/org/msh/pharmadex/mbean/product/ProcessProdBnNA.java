package org.msh.pharmadex.mbean.product;

import static org.msh.pharmadex.domain.enums.RegState.FEE;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.CommentService;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ReviewService;
import org.msh.pharmadex.util.JsfUtils;




/**
 * @author Admin
 *
 */
@ManagedBean
@ViewScoped
public class ProcessProdBnNA implements Serializable {
   
	@ManagedProperty(value = "#{processProdBn}")
    public ProcessProdBn processProdBn;
    @ManagedProperty(value = "#{userSession}")
    public UserSession userSession;
    
    @ManagedProperty(value = "#{prodApplicationsService}")
    public ProdApplicationsService prodApplicationsService;
    @ManagedProperty(value = "#{reviewService}")
    public ReviewService reviewService;
    
    @ManagedProperty(value = "#{commentService}")
    public CommentService commentService;

    private String changedFields;
   // protected boolean displayVerify = false;
    //protected boolean displayScreen = false;
    private boolean disableVerify = true;
    private boolean prescreened = false;
    private boolean showFeedBackButton;
    private List<ProdApplications> allAncestors;
    
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
          allAncestors = getProdApplicationsService().getAllAncestor(prod);
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

      public ProdApplicationsService getProdApplicationsService() {
          return prodApplicationsService;
      }

      public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
          this.prodApplicationsService = prodApplicationsService;
      }
      public ReviewService getReviewService() {
  		return reviewService;
  	}
  	public void setReviewService(ReviewService reviewService) {
  		this.reviewService = reviewService;
  	}
  	
	public CommentService getCommentService() {
		return commentService;
	}
	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}
	/**
	 * Issues #2339
	 * Expiry date should be calculated as registration date + 365*5 (days)
	 */
	public void dateChange() {
		int countDay = 365*5;
		getProcessProdBn().getProdApplications().setRegExpiryDate(JsfUtils.addDays(getProcessProdBn().getProdApplications().getRegistrationDate(), countDay));
	}
	
	public boolean isDisableVerify() {
		disableVerify = true;
		ProdApplications prodApp = getProcessProdBn().getProdApplications();
		if(prodApp == null)
			return disableVerify;
		
		if(userSession.isStaff()){
			if(prodApp.getRegState().ordinal() < RegState.SCREENING.ordinal()){
				disableVerify = false; // edit tab
			}
		}
		
		return disableVerify;
	}
	
	public boolean isPrescreened() {
		prescreened = false;
		ProdApplications prodApp = getProcessProdBn().getProdApplications();
		if(prodApp == null)
			return prescreened;
		
		if(userSession.isStaff()){
			if (prodApp.getRegState().equals(RegState.FOLLOW_UP) || 
					prodApp.getRegState().equals(RegState.VERIFY))
				prescreened = true;
		}
		
		return prescreened;
	}

	public void setPrescreened(boolean prescreened) {
		this.prescreened = prescreened;
	}
	
	public void changeStatusListener() {
		try {
			ProdApplications prodApp = getProcessProdBn().getProdApplications();
			if (prodApp.getRegState().equals(RegState.NEW_APPL)) {
				if (prodApp.isFeeReceived()) {
					getProcessProdBn().getTimeLine().setRegState(FEE);
					getProcessProdBn().addTimeline();
				}
			}
			if (prodApp.getRegState().equals(RegState.FEE)) {
				if (prodApp.isApplicantVerified() && prodApp.isProductVerified() && prodApp.isDossierReceived()) {
					getProcessProdBn().getTimeLine().setRegState(RegState.VERIFY);
					getProcessProdBn().addTimeline();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}