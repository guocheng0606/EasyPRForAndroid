/***********************************************************
 * author   colin
 * company  fosung
 * email    wanglin2046@126.com
 * date     16-7-15 下午4:41
 **********************************************************/

package com.fosung.libeasypr.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 文件操作工具类
 */
public class FileUtil {

    /**
     * 重命名文件, 在同级目录下成功，否则失败
     *
     * @param sourcePath 源文件路径
     * @param targetPaht 目标文件路径
     */
    public static void renameFileInSameDir(String sourcePath, String targetPaht) {
        File fileSource = new File(sourcePath);
        File fileTarget = new File(targetPaht);
        renameFileInSameDir(fileSource, fileTarget);
    }

    /**
     * 重命名文件, 在同级目录下成功，否则失败
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     */
    public static void renameFileInSameDir(File sourceFile, File targetFile) {
        if (sourceFile.exists()) {
            sourceFile.renameTo(targetFile);
        }
    }

    /**
     * 删除文件 （如果是文件夹，遍历删除文件夹下的文件）
     *
     * @param file 要删除的文件对象
     */
    public static void delete(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        delete(files[i]);
                    }
                }
                file.delete();
            }
        }
    }

    /**
     * 复制文件
     *
     * @param sourceDir 源文件路径
     * @param targetDir 目标文件路径
     */
    public static void copyFile(String sourceDir, String targetDir) throws IOException {
        File fileSource = new File(sourceDir);
        File fileTarget = new File(targetDir);
        copyFile(fileSource, fileTarget);
    }

    /**
     * 复制文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        InputStream input = new FileInputStream(sourceFile);
        copyFile(input, targetFile);
    }

    /**
     * 复制文件
     *
     * @param input      文件输入流
     * @param targetFile 目标文件
     */
    public static void copyFile(InputStream input, File targetFile) throws IOException {
        checkFilePath(targetFile, false);
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;

        try {
            inBuff = new BufferedInputStream(input);
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }

            outBuff.flush();
        } finally {
            colseStreamWithCatchException(inBuff);
            colseStreamWithCatchException(outBuff);
        }
    }

    /**
     * 复制文件夹
     *
     * @param sourceDir 源文件夹
     * @param targetDir 目标文件夹
     */
    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
        copyDirectiory(sourceDir, targetDir, null);
    }

    /**
     * 复制文件夹
     *
     * @param sourceDir 源文件夹
     * @param targetDir 目标文件夹
     * @param arrExcept 需要剔除的文件路径
     */
    public static void copyDirectiory(String sourceDir, String targetDir, String[] arrExcept) throws IOException {
        // 新建目标目录
        File fileDir = new File(targetDir);
        checkFilePath(fileDir, true);
        // 获取源文件夹当前下的文件或目录
        File fileSource = new File(sourceDir);
        File[] files = fileSource.listFiles();
        if (files != null) {
            for (File file : files) {
                // 需要剔除的文件列表
                if (arrExcept != null && arrExcept.length > 0) {
                    boolean flag = false;
                    for (String strExc : arrExcept) {
                        if (strExc.equals(file.getAbsolutePath())) {
                            flag = true;
                            break;
                        }
                    }

                    if (flag) {
                        continue;
                    }
                }

                if (file.isFile()) {
                    File sourceFile = file;
                    File targetFile = new File(fileDir.getAbsolutePath() + File.separator + file.getName());
                    copyFile(sourceFile, targetFile);
                } else if (file.isDirectory()) {
                    /*递归复制文件夹*/
                    String dir1 = sourceDir + File.separator + file.getName();
                    String dir2 = targetDir + File.separator + file.getName();
                    copyDirectiory(dir1, dir2, arrExcept);
                }
            }
        }
    }

    /**
     * 从Raw文件夹复制到指定路径
     *
     * @param filePath 文件路径
     * @param rawID    文件资源
     * @return 是否拷贝成功
     */
    public static boolean copyFileFromRaw(Context context, String filePath, int rawID) {
        boolean flag = false;
        File file = new File(filePath);
        if (!file.exists()) {
            InputStream input;
            try {
                input = context.getResources()
                               .openRawResource(rawID);
                copyFile(input, file);
                flag = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 创建文件 当文件不存在的时候就创建一个文件，否则直接返回文件
     */
    public static File createFile(String path) {
        File file = new File(path);
        checkFilePath(file, false);
        // 创建目标文件
        try {
            if (!file.exists()) {
                file.createNewFile();
                return file;
            } else {
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从Assets文件夹复制到指定路径
     *
     * @param assetDir  原文件夹路径
     * @param targetDir 目标文件夹路径
     */
    public static void copyDirFromAssets(Context context, String assetDir, String targetDir) {
        String[] files;
        try {
            files = context.getResources()
                           .getAssets()
                           .list(assetDir);
        } catch (IOException e1) {
            return;
        }
        checkFilePath(new File(targetDir), true);
        for (String fileName : files) {
            if (!fileName.contains(".")) {
                copyDirFromAssets(context, assetDir + "/" + fileName, targetDir + "/" + fileName);
            } else {
                copyFileFromAssets(context, assetDir + "/" + fileName, targetDir + "/" + fileName);
            }
        }
    }

    /**
     * 从 Assets中拷贝文件
     *
     * @param assetPath  原文件路径
     * @param targetPath 目标文件夹路径
     */
    public static void copyFileFromAssets(Context context, String assetPath, String targetPath) {
        File outFile = new File(targetPath);
        InputStream in;
        try {
            in = context.getAssets()
                        .open(assetPath);
            copyFile(in, outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将内容读取成字符串
     *
     * @param in 输入流
     * @return 从文件读取的内容
     */
    public static String readFileStr(InputStream in) {
        BufferedReader br = null;
        StringBuffer strbuf = new StringBuffer();
        try {
            br = new BufferedReader(new InputStreamReader(in));
            String str = null;
            while ((str = br.readLine()) != null) {
                strbuf.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            colseStreamWithCatchException(br);
        }
        return strbuf.toString();
    }

    /**
     * 将文件内容读取成字符串
     *
     * @param path 文件路径
     * @return 从文件读取的内容
     */
    public static String readFileStr(String path) {
        File file = new File(path);
        BufferedReader br = null;
        StringBuffer strbuf = new StringBuffer();
        if (file.exists()) {
            try {
                //可以换成工程目录下的其他文本文件
                br = new BufferedReader(new FileReader(file));
                String str = null;
                while ((str = br.readLine()) != null) {
                    strbuf.append(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                colseStreamWithCatchException(br);
            }
        }
        return strbuf.toString();
    }

    /**
     * 将字符串写入文件
     *
     * @param path 文件路径
     * @param str  要写入的内容
     * @return 是否写入成功
     */
    public static boolean writeFileStr(String path, String str) {
        return writeFileStr(path, str, false);
    }

    /**
     * 将字符串写入文件
     *
     * @param path     文件路径
     * @param str      要写入的内容
     * @param isAppend 是否追加到文件中
     * @return 是否写入成功
     */
    public static boolean writeFileStr(String path, String str, boolean isAppend) {
        boolean isSucc = false;
        File file = new File(path);
        checkFilePath(file, false);
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new FileWriter(file, isAppend));
            if (isAppend) {
                br.newLine();
            }
            br.write(str);
            br.flush();
            isSucc = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            colseStreamWithCatchException(br);
        }
        return isSucc;
    }

    /**
     * 将文件读入到字节数组中
     *
     * @param f     文件对象
     * @param start 开始读取的位置
     * @param len   读取的长度
     * @return 存储读取内容的字节数组
     */
    public static byte[] readToByteArray(File f, long start, int len) {
        byte[] ib = new byte[len];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);// pathStr 文件路径
            fis.skip(start);
            fis.read(ib, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            colseStreamWithCatchException(fis);
        }
        return ib;
    }

    /**
     * 将文件读入到字节数组中
     *
     * @param f 文件对象
     * @return 存储读取内容的字节数组
     */
    public static byte[] readToByteArray(File f) {
        ByteArrayOutputStream out = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            out = new ByteArrayOutputStream();

            byte[] byteArrary = new byte[1024];
            int count;
            while ((count = fis.read(byteArrary)) != -1) {
                out.write(byteArrary, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            colseStreamWithCatchException(fis);
            colseStreamWithCatchException(out);
        }
        return out.toByteArray();
    }

    /**
     * 从Uri中获取path
     *
     * @param uri Uri
     * @return 读取的内容
     */
    public static String getPathFroUri(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                                .query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return "";
    }

    /**
     * 检查目录是否存在，不存在创建
     *
     * @param path  文件路径
     * @param isDir 是否是文件夹
     */
    public static void checkFilePath(String path, boolean isDir) {
        File file = new File(path);
        checkFilePath(file, isDir);
    }

    /**
     * 检查目录是否存在，不存在创建
     *
     * @param file  File对象
     * @param isDir 是否是文件夹
     */
    public static void checkFilePath(File file, boolean isDir) {
        if (file != null) {
            if (!isDir) {
                file = file.getParentFile();
            }

            if (file != null && !file.exists()) {
                file.mkdirs();
            }
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件路径
     * @return 文件是否存在
     */
    public static boolean exist(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 解压一个压缩文档 到指定位置
     *
     * @param zipFilePath 压缩包的路径
     * @param outPath     解压指定的路径
     */
    public static void unZipFolder(String zipFilePath, String outPath) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry zipEntry;
        String szName = "";

        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPath + File.separator + szName);
                folder.mkdirs();
            } else {

                File file = new File(outPath + File.separator + szName);
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }//end of while

        inZip.close();
    }

    /**
     * 关闭输入/输出流，捕获异常不抛出
     */
    public static void colseStreamWithCatchException(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
