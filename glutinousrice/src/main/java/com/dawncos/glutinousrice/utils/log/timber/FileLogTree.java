package com.dawncos.glutinousrice.utils.log.timber;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dawncos.glutinousrice.BuildConfig;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * -----------------------------------------------
 * 打印日志FileLogTree--工具类
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public class FileLogTree extends Timber.DebugTree {

    private static final String LOG_TAG = FileLogTree.class.getSimpleName();

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        try {
            String path = "Log";
            String fileNameTimeStamp = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.getDefault()).format(new Date());
            String logTimeStamp = new SimpleDateFormat("E yyyy MM dd 'at' hh:mm:ss:SSS aaa",
                    Locale.getDefault()).format(new Date());
            String fileName = fileNameTimeStamp + ".html";

            File file  = generateFile(path, fileName);

            if (file != null) {
                FileWriter writer = new FileWriter(file, true);
                writer.append("<p style=\"background:lightgray;\"><strong "
                        + "style=\"background:lightblue;\">&nbsp&nbsp")
                        .append(logTimeStamp)
                        .append(" :&nbsp&nbsp</strong><strong>&nbsp&nbsp")
                        .append(tag)
                        .append("</strong> - ")
                        .append(message)
                        .append("</p>");
                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG,"Error while logging into file : " + e);
        }
    }

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        // Add log statements line number to the log
        return super.createStackElementTag(element) + " - " + element.getLineNumber();
    }

    @Nullable
    private static File generateFile(@NonNull String path, @NonNull String fileName) {
        File file = null;
        if (isExternalStorageAvailable()) {
            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                    BuildConfig.APPLICATION_ID + File.separator + path);

            boolean dirExists = true;

            if (!root.exists()) {
                dirExists = root.mkdirs();
            }

            if (dirExists) {
                file = new File(root, fileName);
            }
        }
        return file;
    }

    private static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}