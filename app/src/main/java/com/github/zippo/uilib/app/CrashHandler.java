package com.github.zippo.uilib.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrashHandler implements UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    // CrashHandler 实例
    private static CrashHandler INSTANCE = new CrashHandler();

    // 程序的 Context 对象
    private Context mContext;

    // 系统默认的 UncaughtException 处理类
    private UncaughtExceptionHandler mDefaultHandler;

    // 用来存储设备信息和异常信息
    private Map<String, String> mInfos = new HashMap<>();

    // 用于格式化日期,作为日志文件名的一部分
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());

    /**
     * 保证只有一个 CrashHandler 实例
     */
    private CrashHandler() {
    }

    /**
     * 获取 CrashHandler 实例 ,单例模式
     */
    static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context 上下文环境
     */
    void init(Context context) {
        mContext = context;

        // 获取系统默认的 UncaughtException 处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 设置该 CrashHandler 为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当 UncaughtException 发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }

            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
     *
     * @param ex 异常封装类
     * @return true：如果处理了该异常信息；否则返回 false
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }

        // 使用 Toast 来显示异常信息
//		new Thread() {
//			@Override
//			public void run() {
        Toast.makeText(mContext, "很抱歉，程序出现异常，即将退出。", Toast.LENGTH_LONG)
                .show();
//			}
//		}.start();

        // 收集设备参数信息
        collectDeviceInfo(mContext);

        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx 上下文环境
     */
    private void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);

            if (pi != null) {
                String versionName = pi.versionName == null ? "unknown"
                        : pi.versionName;
                String versionCode = String.valueOf(pi.versionCode);
                mInfos.put("versionName", versionName);
                mInfos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mInfos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中 *
     *
     * @param ex 异常封装类
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String saveCrashInfo2File(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : mInfos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();

        final String result = writer.toString();
        sb.append(result);

        Log.e(TAG, "异常--------------" + result);
        // 上传日志文件
//        ServerService server = ServerService.getService(null, ServerService.TYPE_MOTOUCH);
//        server.submitException(sb.toString(), null);

        try {
            long timestamp = System.currentTimeMillis();
            String time = mFormatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";

            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                String path = Environment.getExternalStorageDirectory().getPath() + "crash/xueche/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }

            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

}