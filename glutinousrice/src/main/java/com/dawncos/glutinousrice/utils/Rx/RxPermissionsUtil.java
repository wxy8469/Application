package com.dawncos.glutinousrice.utils.Rx;

import android.Manifest;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

/**
 * -----------------------------------------------
 * 权限请求--工具类
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public class RxPermissionsUtil {

    private RxPermissionsUtil() {
        throw new RuntimeException("initialization failed!");
    }

    public static void requestPermission(final PermissionListener permissionListener, RxPermissions rxPermissions, String... permissions) {
        if (permissions == null || permissions.length == 0) return;

        //过滤调已经申请过的权限
        List<String> applyList = new ArrayList<>();
        for (String permission : permissions) {
            if (!rxPermissions.isGranted(permission)) {
                applyList.add(permission);
            }
        }

        if (applyList.isEmpty()) {
            permissionListener.onPermissionApplied();
        } else {
            rxPermissions
                    .requestEach(applyList.toArray(new String[applyList.size()]))
                    .buffer(permissions.length)
                    .subscribe(p -> {
                        for (Permission per : p) {
                            if (!per.granted) {
                                if (per.shouldShowRequestPermissionRationale) {
                                    Timber.e("Request permissions failure");
                                    permissionListener.onPermissionApplyFailure(Arrays.asList(per.name));
                                    return;
                                } else {
                                    Timber.e("Request permissions failure with ask never again");
                                    permissionListener.onPermissionApplyFailureAndNeverAsk(Arrays.asList(per.name));
                                    return;
                                }
                            }
                        }
                        Timber.e("Request permissions success");
                        permissionListener.onPermissionApplySuccess();
                    });
        }

    }


    /**
     * 请求摄像头权限
     */
    public static void launchCamera(PermissionListener permissionListener, RxPermissions rxPermissions) {
        requestPermission(permissionListener, rxPermissions, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }


    /**
     * 请求外部存储的权限
     */
    public static void externalStorage(PermissionListener permissionListener, RxPermissions rxPermissions) {
        requestPermission(permissionListener, rxPermissions, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    /**
     * 请求发送短信权限
     */
    public static void sendSms(PermissionListener permissionListener, RxPermissions rxPermissions) {
        requestPermission(permissionListener, rxPermissions, Manifest.permission.SEND_SMS);
    }


    /**
     * 请求打电话权限
     */
    public static void callPhone(PermissionListener permissionListener, RxPermissions rxPermissions) {
        requestPermission(permissionListener, rxPermissions, Manifest.permission.CALL_PHONE);
    }


    /**
     * 请求获取手机状态的权限
     */
    public static void readPhonestate(PermissionListener permissionListener, RxPermissions rxPermissions) {
        requestPermission(permissionListener, rxPermissions, Manifest.permission.READ_PHONE_STATE);
    }

    public interface PermissionListener {

        void onPermissionApplied();

        void onPermissionApplySuccess();
        
        void onPermissionApplyFailure(List<String> permissions);
        
        void onPermissionApplyFailureAndNeverAsk(List<String> permissions);
    }
}

