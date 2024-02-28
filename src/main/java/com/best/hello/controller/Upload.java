package com.best.hello.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Api("上传漏洞")
@Slf4j
@RestController
@RequestMapping("/UPLOAD")
public class Upload {

    private static final String UPLOADED_FOLDER = System.getProperty("user.dir") + "/src/main/resources/static/file/";


//    @GetMapping("/upload")
//    public String uploadStatus() {
//        return "upload";
//    }

    @ApiOperation(value = "vul：上传任意文件")
    @PostMapping("/uploadVul")
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {
        File dir = new File(UPLOADED_FOLDER);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File filePath = new File(UPLOADED_FOLDER + file.getOriginalFilename());
        log.info("[vul] 上传文件：" + filePath.getPath());
        file.transferTo(filePath);

        return "文件上传路径: " + filePath.getPath();
    }

    @ApiOperation(value = "vul：indexOf截取")
    @PostMapping("/uploadVul2")
    public String singleFileUpload2(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {

        //白名单校验
        String[] extsList = new String[]{".jpg", ".png", ".doc", ".docx"};
        // 获取文件后缀
        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.indexOf("."));
        boolean flag = true;
        for (String str : extsList) {
            if (ext.contains(str)) { // 上传文件后缀名存在于白名单中，则返回true
                flag = false;
            }
        }
        if(flag){return "文件名非法！";}

        File dir = new File(UPLOADED_FOLDER);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File filePath = new File(UPLOADED_FOLDER + fileName);
        log.info("[vul] 上传文件：" + filePath.getPath());
        file.transferTo(filePath);

        return "文件上传路径: " + filePath.getPath();
    }


    @ApiOperation(value = "vul：Windows")
    @PostMapping("/uploadVul3")
    public String singleFileUpload3(@RequestParam("file") MultipartFile file,
                                    RedirectAttributes redirectAttributes) throws IOException {
        //白名单校验
        String[] extsList = new String[]{"jpg", "png", "doc", "docx"};
        // 获取文件后缀
        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".")+1);
        // 判断后缀是否为空，如果为空则不校验白名单
        if (!ext.isEmpty() && !Arrays.asList(extsList).contains(ext)) {
            return ("上传文件非法！");
        }

        File filePath = new File(UPLOADED_FOLDER + fileName);
        log.info("[vul] 上传文件：" + filePath.getPath());
        file.transferTo(filePath);

        File dirlist = new File(UPLOADED_FOLDER);
        String[] list = dirlist.list();
        String filenameList = "";
        for(String name : list){
            filenameList += "\n\r" + name;
        }

        return "上传文件目录文件列表: " + filenameList.toString();
    }


    /*@ApiOperation(value = "safe：白名单后缀名处理")
    @PostMapping("/uploadSafe")
    public String singleFileUploadSafe(@RequestParam("file") MultipartFile file,
                                       RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "请选择要上传的文件");
            return "redirect:upload";
        }

        try {
            byte[] bytes = file.getBytes();
            String fileName = file.getOriginalFilename();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());

            // 获取文件后缀名，并且索引到最后一个，避免使用.jpg.jsp来绕过
            assert fileName != null;
            String Suffix = fileName.substring(fileName.lastIndexOf("."));

            String[] SuffixSafe = {".jpg", ".png", ".jpeg", ".gif", ".bmp", ".ico"};
            boolean flag = false;

            // 如果满足后缀名单，返回true
            for (String s : SuffixSafe) {
                if (Suffix.toLowerCase().equals(s)) {
                    flag = true;
                    break;
                }
            }

            log.info("[safe] 上传漏洞-白名单模式：" + fileName);

            if (!flag) {
                redirectAttributes.addFlashAttribute("message",
                        "只允许上传图片，[.jpg, .png, .jpeg, .gif, .bmp, .ico]");
            } else {
                Files.write(path, bytes);
                redirectAttributes.addFlashAttribute("message",
                        "上传文件成功：" + path + "");
            }

        } catch (Exception e) {
            return e.toString();
        }
        return "redirect:upload";
    }*/

    @ApiOperation(value = "safe：白名单后缀名处理")
    @PostMapping("/uploadSafe")
    public String singleFileUploadSafe(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {

        String filePath = UPLOADED_FOLDER + file.getOriginalFilename();
        if(!checkFile(filePath)){
            return "上传文件非法！";
        }
        return "文件上传成功！";
    }
    public boolean checkFile(String file){
        //处理截断问题
        file = file.replaceAll("\\p{C}", "");
        if(file.contains("..")){
            return false;
        }
        //文件后缀白名单校验
        String ext = file.substring(file.lastIndexOf("."),file.length());
        List<String> exts = Arrays.asList(".jpg", ".png", ".jpeg", ".gif");
        return exts.contains(ext);
    }
}
