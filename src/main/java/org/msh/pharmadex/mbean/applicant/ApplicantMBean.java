package org.msh.pharmadex.mbean.applicant;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ApplicantType;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("request")
public class ApplicantMBean implements Serializable{
    private static final long serialVersionUID = -7233445025890580011L;
    private Applicant selectedApplicant;
    private List<Applicant> allApplicant;
    private List<Applicant> filteredApplicant;
    private boolean showAdd = false;
    private ApplicantType[] appType;
    private User user;
    private List<Country> countries;

    @Autowired
    ApplicantService applicantService;

    @Autowired
    CountryService countryService;

    @Autowired
    private UserSession userSession;

    public List<ApplicantType> getAppType() {
        return Arrays.asList(ApplicantType.values());
    }

    @PostConstruct
    private void init(){
        selectedApplicant = new Applicant();
        selectedApplicant.getAddress().setCountry(new Country());
        user = userSession.getLoggedInUserObj();
        selectedApplicant.setContactName(user!=null?user.getName():null);
        selectedApplicant.setEmail(user!=null?user.getEmail():null);
    }

    public void onRowSelect(){
        System.out.println("inside onrowselect");
        setShowAdd(true);
        System.out.println("inside onrowselect");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage("Successful", "Selected " + selectedApplicant.getAppName()));
    }

    public String saveApp(){
        selectedApplicant.setSubmitDate(new Date());
        selectedApplicant.setAppType(null);
        if(applicantService.saveApp(selectedApplicant, userSession.getLoggedInUserObj())){
            selectedApplicant = new Applicant();
            setShowAdd(false);
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            WebUtils.setSessionAttribute(request, "applicantMBean", null);
            return "/public/applicantlist.faces";
        }else{
            return null;
        }
    }

//    @PostConstruct
//    private void init(){
//        if(userSession!=null&&userSession.getLoggedInUserObj()!=null&&selectedApplicant==null){
//            selectedApplicant = new applicant();
//            System.out.print("insisde initialization ");
//            System.out.print("insisde initialization ");
//            List<User> users = new ArrayList<User>();
//            User user =userSession.getLoggedInUserObj();
//            users.add(user);
//            selectedApplicant.setUsers(users);
//            selectedApplicant.setEmail(user.getEmail());
//            selectedApplicant.setContactName(user.getName());
//        }
//    }

    public void editApp(){
        System.out.println("inside editApp");
    }

    public String cancelApp(){
        setShowAdd(false);
        selectedApplicant = new Applicant();
        return "/public/registrationhome.faces?redirect=true";
    }

    public Applicant getSelectedApplicant() {
//        init();
        return selectedApplicant;
    }

    public List<Applicant> getAllApplicant() {
            return applicantService.getRegApplicants();
    }

    public void setAllApplicant(List<Applicant> allApplicant) {
        this.allApplicant = allApplicant;
    }

    public void setSelectedApplicant(Applicant selectedApplicant) {
        this.selectedApplicant = selectedApplicant;
    }

    public boolean isShowAdd() {
        return showAdd;
    }

    public void setShowAdd(boolean showAdd) {
        this.showAdd = showAdd;
    }

    public List<Country> getCountries() {
        return countryService.getCountries();
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Applicant> getFilteredApplicant() {
        return filteredApplicant;
    }

    public void setFilteredApplicant(List<Applicant> filteredApplicant) {
        this.filteredApplicant = filteredApplicant;
    }
}
