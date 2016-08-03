/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.lab;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.service.SampleTestServiceMZ;
import org.msh.pharmadex.util.RetObject;

/**
 * Author: dudchenko
 */
@ManagedBean
@ViewScoped
public class SampleTestBnMZ implements Serializable {

    @ManagedProperty(value = "#{sampleTestServiceMZ}")
    private SampleTestServiceMZ sampleTestServiceMZ;
    
    @ManagedProperty(value = "#{sampleTestBn}")
    private SampleTestBn sampleTestBn;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    public void addSample() {
        getSampleTestBn().createSample();
        
        RetObject riRetObj = sampleTestServiceMZ.createSampleReqLetter(getSampleTestBn().getSampleTest());
        if (!riRetObj.getMsg().equalsIgnoreCase("persist")) {
        	facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("processor_add_error")));
        } else {
        	getSampleTestBn().getSampleTests().add(getSampleTestBn().getSampleTest());

        }
        getSampleTestBn().setSampleTest(new SampleTest());
    }

	public SampleTestBn getSampleTestBn() {
		return sampleTestBn;
	}

	public void setSampleTestBn(SampleTestBn sampleTestBn) {
		this.sampleTestBn = sampleTestBn;
	}

	public SampleTestServiceMZ getSampleTestServiceMZ() {
		return sampleTestServiceMZ;
	}

	public void setSampleTestServiceMZ(SampleTestServiceMZ sampleTestServiceMZ) {
		this.sampleTestServiceMZ = sampleTestServiceMZ;
	}
}
