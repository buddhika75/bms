/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.common;

import java.io.ByteArrayInputStream;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author archmage
 */
@Named(value = "imageViewController")
@RequestScoped
public class ImageViewController {

    /**
     * Creates a new instance of ImageViewController
     */
    public ImageViewController() {
    }
    
    public StreamedContent getPhotoByByte(byte[] p) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (p == null) {
            return new DefaultStreamedContent();
        } else {
//   //System.out.println("giving image");
                        return new DefaultStreamedContent(new ByteArrayInputStream((byte[])p), "image/png");
        }
    }
    
}
