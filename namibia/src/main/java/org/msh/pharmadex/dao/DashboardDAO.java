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

import sun.security.util.BigInt;

@Repository
@Transactional
public class DashboardDAO implements Serializable {

	private static final long serialVersionUID = -7177773420978773061L;
	@PersistenceContext
	EntityManager entityManager;

	public List<ItemDashboard> getListByPercentNemList() {
		List<ItemDashboard> items = null;

		String prodCat = ProdCategory.HUMAN + "";
		String regState = RegState.REGISTERED + "";

		String sql = "select year(pa.registrationDate), quarter(pa.registrationDate),"
				+ " count(pa.id), count(p.id)"
				+ " FROM prodapplications pa"
				+ " left join product p on p.id=pa.PROD_ID and (p.fnm is not null and length(trim(p.fnm))>0 and p.prod_cat like '" + prodCat + "')"
				+ " where pa.regState like '" + regState + "'"
				+ " group by year(pa.registrationDate), quarter(pa.registrationDate)";

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

		Collections.sort(items, new Comparator<ItemDashboard>() {
			@Override
			public int compare(ItemDashboard o1, ItemDashboard o2) {
				Integer i1 = new Integer(o1.getYear());
				Integer i2 = new Integer(o2.getYear());
				return i1.compareTo(i2);
			}
		});

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
				+ " group by year(pa.registrationDate), quarter(pa.registrationDate)";

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

		Collections.sort(items, new Comparator<ItemDashboard>() {
			@Override
			public int compare(ItemDashboard o1, ItemDashboard o2) {
				Integer i1 = new Integer(o1.getYear());
				Integer i2 = new Integer(o2.getYear());
				return i1.compareTo(i2);
			}
		});

		return items;
	}
}
