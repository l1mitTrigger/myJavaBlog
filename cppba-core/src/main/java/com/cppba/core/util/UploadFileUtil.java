package com.cppba.core.util;

import com.cppba.core.constant.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author Administrator
 *
 */
public class UploadFileUtil {
	private static Logger logger = LoggerFactory.getLogger(UploadFileUtil.class);
	/**
	 * base64 --  文件base64编码 "iVBORw0KGgoAAAAN......"
	 * path -- 上传路径 "/Image/article"
	 * ext -- 拓展名   "png"
	 * @throws Exception 
	 */
	public static String base64ToFile(String base64,String path,String ext) throws Exception{
		String root = Globals.getFileSystemPath()+path;
		//创建文件夹  
        File dirFile = new File(root);  
        if (!dirFile.exists()) {
            dirFile.mkdirs();  
        }
		String name=UUID.randomUUID().toString()+"."+ext;
		BASE64Decoder decoder = new BASE64Decoder();
		FileOutputStream write = new FileOutputStream(new File(root+"/"+name));
		byte[] decoderBytes = decoder.decodeBuffer(base64);
		write.write(decoderBytes);
		write.close();
		//生成中等缩略图
		ImgCompressUtil.createImgCompress(root+"/"+name,1);
		//生成小等缩略图
		ImgCompressUtil.createImgCompress(root+"/"+name,2);
		return name;
	}

	// 图片上传
	public static String uploadImg(MultipartFile file, String path) {
		String root = Globals.getFileSystemPath() + path;
		// 创建文件夹
		File dirFile = new File(root);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		// 获得图片后缀名
		String imgName = file.getOriginalFilename();
		String fileExt = imgName.substring(imgName.lastIndexOf(".")).toLowerCase();

		InputStream is;
		String name = UUID.randomUUID().toString()+fileExt;
		try {
			is = file.getInputStream();
			File descfile = new File(root, name);
			OutputStream os = new FileOutputStream(descfile);
			byte[] buffer = new byte[400];
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
			}
			os.close();
			is.close();
			// 生成中等缩略图
			ImgCompressUtil.createImgCompress(root + "/" + name, 1);
			// 生成小等缩略图
			ImgCompressUtil.createImgCompress(root + "/" + name, 2);
			logger.info("{} 图片上传成功为 {}",imgName,name);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return name;
	}
}