package com.alphen.mall.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/*
* 生成二维码工具
* */
public class QRCodeGenerator {
    public static void generatorQRCodeImage(String text,int width,int height,String filePath)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        //生成编码后结果，由比特矩阵类接收
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE,width,height);
        Path path = FileSystems.getDefault().getPath(filePath);
        //将矩阵转换为图片
        MatrixToImageWriter.writeToPath(bitMatrix,"PNG",path);
    }
}
