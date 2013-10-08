package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.enums.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Author: usrivastava
 */
@Component
@Scope("singleton")
public class GlobalLists {

    public List<UserType> getUserTypes() {
        return Arrays.asList(UserType.values());
    }

    public List<ApplicantType> getAppType() {
        return Arrays.asList(ApplicantType.values());
    }

    public List<CompanyType> getCompanyType() {
        return Arrays.asList(CompanyType.values());
    }

    public List<ProdType> getProdTypes() {
        return Arrays.asList(ProdType.values());
    }

    public List<AdminRoute> getAdminRoutes() {
        return Arrays.asList(AdminRoute.values());
    }

    public List<ProdDrugType> getDrugTypes() {
        return Arrays.asList(ProdDrugType.values());
    }

    public List<Modules> getModules() {
        return Arrays.asList(Modules.values());
    }


}
