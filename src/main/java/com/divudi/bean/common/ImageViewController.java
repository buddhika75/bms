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

    // Modified by Dr M H B Ariyaratne with assistance from ChatGPT from OpenAI
    public StreamedContent getPhotoByByte(byte[] p) {
        if (p == null) {
            return DefaultStreamedContent.builder().build();
        } else {
            return DefaultStreamedContent.builder()
                    .contentType("image/png")
                    .stream(() -> new ByteArrayInputStream(p))
                    .build();
        }
    }

}
