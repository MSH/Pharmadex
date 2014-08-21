package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.enums.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    public List<CompanyType> getCompanyType() {
        return Arrays.asList(CompanyType.values());
    }

    public List<ProdType> getProdTypes() {
        return Arrays.asList(ProdType.values());
    }

    public List<ProdCategory> getProdCategories() {
        List<ProdCategory> prodCategories = new ArrayList<ProdCategory>();
        prodCategories.add(ProdCategory.HUMAN);
        prodCategories.add(ProdCategory.VETENIARY);
        return prodCategories;
    }

    public List<ProdAppType> getProdAppType() {
        return Arrays.asList(ProdAppType.values());
    }

    public List<ProdDrugType> getDrugTypes() {
        return Arrays.asList(ProdDrugType.values());
    }

    public List<Modules> getModules() {
        return Arrays.asList(Modules.values());
    }

    public List<LetterType> getLetterTypes() {
        return Arrays.asList(LetterType.values());
    }

    public List<AmdmtType> getAmdmtType() {
        return Arrays.asList(AmdmtType.values());
    }

    public List<RecomendType> getRecomendType() {
        return Arrays.asList(RecomendType.values());
    }


    public List<AgeGroup> getAgeGroupes() {
        return Arrays.asList(AgeGroup.values());  //To change body of created methods use File | Settings | File Templates.
    }
}
