package org.msh.pharmadex.service;

import java.io.File;
import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;


/**
 * This class intends to provide possibility to use facelets common to MZ, NA and BD, but not ET 
 * @author Alex Kurasoff
 *
 */
public class PharmadexViewHandler extends ViewHandlerWrapper {

	private ViewHandler wrapped;
	private static final String[] FOLDERS = {"/admin/","/internal/","/public/","/secure/"};

	public PharmadexViewHandler(ViewHandler wrapped){
		super();
		System.err.println("CustomViewHandler.CustomViewHandler():wrapped View Handler:"+wrapped.getClass());
		this.wrapped= wrapped;
	}

	@Override
	public String getActionURL(FacesContext context, String viewId) {
		viewId = viewToUri(context, viewId);
		return super.getActionURL(context, viewId);
	}

	/**
	 * Strip "/custom" from the path if exists
	 * @param facesContext
	 * @param viewid
	 * @return
	 */
	protected String viewToUri(FacesContext facesContext, String viewid) {
		String rootviewid = (facesContext.getViewRoot().getViewId());
		// check if uri is the page being rendered 
		if (!viewid.equals(rootviewid))
			return viewid;
		// just convert viewid inside the page
		String uri = convertViewToUri(facesContext, viewid);
		return uri;
	}

	/**
	 * Convert a view id to a URI to be used by facelets
	 * @param facesContext
	 * @param view
	 * @return
	 */
	public String convertViewToUri(FacesContext facesContext, String view) {
		String customPath="/custom";
		if (view.startsWith(customPath)) {
			String uri = view.substring(customPath.length(), view.length() - 1);
			return uri;
		}
		else return view;
	}

	/**
	 * Search for the most priority view 
	 * most priority is on /custom path
	 * @param facesContext
	 * @param uri
	 * @return
	 */
	protected String uriToView(FacesContext facesContext, String uri) {
		String viewid = convertUriToView(facesContext, uri);
		return viewid;
	}
	/**
	 * Try to convert uri to real view will be in use
	 * first, try to add "custom" as the most priority
	 * second, try to find in regular folder
	 * third, try to find in all "custom" folders
	 * fourth, try to find in all regular folders
	 * @param facesContext
	 * @param uri
	 * @return
	 */
	private String convertUriToView(FacesContext facesContext, String uri) {
		String extPage = "/custom" + uri;
		if (pageExists(facesContext, extPage)){
			return extPage;
		}
		else{
			if(pageExists(facesContext,uri)){
				return uri;
			}else{
				return searchForPage(facesContext,uri);
			}
		}
	}
	/**
	 * get page name and try to search for all sub folders
	 * 
	 * @param facesContext
	 * @param uri
	 * @return
	 */
	private String searchForPage(FacesContext facesContext, String uri) {
		String tmp = uri.substring(uri.lastIndexOf("/")+1,uri.length());
		for(String folder : FOLDERS){
			if(pageExists(facesContext, "/custom"+folder+tmp)){
				return "/custom"+folder+tmp;
			}
			if(pageExists(facesContext, folder+tmp)){
				return folder+tmp;
			}
		}
		return uri;
	}

	/**
	 * Does page exist on uri given
	 * @param facesContext
	 * @param extPage
	 * @return
	 */
	private boolean pageExists(FacesContext facesContext, String extPage) {
		String pg = ((ServletContext)facesContext.getExternalContext().getContext()).getRealPath(extPage);
		File pageFile = new File(pg.replace(".faces", ".xhtml"));
		boolean exists = pageFile.exists();
		return exists;
	}

	@Override
	public UIViewRoot createView(FacesContext context, String viewId) {
		return super.createView(context, viewId);
	}


	@Override
	public UIViewRoot restoreView(FacesContext facesContext, String viewId) {
		String newView = uriToView(facesContext, viewId);
		return super.restoreView(facesContext, newView);
	}


	@Override
	public void renderView(FacesContext facesContext, UIViewRoot viewToRender)
			throws IOException, FacesException {

		String viewId = viewToRender.getViewId();
		viewId = uriToView(facesContext, viewId);
		viewToRender.setViewId(viewId);

		super.renderView(facesContext, viewToRender);
	}


	@Override
	public ViewHandler getWrapped() {
		return this.wrapped;
	}

}
