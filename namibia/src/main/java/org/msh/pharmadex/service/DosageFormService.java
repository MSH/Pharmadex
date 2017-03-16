package org.msh.pharmadex.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.msh.pharmadex.dao.iface.DosUomDAO;
import org.msh.pharmadex.dao.iface.DosageFormDAO;
import org.msh.pharmadex.domain.DosUom;
import org.msh.pharmadex.domain.DosageForm;
import org.msh.pharmadex.mbean.product.DosFormItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: usrivastava
 */
@Service
public class DosageFormService implements Serializable {

	private static final long serialVersionUID = 57867251887209089L;

	@Autowired
	private DosageFormDAO dosageFormDAO;

	@Autowired
	private DosUomDAO dosUomDAO;

	@Transactional
	public List<DosageForm> findAllDosForm() {
		List<DosageForm> dosageForms = (List<DosageForm>) dosageFormDAO.findAll();
		return dosageForms;
	}

	/*public DosFormItem findMainDosFormItemById(long id) {
		DosageForm df = dosageFormDAO.findOne(id);
		DosFormItem it = null;
		if(df != null){
			it = new DosFormItem();
			String val = df.getDosForm();
			it.setFullName(val);
			int index = val.indexOf(",");
			if(index != -1){
				String n = val.substring(0, index);
				it.setName(n);
			}
		}
		return it;
	}*/

	public DosFormItem findDosFormItemByName(String name, boolean isMain) {
		DosFormItem it = null;
		DosageForm df = findDosageFormByName(name, isMain);
		if(df != null){
			it = new DosFormItem();
			it.setName(name);
			it.setFullName(name);
		}
		return it;
	}

	public List<DosFormItem> findMainDosForms() {
		List<DosFormItem> res = new ArrayList<DosFormItem>();

		List<DosageForm> list = (List<DosageForm>) dosageFormDAO.findAll();
		if(list != null && list.size() > 0){
			Set<String> uniq = new HashSet<String>();
			for(DosageForm df:list){
				String val = df.getDosForm();
				String name = "";
				int index = val.indexOf(",");
				if(index != -1)
					name = val.substring(0, index);
				else
					name = val;

				if(!uniq.contains(name)){
					DosFormItem it = new DosFormItem();
					it.setName(name);
					it.setFullName(val);

					res.add(it);

					uniq.add(name);
				}
			}
		}
		return res;
	}

	public List<DosFormItem> findSubDosFormByMain(DosFormItem mainIt) {
		List<DosFormItem> res = new ArrayList<DosFormItem>();
		if(mainIt == null)
			return res;

		String mainName = mainIt.getName();

		if(mainName != null && mainName.length() > 0){
			DosageForm m = findDosageFormByName(mainName, true);
			DosFormItem emptyIt = null;
			if(m != null){
				emptyIt = new DosFormItem();
				emptyIt.setFullName(mainName);
				emptyIt.setName(" ");
				res.add(emptyIt);
			}
			String start = mainName + ", ";
			List<DosageForm> list = findDosageFormByStartName(start);
			if(list != null && list.size() > 0){
				for(DosageForm df:list){
					DosFormItem it = new DosFormItem();
					String val = df.getDosForm();
					it.setFullName(val);
					int index = val.indexOf(", ");
					if(index != -1){
						String n = val.substring(index + 1).trim();
						it.setName(n);
					}
					res.add(it);
				}
			}else
				res = new ArrayList<DosFormItem>();
		}
		return res;
	}
	
	public DosFormItem[] findSaveDosForm(Long dosFId) {
		DosFormItem[] res = null;
		if(dosFId == null)
			return res;
		if(dosFId < 1)
			return res;
		
		DosageForm df = dosageFormDAO.findOne(dosFId);
		if(df != null){
			res = new DosFormItem[2];
			DosFormItem main = new DosFormItem();
			DosFormItem it = null;
			
			String val = df.getDosForm();
			main.setFullName(val);
			int index = val.indexOf(",");
			if(index != -1){
				String n = val.substring(0, index).trim();
				main.setName(n);
				
				it = new DosFormItem();
				it.setFullName(val);
				n = val.substring(index + 1).trim();
				it.setName(n);
			}else{
				main.setName(val);
			}
			
			res[0] = main;
			res[1] = it;
		}
		
		return res;
	}

	public DosageForm findDosageFormByItem(DosFormItem main, DosFormItem it){
		DosageForm res = null;
		if(main == null)
			return res;
		
		String fname = main.getName() + (it != null ? (", " + it.getName().trim()) : "");
		res = findDosageFormByName(fname, (it != null));
		return res;
	}
	
	public DosageForm findDosageFormByName(String name, boolean isfullname){
		List<DosageForm> dosageForms = (List<DosageForm>) dosageFormDAO.findAll();
		for (DosageForm c : dosageForms) {
			if(isfullname){
				if (c.getDosForm().equalsIgnoreCase(name))
					return c;
			}else{
				if (c.getDosForm().endsWith(name))
					return c;
			}
		}
		return null;
	}

	public List<DosageForm> findDosageFormByStartName(String startStr){
		List<DosageForm> res = new ArrayList<DosageForm>();

		List<DosageForm> dosageForms = (List<DosageForm>) dosageFormDAO.findAll();
		for (DosageForm c : dosageForms) {
			if (c.getDosForm().startsWith(startStr))
				res.add(c);
		}
		return res;
	}

	/**
	 * 03.05.2016
	 * значение mg вынесено на первое место-чаще всего используется
	 */
	@Transactional
	public List<DosUom> findAllDosUom() {
		List<DosUom> dosUoms = (List<DosUom>) dosUomDAO.findAll();
		if(dosUoms != null && dosUoms.size() > 0){
			DosUom mg = null;
			for(DosUom uom:dosUoms){
				if(uom.getUom().equals("mg")){
					mg = uom;
					break;
				}
			}
			if(mg != null){
				int ind_mg = dosUoms.indexOf(mg);
				if(ind_mg > 0){ // вдруг и так первый в списке
					DosUom firstUom = dosUoms.get(0);
					dosUoms.set(0, mg);
					dosUoms.set(ind_mg, firstUom);
				}
			}
		}
		return dosUoms;
	}

	@Transactional
	/**
	 * Reread list from DB, even dosUom exist (usually after updatr dictionary)
	 */
	public List<DosUom> fetchAllDosUom() {
		List<DosUom> dosUoms = (List<DosUom>) dosUomDAO.findAll();
		return dosUoms;
	}

	@Transactional
	public DosageForm findDosagedForm(Long id) {
		return dosageFormDAO.findOne(id);
	}


	public DosUom findDosUom(int id) {
		return dosUomDAO.findOne(id);

	}

	public DosUom findDosUomByName(String name){
		if (name==null) return null;
		if ("".equals(name)) return null;
		List<DosUom> lst = findAllDosUom();
		if (lst==null) return null;
		if (lst.size()==0) return null;
		for (DosUom unit:lst){
			if (unit.getUom().equalsIgnoreCase(name)){
				return unit;
			}
		}
		return  null;
	}

	public DosUom saveDosUom(String name){
		DosUom unit = new DosUom();
		DosUom existUnit = findDosUomByName(name);
		unit.setUom(name);
		unit.setDiscontinued(false);
		if (existUnit!=null) return existUnit;
		unit = dosUomDAO.save(unit);
		dosUomDAO.flush();
		return unit;
	}

}
