package org.msh.pharmadex.service;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tools.ant.types.FileList;
import org.msh.pharmadex.domain.Attachment;
import org.msh.pharmadex.util.StrTools;
import org.primefaces.model.UploadedFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletContext;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Properties;
import java.util.UUID;
import static org.omnifaces.util.Faces.getServletContext;

/**
 * Created by Одиссей on 19.10.2016.
 */
public class AttachmentService {
    private final static String fileSeparator = "/";
    public static String uploadPath="";
    public static String uniqueFileName;

    public static String getUploadPath() {
        Properties prop = new Properties();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            prop.load(loader.getResourceAsStream("db.properties"));
            uploadPath = prop.getProperty("upload.location");
            if (StrTools.isEmptyString(uploadPath)){
                return null;
                //throw new IOException("can't open property");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            File tmpDir = (File)getServletContext().getAttribute(ServletContext.TEMPDIR);
            uploadPath = tmpDir.getPath();
        }
        if (uploadPath.startsWith("\"")) uploadPath = StrTools.right(uploadPath,"\"");
        if (uploadPath.endsWith("\"")) uploadPath = StrTools.left(uploadPath,"\"");
        if (!StrTools.isEmptyString(uploadPath)) {
            if (uploadPath.contains("\\"))
                uploadPath.replaceAll("\\\\",fileSeparator);
        }
        return uploadPath;
    }


    public static boolean attStoresInDb(){
        return StrTools.isEmptyString(getUploadPath());
    }

    public static String getFullUploadPath(){
        String folder = getUploadPath();
        if (StrTools.isEmptyString(folder)) return "";
        if (!folder.endsWith(fileSeparator)) folder = folder+fileSeparator;
        String day = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        folder = folder + day + fileSeparator;
        return folder;
    }

    public static String getUniqueFileName(Long id, String table) {
        if (id==null)
            uniqueFileName = table + UUID.randomUUID();
        else
            uniqueFileName = table+String.valueOf(id);
        return uniqueFileName;
    }

    public static String save(InputStream input, String fileName) {
        String res="";
        try {
            //get or create temporary path
            String tempDir = getFullUploadPath();
            java.nio.file.Path path = Paths.get(tempDir);
            if (!"".equals(path)) {
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
            }

            java.nio.file.Path file;
            if (fileName != null){
                if (fileName.contains(":")||fileName.contains("/")||fileName.contains("\\")) {//this is not file name, but full path
                    fileName = FilenameUtils.getName(fileName);
                }
            }
            String extension = FilenameUtils.getExtension(fileName);
            if (StrTools.isEmptyString(extension))
                extension = ".pdf";
            else
                extension = "." + extension;

            file = Files.createTempFile(path, fileName, extension);
            Long result = Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
            if (result > 0) res = tempDir+file.getFileName().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return res;
    }

    public static String extractAttFileNameFromBLOBField(byte[] file){
        String filename=null;
        if (file!=null) {
            if (file.length < 200)
                filename = new String(file, Charset.forName("UTF-8"));
        }else{
            return "";
        }
        String extension = FilenameUtils.getExtension(filename);
        if (StrTools.isEmptyString(extension))
            filename = filename + ".pdf";
        return filename;
    }

    public static String extractAttFileName(Attachment attachment){
        byte[] file = attachment.getFile();
        String filename=null;
        if (file!=null) {
            if (file.length < 200)
                filename = new String(file, Charset.forName("UTF-8"));
        }else{
            filename = attachment.getFileName();
            if (filename.contains(":") || filename.contains("/")) return null;
        }
        String extension = FilenameUtils.getExtension(filename);
        if (StrTools.isEmptyString(extension))
            filename = filename + ".pdf";
        return filename;
    }

    public static String deleteAttachedFile(Attachment attachment){
        String fileName = extractAttFileName(attachment);
        if (fileName==null) return "";
        try {
            File file = new File(fileName);
            if (!file.exists()) return "";
            file.deleteOnExit();
        }catch(Exception e){
            return e.getMessage();
        }
        return "";
    }

    private static String parseExtension(String fileName){
        if (StrTools.isEmptyString(fileName)) return "";
        String ext = FilenameUtils.getExtension(fileName);
        String cType="";
        if (ext.equalsIgnoreCase("pdf"))
            cType = "application/pdf";
        else if (ext.equalsIgnoreCase("jpeg")||ext.equalsIgnoreCase("jpg"))
            cType = "image/jpeg";
        else if (ext.equalsIgnoreCase("png"))
            cType = "image/png";
        else if (ext.equalsIgnoreCase("gif"))
            cType = "image/gif";
        else
            cType = "application/pdf";
        return cType;
    }

    public static String detectContentType(UploadedFile file){
        if (StrTools.isEmptyString(file.getFileName())) return "";
        String cType = file.getContentType();
        if (!StrTools.isEmptyString(cType)) return cType;
        cType = parseExtension(file.getFileName());
        return cType;
    }

    public static String detectContentType(File file){
        if (StrTools.isEmptyString(file.getName())) return "";
        String cType=parseExtension(file.getName());
        return cType;
    }

    public static void setUniqueFileName(String uniqueFileName) {
        AttachmentService.uniqueFileName = uniqueFileName;
    }

    public static void setUploadPath(String uploadPath) {
        AttachmentService.uploadPath = uploadPath;
    }

    public static String getUniqueFileName() {
        return uniqueFileName;
    }
}
