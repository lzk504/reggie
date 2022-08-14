package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件的上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file 是一个临时文件，需要转存到指定位置，否则请求完成后临时文件会删除
//        log.info(file.toString());
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //截取.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID重新生成文件名，防止文件名重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;
        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()) {
            //不存在就创建目录
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //向页面返回文件名称
        return R.success(fileName);
    }

    /**
     * 文件下载
     *
     * @param response
     * @param name
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws Exception {
//            读取文件内容


        FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

//        写入到浏览器
        ServletOutputStream outputStream = response.getOutputStream();
//        设置响应方式
        response.setContentType("image/jpeg");

//            读取一行一行
        int len = 0;

        byte[] bytes = new byte[1024];

        while ((len = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, len);
            outputStream.flush();
        }
        //关闭资源
        outputStream.close();
        fileInputStream.close();
    }
}
