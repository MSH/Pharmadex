package org.msh.pharmadex.service;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.util.RegistrationUtil;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ProdApplicationsServiceET extends ProdApplicationsService {


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
        revState.add(ReviewStatus.RFI_SUBMIT);
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





}
