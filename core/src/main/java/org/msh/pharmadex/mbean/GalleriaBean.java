package org.msh.pharmadex.mbean;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: utkarsh
 * Date: 11/28/12
 * Time: 10:58 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class GalleriaBean implements Serializable {

    private List<ImagePack> images;
    private String[] imageDescArr;


    @PostConstruct
    public void init() {
        images = new ArrayList<ImagePack>();
        imageDescArr = new String[]{"Confisticated Drugs", "HIV AIDS Medicine", "NMRC inaguration", "World Map", "tabs", "injections", "pharmacy", "tabs2"};

        for(int i=1;i<=6;i++) {
            String imageName = "galleria" + i + ".jpg";
            String imageDesc = imageDescArr[i-1];
            images.add(new ImagePack(imageName, imageDesc));
        }
    }

    public List<ImagePack> getImages() {
        return images;
    }

    public class ImagePack{
        String imageName;
        String imageDesc;

        ImagePack(String imageName, String imageDesc) {
            this.imageName = imageName;
            this.imageDesc = imageDesc;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public String getImageDesc() {
            return imageDesc;
        }

        public void setImageDesc(String imageDesc) {
            this.imageDesc = imageDesc;
        }
    }
}