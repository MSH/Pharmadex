package org.msh.pharmadex.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.CustomReviewDAO;
import org.msh.pharmadex.dao.ProductCompanyDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.util.RegistrationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ProdApplicationsServiceET extends ProdApplicationsService {
    @Autowired
    private CustomReviewDAO customReviewDAO;
    @Autowired
    private ProductCompanyDAO prodCompanyDAO;

    @Override
    public List<RegState> nextStepOptions(RegState regState, UserSession userSession, boolean reviewStatus) {
        RegState[] options = null;
        switch (regState) {
            case NEW_APPL:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.SCREENING;
                break;
            case SCREENING:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.FEE;
                break;
            case FEE:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.VERIFY;
                break;
            case VERIFY:
                options = new RegState[2];
                options[0] = RegState.FOLLOW_UP;
                options[1] = RegState.REVIEW_BOARD;
                break;
            case REVIEW_BOARD:
                if (userSession.isAdmin() || userSession.isModerator()) {
                    if (reviewStatus) {
                        options = new RegState[3];
                        options[0] = RegState.FOLLOW_UP;
                        options[1] = RegState.RECOMMENDED;
                        options[2] = RegState.NOT_RECOMMENDED;
                    } else {
                        options = new RegState[1];
                        options[0] = RegState.FOLLOW_UP;
                    }
                } else {
                    options = new RegState[1];
                    options[0] = RegState.FOLLOW_UP;
                }
                break;
            case RECOMMENDED:
                if (userSession.isAdmin() || userSession.isModerator() || userSession.isHead()) {
                    options = new RegState[1];
                    options[0] = RegState.FOLLOW_UP;
                    options[0] = RegState.REJECTED;
                }
                break;
            case REGISTERED:
                options = new RegState[3];
                options[0] = RegState.DISCONTINUED;
                options[1] = RegState.XFER_APPLICANCY;
                break;
            case FOLLOW_UP:
                options = new RegState[7];
                options[0] = RegState.FEE;
                options[1] = RegState.VERIFY;
                options[2] = RegState.SCREENING;
                options[3] = RegState.REVIEW_BOARD;
                options[4] = RegState.SCREENING;
                options[5] = RegState.REVIEW_BOARD;
                options[6] = RegState.DEFAULTED;
                break;
            case NOT_RECOMMENDED:
                options = new RegState[1];
                options[0] = RegState.REJECTED;
                break;
            case DEFAULTED:
                options = new RegState[1];
                options[0] = RegState.FOLLOW_UP;
                break;
            case REJECTED:
                options = new RegState[1];
                options[0] = RegState.FOLLOW_UP;
                break;
        }
        return Arrays.asList(options);


    }

    public List<ProdApplications> getSubmittedApplications(UserSession userSession) {
        List<ProdApplications> prodApplicationses = null;
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (userSession.isAdmin()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.FEE);
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.DEFAULTED);
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.RECOMMENDED);
            regState.add(RegState.REVIEW_BOARD);
            regState.add(RegState.SCREENING);
            regState.add(RegState.VERIFY);
            params.put("regState", regState);
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isModerator()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.FEE);
            regState.add(RegState.VERIFY);
            regState.add(RegState.REVIEW_BOARD);
            params.put("regState", regState);
            params.put("moderatorId", userSession.getLoggedINUserID());
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isReviewer()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.REVIEW_BOARD);
            params.put("regState", regState);
            if (workspaceDAO.findAll().get(0).isDetailReview()) {
                params.put("reviewer", userSession.getLoggedINUserID());
                return prodApplicationsDAO.findProdAppByReviewer(params);
            } else
                params.put("reviewerId", userSession.getLoggedINUserID());
//            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isHead()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.FEE);
            regState.add(RegState.VERIFY);
            regState.add(RegState.SCREENING);
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.REVIEW_BOARD);
            regState.add(RegState.RECOMMENDED);
            regState.add(RegState.NOT_RECOMMENDED);
            regState.add(RegState.REJECTED);

            params.put("regState", regState);
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isStaff()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.SCREENING);
            regState.add(RegState.FEE);
//            regState.add(RegState.VERIFY);
            regState.add(RegState.FOLLOW_UP);
            params.put("regState", regState);
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isCompany()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.FEE);
            regState.add(RegState.NEW_APPL);
            regState.add(RegState.DEFAULTED);
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.RECOMMENDED);
            regState.add(RegState.REVIEW_BOARD);
            regState.add(RegState.SCREENING);
            regState.add(RegState.VERIFY);
            regState.add(RegState.REGISTERED);
            regState.add(RegState.CANCEL);
            regState.add(RegState.SUSPEND);
            regState.add(RegState.DEFAULTED);
            regState.add(RegState.NOT_RECOMMENDED);
            regState.add(RegState.REJECTED);

            params.put("regState", regState);
            params.put("userId", userSession.getLoggedINUserID());
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isLab()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.VERIFY);
            regState.add(RegState.REVIEW_BOARD);
            regState.add(RegState.FOLLOW_UP);
            regState.add(RegState.RECOMMENDED);
            regState.add(RegState.NOT_RECOMMENDED);
            params.put("regState", regState);
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        } else if (userSession.isClinical()) {
            List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.VERIFY);
            regState.add(RegState.REVIEW_BOARD);
            regState.add(RegState.FOLLOW_UP);
            params.put("regState", regState);
            params.put("prodAppType", ProdAppType.NEW_CHEMICAL_ENTITY);
            prodApplicationses = prodApplicationsDAO.getProdAppByParams(params);
        }

        if (userSession.isModerator() || userSession.isReviewer() || userSession.isHead() || userSession.isLab()) {
            Collections.sort(prodApplicationses, new Comparator<ProdApplications>() {
                @Override
                public int compare(ProdApplications o1, ProdApplications o2) {
                    return o1.getPriorityDate().compareTo(o2.getPriorityDate());
                }
            });
        }

        return prodApplicationses;
    }

    public String generateRegNo() {
        int count = productDAO.findCountRegProduct();
        String regNO = String.format("%04d", count);
        String appNo = prodApp.getProdAppNo();
        appNo = appNo.substring(0, 4);
        String appType = "NMR";
        int dt = Calendar.getInstance().get(Calendar.YEAR);
        String year = "" + dt;
        regNO = regNO + appNo + appType + year;
        return regNO;

    }

    @Override
    public String generateAppNo(ProdApplications prodApp) {
        RegistrationUtil registrationUtil = new RegistrationUtil();
        String appType;
        if (prodApp.getProdAppType().equals(ProdAppType.NEW_CHEMICAL_ENTITY)) {
            appType = "NMR";
        }
        if (prodApp.getProdAppType().equals(ProdAppType.GENERIC) || prodApp.getProdAppType().equals(ProdAppType.GENERIC_NO_BE)) {
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

    public List<ProdApplications> getFeedbackApplications(UserSession userSession) {
        List<ProdApplications> prodApplicationses = null;
        HashMap<String, Object> params = new HashMap<String, Object>();
        List<ReviewStatus> revState = new ArrayList<ReviewStatus>();
        revState.add(ReviewStatus.FIR_SUBMIT);
        params.put("reviewState", revState);
        List<RegState> regState = new ArrayList<RegState>();
        regState.add(RegState.REVIEW_BOARD);
        params.put("regState", regState);
        if (!userSession.isAdmin()) {
            if (userSession.isModerator()) {
                params.put("moderatorId", userSession.getLoggedINUserID());
            } else if (userSession.isReviewer()) {
                if (workspaceDAO.findAll().get(0).isDetailReview()) {
                    params.put("reviewer", userSession.getLoggedINUserID());

                } else
                    params.put("reviewer", userSession.getLoggedINUserID());
            } else if (userSession.isCompany()) {
                params.put("userId", userSession.getLoggedINUserID());
            }
        }
        prodApplicationses = prodApplicationsDAO.findProdAppByReviewStatus(params);

        return prodApplicationses;
    }

    public List<ProdApplications> getNewVariationApp(UserSession userSession){
    	  List<ProdApplications> prodApplicationses = null;  
    	HashMap<String, Object> params = new HashMap<String, Object>();
    	    List<RegState> regState = new ArrayList<RegState>();
            regState.add(RegState.NEW_APPL);
            params.put("regState", regState);
            if (!userSession.isAdmin()) {
                if (userSession.isModerator()) {
                    params.put("moderatorId", userSession.getLoggedINUserID());
                } else if (userSession.isReviewer()) {
                    if (workspaceDAO.findAll().get(0).isDetailReview()) {
                        params.put("reviewer", userSession.getLoggedINUserID());

                    } else
                        params.put("reviewer", userSession.getLoggedINUserID());
                } else if (userSession.isCompany()) {
                    params.put("userId", userSession.getLoggedINUserID());
                }
            }
           params.put("prodAppType",ProdAppType.VARIATION);
           prodApplicationses= prodApplicationsDAO.getProdAppByParams(params);
           return prodApplicationses;

    }
    /**
     * Create review details and save it to letters (one point to find all generated documents)
     * @param prodApplications
     * @return
     */
    public String createReviewDetails(ProdApplications prodApplications) {
        prodApp = prodApplications;
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");
        Properties prop = fetchReviewDetailsProperties();
        if(prop != null){
            Product prod = prodApp.getProduct();
            try {
                JasperPrint jasperPrint;
                HashMap<String, Object> param = new HashMap<String, Object>();
                UtilsByReports utilsByReports = new UtilsByReports();
                utilsByReports.init(param, prodApp, prod);
                utilsByReports.putNotNull(UtilsByReports.KEY_PRODNAME, "", false);
                utilsByReports.putNotNull(UtilsByReports.KEY_MODERNAME, "", false);

                //TODO chief name from properties!!
                JRMapArrayDataSource source = ReviewDetailPrintMZ.createReviewSourcePorto(prodApplications,bundle, prop, prodCompanyDAO, customReviewDAO);
                URL resource = getClass().getClassLoader().getResource("/reports/review_detail_report.jasper");
                if(source != null){
                    if(resource != null){
                        jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, source);
                        javax.servlet.http.HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                        httpServletResponse.addHeader("Content-disposition", "attachment; filename=" +prod.getProdName() + "_Review.pdf");
                        httpServletResponse.setContentType("application/pdf");
                        javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
                        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
                        servletOutputStream.close();
                        return "persist";
                    }else{
                        return "error";
                    }
                }else{
                    return "error";
                }

            } catch (JRException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return "error";
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return "error";
            }
        }else{
            return "error";
        }
    }
    /**
     * Read necessary properties for review details (for Porto language)
     * @return properties or null
     */
    private Properties fetchReviewDetailsProperties() {
        Properties props = new Properties();
        InputStream in;
        try {
            in = this.getClass().getResourceAsStream("review_details.properties");
            props.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return props;
    }
}
