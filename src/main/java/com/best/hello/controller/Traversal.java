package com.best.hello.controller;

import com.best.hello.util.Security;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @date 2021/07/15
 */
@Api("目录遍历")
@RestController
@RequestMapping("/Traversal")
public class Traversal {
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

