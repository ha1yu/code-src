package com.best.hello.controller;

import com.best.hello.util.Security;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @date 2021/07/15
 */
@Api("目录遍历")
@RestController
@RequestMapping("/Traversal")
public class Traversal extends HttpServlet {
    Logger log = LoggerFactory.getLogger(Traversal.class);

    @ApiOperation(value = "vul：任意文件下载")
    @GetMapping("/download")
    public String download(String filename, HttpServletResponse response) {
        // 下载的文件路径
        String filePath = System.getProperty("user.dir") + "/logs/" + filename;
        log.info("[vul] 任意文件下载：" + filePath);

        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)))) {
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.setContentLength((int) Files.size(Paths.get(filePath)));
            response.setContentType("application/octet-stream");

            // 使用 Apache Commons IO 库的工具方法将输入流中的数据拷贝到输出流中
            IOUtils.copy(inputStream, response.getOutputStream());
            log.info("文件 {} 下载成功，路径：{}", filename, filePath);
            return "下载文件成功：" + filePath;
        } catch (IOException e) {
            log.error("下载文件失败，路径：{}", filePath, e);
            return "未找到文件：" + filePath;
        }
    }

    @ApiOperation(value = "vul：过滤../")
    @GetMapping("/download2")
    public String download2(String filename, HttpServletResponse response) {
        // 下载的文件路径
        String filePath = System.getProperty("user.dir") + "/logs/" + filename;
        log.info("[vul] 任意文件下载：" + filePath);

        //替换../
        filePath = filePath.replace("../","").replace("..\\","");

        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)))) {
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.setContentLength((int) Files.size(Paths.get(filePath)));
            response.setContentType("application/octet-stream");

            // 使用 Apache Commons IO 库的工具方法将输入流中的数据拷贝到输出流中
            IOUtils.copy(inputStream, response.getOutputStream());
            log.info("文件 {} 下载成功，路径：{}", filename, filePath);
            return "下载文件成功：" + filePath;
        } catch (IOException e) {
            log.error("下载文件失败，路径：{}", filePath, e);
            return "未找到文件：" + filePath;
        }
    }

    @ApiOperation(value = "vul：解压文件")
    @PostMapping("/unzip")
    public void unzip(@RequestParam("file") MultipartFile zipfile, HttpServletResponse response) throws Exception {
        // 获取文件名
        String fileName = zipfile.getOriginalFilename();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        File file = File.createTempFile(fileName, prefix);
        zipfile.transferTo(file);

        ZipFile zf = new ZipFile(file);
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
        ZipEntry entry;
        ServletOutputStream outputStream = response.getOutputStream();
        //遍历zip中的文件
        while((entry = zis.getNextEntry()) != null){
            //构造输出流
            String zipfilename = entry.getName();
            File outfile = new File(System.getProperty("user.dir") + "/src/main/resources/static/file/" + zipfilename);
            FileOutputStream fos = new FileOutputStream(outfile);
            //将zip中的内容读出
            InputStream inputStream = zf.getInputStream(entry);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            //写文件并关闭连接
            outputStream.write(("解压路径：" + outfile.getPath()).getBytes("gbk"));
            fos.write(bytes);
            fos.close();
            inputStream.close();
        }
    }


    /*@ApiOperation(value = "safe：过滤../")
    @GetMapping("/download/safe")
    public String safe(String filename) {
        if (!Security.checkTraversal(filename)) {
            String filePath = System.getProperty("user.dir") + "/logs/" + filename;
            return "安全路径：" + filePath;
        } else {
            return "检测到非法遍历！";
        }
    }*/

    @GetMapping("/download/safe")
    public String safe(String filename) throws Exception {
        String filePath = System.getProperty("user.dir") + "/logs/" + filename;
        if (checkFile(filePath)) {
            return "安全路径：" + filePath;
        } else {
            return "检测到非法遍历！";
        }
    }

    @PostMapping("/unzip/safe")
    public void unzipSafe(@RequestParam("file") MultipartFile zipfile, HttpServletResponse response) throws Exception {
        // 获取文件名
        String fileName = zipfile.getOriginalFilename();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        File file = File.createTempFile(fileName, prefix);
        zipfile.transferTo(file);

        ZipFile zf = new ZipFile(file);
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
        ZipEntry entry;
        ServletOutputStream outputStream = response.getOutputStream();
        //遍历zip中的文件
        while((entry = zis.getNextEntry()) != null){
            //构造输出流
            String zipfilename = entry.getName();
            String filepath = System.getProperty("user.dir") + "/src/main/resources/static/file/" + zipfilename;
            if (!checkFile(filepath) || !checkExt(filepath)){
                outputStream.write("解压失败，zip文件存在问题！".getBytes("GBK"));
                return;
            }
            File outfile = new File(filepath);
            FileOutputStream fos = new FileOutputStream(outfile);
            //将zip中的内容读出
            InputStream inputStream = zf.getInputStream(entry);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            //写文件并关闭连接
            outputStream.write(("解压路径：" + outfile.getPath()).getBytes("gbk"));
            fos.write(bytes);
            fos.close();
            inputStream.close();
        }
    }
    public boolean checkFile(String path) throws Exception {
        //处理截断问题
        path = path.replaceAll("\\p{C}", "");
        if(path.contains("..")){
            return false;
        }
        return true;
    }

    public boolean checkExt(String path){
        String ext = path.substring(path.lastIndexOf("."), path.length());
        List<String> exts = Arrays.asList(".jpg", ".png", ".jpeg", ".gif");
        return exts.contains(ext);
    }
}

