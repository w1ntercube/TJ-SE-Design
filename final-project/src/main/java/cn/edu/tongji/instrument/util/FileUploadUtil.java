package cn.edu.tongji.instrument.util;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUploadUtil {

    // 商品图片保存目录
    private static final String PRODUCT_IMAGE_UPLOAD_DIR = "uploads/images/";

    // 用户头像保存目录
    private static final String AVATAR_UPLOAD_DIR = "uploads/avatars/";

    // 保存商品图片
    public static String saveProductImage(MultipartFile file, long productId) throws IOException {
        return saveFile(file, productId, PRODUCT_IMAGE_UPLOAD_DIR);
    }

    // 保存用户头像
    public static String saveAvatar(MultipartFile file, long userId) throws IOException {
        return saveFile(file, userId, AVATAR_UPLOAD_DIR);
    }

    // 保存文件到指定目录，并返回相对路径
    public static String saveFile(MultipartFile file, long productId, String directory) throws IOException {
        // 获取项目根目录
        String projectDir = System.getProperty("user.dir");  // 获取项目根路径
        Path uploadPath = Paths.get(projectDir, directory);  // 拼接上传目录路径

        // 如果目录不存在，创建目录
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 使用商品ID生成文件名
        String fileName = productId + ".jpg";  // 使用商品ID作为文件名

        // 保存文件到目标路径
        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath.toFile());  // 将文件保存到磁盘

        // 返回图片在上传目录下的相对路径
        return "/" + directory + "/" + fileName;  // 返回文件路径（相对路径）
    }

    // 删除文件
    public static void deleteFile(String filePath) throws IOException {
        // 获取项目根目录
        String projectDir = System.getProperty("user.dir");  // 获取项目根路径
        Path fileAbsolutePath = Paths.get(projectDir, filePath);  // 将相对路径转为绝对路径

        File file = fileAbsolutePath.toFile();

        if (file.exists()) {
            System.out.println("文件已找到：" + filePath);
            if (file.delete()) {
                System.out.println("文件删除成功：" + filePath);
            } else {
                throw new IOException("删除文件失败：" + filePath);
            }
        } else {
            throw new IOException("文件未找到：" + filePath);
        }
    }


}

