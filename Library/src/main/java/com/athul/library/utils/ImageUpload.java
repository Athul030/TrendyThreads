package com.athul.library.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ImageUpload {

    private final String UPLOAD_DIR= "C:\\Users\\admin\\Desktop\\Brototype\\Week 11\\TrendyThreads- Code\\Ecomv2\\Admin\\src\\main\\resources\\static\\img\\image-product";
    private final String UPLOAD_DIR_CUS= "C:\\Users\\admin\\Desktop\\Brototype\\Week 11\\TrendyThreads- Code\\Ecomv2\\Customer\\src\\main\\resources\\static\\imgs\\image-product";

    public List<String> uploadImages(List<MultipartFile> imageFiles) throws IOException {
        File uploadDir = new File(UPLOAD_DIR);
        File uploadDirCus = new File(UPLOAD_DIR_CUS);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        if (!uploadDirCus.exists()) {
            uploadDir.mkdirs();
        }
        List<String> uniqueFileNames = new ArrayList<>();

        try {
            for (MultipartFile imageFile : imageFiles) {
                String originalFileName = imageFile.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                Path destinationPath = Path.of(UPLOAD_DIR, uniqueFileName);
                Path destinationPathCus = Path.of(UPLOAD_DIR_CUS, uniqueFileName);
                while (Files.exists(destinationPath)) {
                    uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                    destinationPath = Path.of(UPLOAD_DIR, uniqueFileName);
                }
                while (Files.exists(destinationPathCus)) {
                    uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                    destinationPathCus = Path.of(UPLOAD_DIR_CUS, uniqueFileName);
                }

                Files.copy(imageFile.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(imageFile.getInputStream(), destinationPathCus, StandardCopyOption.REPLACE_EXISTING);

                uniqueFileNames.add(uniqueFileName);
            }
            }catch(Exception e){
                e.getMessage();
            }
            return uniqueFileNames;
        }

//    public static boolean checkExist(MultipartFile imageFile){
//        try {
//            File file = new File(UPLOAD_DIR + "\\" + imageFile.getOriginalFilename());
//            return file.exists();
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//    }
}
