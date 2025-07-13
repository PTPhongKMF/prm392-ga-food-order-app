package com.example.foodorderapp.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.foodorderapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageManager {
    private static final String TAG = "ImageManager";
    private static final String DRAWABLE_PREFIX = "product_";
    private static int imageCounter = 1;
    
    // Cache để lưu mapping giữa imagePath và resource ID
    private static final Map<String, Integer> imageResourceMap = new HashMap<>();
    
    static {
        // Khởi tạo mapping cho các ảnh có sẵn
        initializeImageMapping();
    }
    
    private static void initializeImageMapping() {
        // Pizza images
        imageResourceMap.put("pizza_1", R.drawable.pizza_1);
        imageResourceMap.put("pizza_2", R.drawable.pizza_2);
        imageResourceMap.put("pizza_3", R.drawable.pizza_3);
        imageResourceMap.put("pizza_4", R.drawable.pizza_4);
        imageResourceMap.put("pizza_5", R.drawable.pizza_5);
        
        // Burger images
        imageResourceMap.put("burger_1", R.drawable.burger_1);
        imageResourceMap.put("burger_2", R.drawable.burger_2);
        imageResourceMap.put("burger_3", R.drawable.burger_3);
        imageResourceMap.put("burger_4", R.drawable.burger_4);
        imageResourceMap.put("burger_5", R.drawable.burger_5);
        
        // Chicken images
        imageResourceMap.put("chicken_1", R.drawable.chicken_1);
        imageResourceMap.put("chicken_2", R.drawable.chicken_2);
        imageResourceMap.put("chicken_3", R.drawable.chicken_3);
        imageResourceMap.put("chicken_4", R.drawable.chicken_4);
        imageResourceMap.put("chicken_5", R.drawable.chicken_5);
        
        // Sushi images
        imageResourceMap.put("sushi_1", R.drawable.sushi_1);
        imageResourceMap.put("sushi_2", R.drawable.sushi_2);
        imageResourceMap.put("sushi_3", R.drawable.sushi_3);
        imageResourceMap.put("sushi_4", R.drawable.sushi_4);
        imageResourceMap.put("sushi_5", R.drawable.sushi_5);
        
        // Meat images
        imageResourceMap.put("meat_1", R.drawable.meat_1);
        imageResourceMap.put("meat_2", R.drawable.meat_2);
        imageResourceMap.put("meat_3", R.drawable.meat_3);
        imageResourceMap.put("meat_4", R.drawable.meat_4);
        imageResourceMap.put("meat_5", R.drawable.meat_5);
        
        // Hotdog images
        imageResourceMap.put("hotdog_1", R.drawable.hotdog_1);
        imageResourceMap.put("hotdog_2", R.drawable.hotdog_2);
        imageResourceMap.put("hotdog_3", R.drawable.hotdog_3);
        imageResourceMap.put("hotdog_4", R.drawable.hotdog_4);
        imageResourceMap.put("hotdog_5", R.drawable.hotdog_5);
        
        // Drink images
        imageResourceMap.put("drink_1", R.drawable.drink_1);
        imageResourceMap.put("drink_2", R.drawable.drink_2);
        imageResourceMap.put("drink_3", R.drawable.drink_3);
        imageResourceMap.put("drink_4", R.drawable.drink_4);
        imageResourceMap.put("drink_5", R.drawable.drink_5);
        
        // Coffee images
        imageResourceMap.put("coffie_1", R.drawable.coffie_1);
        imageResourceMap.put("coffie_2", R.drawable.coffie_2);
        imageResourceMap.put("coffie_3", R.drawable.coffie_3);
        imageResourceMap.put("coffie_4", R.drawable.coffie_4);
        imageResourceMap.put("coffie_5", R.drawable.coffie_5);
        
        // More images
        imageResourceMap.put("more_1", R.drawable.more_1);
        imageResourceMap.put("more_2", R.drawable.more_2);
        imageResourceMap.put("more_3", R.drawable.more_3);
        imageResourceMap.put("more_4", R.drawable.more_4);
        imageResourceMap.put("more_5", R.drawable.more_5);
    }
    
    /**
     * Lấy resource ID từ imagePath
     */
    public static int getImageResource(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return R.drawable.pizza_1; // Default image
        }
        
        Integer resourceId = imageResourceMap.get(imagePath);
        return resourceId != null ? resourceId : R.drawable.pizza_1;
    }
    
    /**
     * Thêm ảnh mới vào mapping
     */
    public static void addImageMapping(String imagePath, int resourceId) {
        imageResourceMap.put(imagePath, resourceId);
    }
    
    /**
     * Tạo tên file ảnh mới
     */
    public static String generateImageName() {
        return DRAWABLE_PREFIX + System.currentTimeMillis() + "_" + imageCounter++;
    }
    
    /**
     * Copy ảnh từ Uri vào internal storage và trả về tên file
     */
    public static String copyImageToInternalStorage(Context context, Uri imageUri) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(imageUri);
            
            if (inputStream == null) {
                Log.e(TAG, "Không thể mở input stream");
                return "pizza_1"; // Return default image
            }
            
            // Tạo tên file mới
            String fileName = generateImageName();
            
            // Tạo file trong internal storage
            File internalDir = context.getDir("product_images", Context.MODE_PRIVATE);
            File imageFile = new File(internalDir, fileName + ".jpg");
            
            // Copy ảnh
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            outputStream.close();
            
            Log.d(TAG, "Đã copy ảnh thành công: " + fileName);
            return fileName;
            
        } catch (IOException e) {
            Log.e(TAG, "Lỗi khi copy ảnh: " + e.getMessage());
            return "pizza_1"; // Return default image
        }
    }
    
    /**
     * Lấy file ảnh từ internal storage
     */
    public static File getImageFile(Context context, String imageName) {
        File internalDir = context.getDir("product_images", Context.MODE_PRIVATE);
        return new File(internalDir, imageName + ".jpg");
    }
    
    /**
     * Kiểm tra xem ảnh có tồn tại không
     */
    public static boolean imageExists(Context context, String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            return false;
        }
        
        // Kiểm tra trong drawable resources
        if (imageResourceMap.containsKey(imageName)) {
            return true;
        }
        
        // Kiểm tra trong internal storage
        File imageFile = getImageFile(context, imageName);
        return imageFile.exists();
    }
    
    /**
     * Xóa ảnh từ internal storage
     */
    public static boolean deleteImage(Context context, String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            return false;
        }
        
        File imageFile = getImageFile(context, imageName);
        if (imageFile.exists()) {
            boolean deleted = imageFile.delete();
            if (deleted) {
                Log.d(TAG, "Đã xóa ảnh: " + imageName);
            }
            return deleted;
        }
        
        return false;
    }
    
    /**
     * Lấy danh sách tất cả ảnh có sẵn
     */
    public static String[] getAvailableImages() {
        return imageResourceMap.keySet().toArray(new String[0]);
    }
} 