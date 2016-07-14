package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.service.ReviewService;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by usrivastava on 09/27/2015.
 */
@ManagedBean
@ApplicationScoped
public class ImageStreamer implements Serializable {

	private static final long serialVersionUID = -4171286475196794385L;
	@ManagedProperty(value = "#{reviewService}")
    private ReviewService reviewService;

    public StreamedContent getImage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            String imageId = context.getExternalContext().getRequestParameterMap().get("imageId");
            byte[] image = reviewService.findReviewDetailImage(Long.valueOf(imageId));

            if(image != null)
            	return new DefaultStreamedContent(new ByteArrayInputStream(image));
            return new DefaultStreamedContent();
        }
    }

    public ReviewService getReviewService() {
        return reviewService;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
}
