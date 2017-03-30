package org.msh.pharmadex.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.msh.pharmadex.domain.enums.ProdCategory;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.ItemDashboard;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class DashboardDAO implements Serializable {

	private static final long serialVersionUID = -7177773420978773061L;
	@PersistenceContext
	EntityManager entityManager;

	public List<ItemDashboard> getListTimesProcess() {
		List<ItemDashboard> items = null;
		
		String sql = "SELECT"
					+ " Year(appEnd.statusDate) as y,"
					+ " quarter(appEnd.statusDate) as q,"
					+ " count(DISTINCT pa.id) as totalApps,"
					+ " avg(datediff(scrEnd.statusDate, scrStart.statusDate)) as 'screening', "
					+ " avg(datediff(revEnd.statusDate, revStart.statusDate)) as 'review',"
					+ " avg(datediff(appEnd.statusDate, appStart.statusDate)) as 'total'"
					+ " FROM prodapplications pa"
					+ " JOIN timeline appStart on pa.ID = appStart.PROD_APP_ID  and appStart.regState='" + RegState.MS_START + "'"
					+ " JOIN timeline scrStart on pa.ID = scrStart.PROD_APP_ID  and scrStart.regState='" + RegState.MS_SCR_START + "'"
					+ " JOIN timeline scrEnd on pa.ID = scrEnd.PROD_APP_ID and scrEnd.regState='" + RegState.MS_SCR_END + "'"
					+ " LEFT JOIN timeline revStart on pa.ID = revStart.PROD_APP_ID and revStart.regState='" + RegState.MS_REV_START + "'"
					+ " LEFT JOIN timeline revEnd on pa.ID = revEnd.PROD_APP_ID and revEnd.regState='" + RegState.MS_REV_END + "'"
					+ " JOIN timeline appEnd on pa.ID = appEnd.PROD_APP_ID and appEnd.regState='" + RegState.MS_END + "'"
					+ " where pa.regState='" + RegState.REGISTERED + "' or pa.regState='" + RegState.REJECTED + "'"
					+ " Group by y DESC,q DESC";

		List<Object[]> list = entityManager.createNativeQuery(sql).getResultList();
		if(list != null && list.size() > 0){
			items = new ArrayList<ItemDashboard>();
			for(Object[] val:list){
				ItemDashboard it = new ItemDashboard();
				it.setYear((Integer)val[0]);
				it.setQuarter((Integer)val[1]);
				it.setTotal(((BigInteger)val[2]).intValue());
				it.setAvg_screening(((BigDecimal)val[3]).doubleValue());
				it.setAvg_review(((BigDecimal)val[4]).doubleValue());
				it.setAvg_total(((BigDecimal)val[5]).doubleValue());

				items.add(it);
			}
		}

		return items;
	}
	
	public List<ItemDashboard> getListByPercentNemList() {
		List<ItemDashboard> items = null;

		String prodCat = ProdCategory.HUMAN + "";
		String regState = RegState.REGISTERED + "";

		String sql = "select year(pa.registrationDate), quarter(pa.registrationDate),"
				+ " count(pa.id), count(p.id)"
				+ " FROM prodapplications pa"
				+ " left join product p on p.id=pa.PROD_ID and (p.fnm is not null and length(trim(p.fnm))>0 and p.prod_cat like '" + prodCat + "')"
				+ " where pa.regState like '" + regState + "'"
				+ " group by year(pa.registrationDate) DESC, quarter(pa.registrationDate) DESC";

		List<Object[]> list = entityManager.createNativeQuery(sql).getResultList();
		if(list != null && list.size() > 0){
			items = new ArrayList<ItemDashboard>();
			for(Object[] val:list){
				ItemDashboard it = new ItemDashboard();
				it.setYear((Integer)val[0]);
				it.setQuarter((Integer)val[1]);
				it.setTotal(((BigInteger)val[2]).intValue());
				it.setCount(((BigInteger)val[3]).intValue());

				items.add(it);
			}
		}

		return items;
	}

	public List<ItemDashboard> getListByPercentApprovedList() {
		List<ItemDashboard> items = null;

		String regStateReg = RegState.REGISTERED + "";
		String regStateRej = RegState.REJECTED + "";

		String sql = "select year(pa.registrationDate), quarter(pa.registrationDate),"
				+ " count(pa.id)"
				+ ",  Sum(IF(pa.regState like '" + regStateReg + "', 1, 0))"
				+ ",  Sum(IF(pa.regState like '" + regStateRej + "', 1, 0))"
				+ " FROM prodapplications pa"
				+ " where pa.regState like '" + regStateReg + "'"
				+ " or pa.regState like '" + regStateRej + "'"
				+ " group by year(pa.registrationDate) DESC, quarter(pa.registrationDate) DESC";

		List<Object[]> list = entityManager.createNativeQuery(sql).getResultList();
		if(list != null && list.size() > 0){
			items = new ArrayList<ItemDashboard>();
			for(Object[] val:list){
				ItemDashboard it = new ItemDashboard();
				if(val[0] != null){
					it.setYear((Integer)val[0]);
					it.setQuarter((Integer)val[1]);
					it.setTotal(((BigInteger)val[2]).intValue());
					it.setCount(((BigDecimal)val[3]).intValue());
					it.setCount_other(((BigDecimal)val[4]).intValue());
					items.add(it);
				}
			}
		}

		return items;
	}
	
	public List<ItemDashboard> getListByApplicant() {
		List<ItemDashboard> items = null;

		String regState = RegState.REGISTERED + "";
		
		String sql = "select a.appName, count(p.id),"
				+ " count(pa.id) - count(p.id),"
				+ " count(pa.id)"
				+ " FROM prodapplications pa"
				+ " left join product p on p.id=pa.PROD_ID and (p.fnm is not null and length(trim(p.fnm))>0)"
				+ " left join applicant a on a.applcntId=pa.APP_ID"
				+ " where pa.regState like '" + regState + "'"
				+ " group by a.appName ASC";
		 
		List<Object[]> list = entityManager.createNativeQuery(sql).getResultList();
		if(list != null && list.size() > 0){
			items = new ArrayList<ItemDashboard>();
			for(Object[] val:list){
				ItemDashboard it = new ItemDashboard();
				if(val[0] != null){
					it.setAppName((String)val[0]);
					it.setCount(((BigInteger)val[1]).intValue());
					it.setCount_other(((BigInteger)val[2]).intValue());
					it.setTotal(((BigInteger)val[3]).intValue());
					items.add(it);
				}
			}
		}

		return items;
	}
}
