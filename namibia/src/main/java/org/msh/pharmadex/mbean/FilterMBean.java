package org.msh.pharmadex.mbean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.msh.pharmadex.domain.Inn;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.msh.pharmadex.service.ProductService;
import org.primefaces.event.SelectEvent;

@ManagedBean
@SessionScoped
public class FilterMBean implements Serializable, HttpSessionBindingListener{

	private static final long serialVersionUID = 5973582231078375169L;
	
	@ManagedProperty(value = "#{productService}")
	private ProductService productService;
	
	private List<ProdTable> products;
	private List<ProdTable> filteredProducts;
	public Inn currentInn;
	public Date startDate;
	public Date endDate;
	
	public String yearRange = "c-50:c";
	private String minYear = "";
	
	@PostConstruct
	private void init() {
		products = loadDefaultProducts();
		minYear = productService.getMinRegistrationYearInDB();
				
	}
	
	public String getYearRange() {
		if(!minYear.isEmpty())
			yearRange = minYear + ":c";
		return yearRange;
	}

	public void setYearRange(String yearRange) {
		this.yearRange = yearRange;
	}

	public void filterClick(){
		setProducts(loadProducts());
	}
	
	public void clearClick(){
		setProducts(loadDefaultProducts());
	}
	
	public List<ProdTable> getProducts() {
		return products;
	}

	public void setProducts(List<ProdTable> products) {
		this.products = products;
	}
	
	public Inn getCurrentInn() {
		return currentInn;
	}

	public void setCurrentInn(Inn currentInn) {
		this.currentInn = currentInn;
	}

	public List<ProdTable> loadProducts(){
		Long innId = new Long(0);
		if(currentInn != null && currentInn.getId() != null)
			innId = currentInn.getId();
		List<ProdTable> products = productService.findRegisteredProduct(innId, getStartDate(), getEndDate());
		return products;
	}

	public void onItemSelect(SelectEvent event) {
		if(event.getObject() instanceof Inn){
			currentInn = (Inn) event.getObject();
		}
	}

	public List<ProdTable> loadDefaultProducts(){
		List<ProdTable> products = productService.findAllRegisteredProduct();
		currentInn = null;
		startDate = null;
		endDate = new Date();
		
		return products;
	}
	
	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}
	
	public List<ProdTable> getFilteredProducts() {
		return filteredProducts;
	}

	public void setFilteredProducts(List<ProdTable> filteredProducts) {
		this.filteredProducts = filteredProducts;
	}

	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
