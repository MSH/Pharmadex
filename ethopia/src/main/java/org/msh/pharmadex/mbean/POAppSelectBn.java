package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.PIPOrderLookUp;
import org.msh.pharmadex.domain.POrderChecklist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class POAppSelectBn extends PIPAppSelectBn implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(POAppSelectBn.class);

    @ManagedProperty(value = "#{purOrderBn}")
    PurOrderBn purOrderBn;

    @Override
    public void addApptoRegistration() {
        selectedApplicant = applicantService.findApplicant(selectedApplicant.getApplcntId());
        if(selectedUser!=null)
            applicantUser = userService.findUser(selectedUser.getUserId());
        purOrderBn.setApplicant(selectedApplicant);
        purOrderBn.setApplicantUser(applicantUser);

        purOrderBn.getPurOrder().setCreatedBy(userService.findUser(userSession.getLoggedINUserID()));
        purOrderBn.getPurOrder().setApplicantUser(applicantUser);
        purOrderBn.getPurOrder().setApplicant(selectedApplicant);

        List<POrderChecklist> pOrderChecklists = new ArrayList<POrderChecklist>();
        List<PIPOrderLookUp> allChecklist = pOrderService.findPIPCheckList(selectedApplicant.getApplicantType(), false);
        POrderChecklist eachCheckList;
        for (int i = 0; allChecklist.size() > i; i++) {
            eachCheckList = new POrderChecklist();
            eachCheckList.setPipOrderLookUp(allChecklist.get(i));
            eachCheckList.setPurOrder(purOrderBn.getPurOrder());
            pOrderChecklists.add(eachCheckList);
        }
        purOrderBn.setpOrderChecklists(pOrderChecklists);
    }

    public PurOrderBn getPurOrderBn() {
        return purOrderBn;
    }

    public void setPurOrderBn(PurOrderBn purOrderBn) {
        this.purOrderBn = purOrderBn;
    }
}
