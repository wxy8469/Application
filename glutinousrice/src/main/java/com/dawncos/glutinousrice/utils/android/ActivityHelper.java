package com.dawncos.glutinousrice.utils.android;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.dawncos.glutinousrice.base.android.GlutinousRiceModule;
import com.dawncos.glutinousrice.base.android.application.IAppLifecycle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * -----------------------------------------------
 * 管理所有 {@link Activity}包括前台，也可以通过 {@link #postEventBus(Message)} ,
 * 远程遥控执行对应方法,用法EventBus实现--工具类
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
@Singleton
public final class ActivityHelper {

    public static final int START_ACTIVITY = 0x01;
    public static final int SHOW_SNACKBAR = 0x02;
    public static final int KILL_ALLACTIVITY = 0x03;
    public static final int APP_EXIT = 0x04;
    public static final String IS_NOT_ADD_ACTIVITY_LIST = "is_not_add_activity_list";
    @Inject
    Application mApplication;
    //管理所有存活的 Activity, 容器中的顺序仅仅是 Activity 的创建顺序, 并不能保证和 Activity 任务栈顺序一致
    private List<Activity> mActivityList;
    //当前在前台的 Activity
    private Activity mCurrentActivity;
    //提供给外部扩展 ActivityHelper 的 onReceive 方法
    private HandleListener mHandleListener;

    @Inject
    public ActivityHelper() {
        EventBus.getDefault().register(this);
        Timber.d("EventBus be registered!");
    }

    /**
     * 通过 {@link EventBus#post(Object)} 事件, 远程遥控执行对应方法
     * 可通过 {@link #setHandleListener(HandleListener)}, 让外部可扩展新的事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        Timber.d("onEventBus %s", message);
        switch (message.what) {
            case START_ACTIVITY:
                if (message.obj == null)
                    break;
                dispatchStart(message);
                break;
            case SHOW_SNACKBAR:
                if (message.obj == null)
                    break;
                showSnackbar((String) message.obj, message.arg1 != 0);
                break;
            case KILL_ALLACTIVITY:
                if (message.obj == null)
                    break;
                killAllActivity();
                break;
            case APP_EXIT:
                if (message.obj == null)
                    break;
                appExit();
                break;
            default:
                Timber.d("The message.what not match");
                break;
        }
        //外部扩展
        if (mHandleListener != null) {
            mHandleListener.handleMessage(this, message);
        }
    }

    public HandleListener getHandleListener() {
        return mHandleListener;
    }

    /**
     * 提供给外部扩展 {@link ActivityHelper} 的 {@link #onEventBus} 方法(远程遥控 {@link ActivityHelper} 的功能)
     * 建议在 {@link GlutinousRiceModule#expandApp(Context, List)} 中
     * 通过 {@link IAppLifecycle#onCreate(Application)} 在 IApplication 初始化时,使用此方法传入自定义的 {@link HandleListener}
     *
     * @param handleListener
     */
    public void setHandleListener(HandleListener handleListener) {
        this.mHandleListener = handleListener;
    }

    private void dispatchStart(Message message) {
        if (message.obj instanceof Intent)
            startActivity((Intent) message.obj);
        else if (message.obj instanceof Class)
            startActivity((Class) message.obj);
    }

    public interface HandleListener {
        void handleMessage(ActivityHelper ActivityHelper, Message message);
    }

    /**
     * 通过此方法远程遥控 {@link ActivityHelper} ,使 {@link #onEventBus(Message)} 执行对应方法
     *
     * @param msg
     */
    public static void postEventBus(Message msg) {
        EventBus.getDefault().post(msg);
    }

    /**
     * 让在前台的 {@link Activity},使用 {@link Snackbar} 显示文本内容
     *
     * @param message
     * @param isLong
     */
    public void showSnackbar(String message, boolean isLong) {
        if (getCurrentActivity() == null) {
            Timber.e("mCurrentActivity == null when showSnackbar(String,boolean)");
            return;
        }
        View view = getCurrentActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(view, message, isLong ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT).show();
    }


    /**
     * 让在栈顶的 {@link Activity} ,打开指定的 {@link Activity}
     *
     * @param intent
     */
    public void startActivity(Intent intent) {
        if (getTopActivity() == null) {
            Timber.e("mCurrentActivity == null when startActivity(Intent)");
            //如果没有前台的activity就使用new_task模式启动activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApplication.startActivity(intent);
            return;
        }
        getTopActivity().startActivity(intent);
    }

    /**
     * 让在栈顶的 {@link Activity} ,打开指定的 {@link Activity}
     *
     * @param activityClass
     */
    public void startActivity(Class activityClass) {
        startActivity(new Intent(mApplication, activityClass));
    }

    /**
     * 将在前台的 {@link Activity} 赋值给 {@code currentActivity}, 注意此方法是在 {@link Activity#onResume} 方法执行时将栈顶的 {@link Activity} 赋值给 {@code currentActivity}
     * 所以在栈顶的 {@link Activity} 执行 {@link Activity#onCreate} 方法时使用 {@link #getCurrentActivity()} 获取的就不是当前栈顶的 {@link Activity}, 可能是上一个 {@link Activity}
     * 如果在 IApplication 启动第一个 {@link Activity} 执行 {@link Activity#onCreate} 方法时使用 {@link #getCurrentActivity()} 则会出现返回为 {@code null} 的情况
     * 想避免这种情况请使用 {@link #getTopActivity()}
     *
     * @param currentActivity
     */
    public void setCurrentActivity(Activity currentActivity) {
        this.mCurrentActivity = currentActivity;
    }

    /**
     * 获取在前台的 {@link Activity} (保证获取到的 {@link Activity} 正处于可见状态, 即未调用 {@link Activity#onStop()}), 获取的 {@link Activity} 存续时间
     * 是在 {@link Activity#onStop()} 之前, 所以如果当此 {@link Activity} 调用 {@link Activity#onStop()} 方法之后, 没有其他的 {@link Activity} 回到前台(用户返回桌面或者打开了其他 IApplication 会出现此状况)
     * 这时调用 {@link #getCurrentActivity()} 有可能返回 {@code null}, 所以请注意使用场景和 {@link #getTopActivity()} 不一样
     * <p>
     * Example usage:
     * 使用场景比较适合, 只需要在可见状态的 {@link Activity} 上执行的操作
     * 如当后台 {@link Service} 执行某个任务时, 需要让前台 {@link Activity} ,做出某种响应操作或其他操作,如弹出 {@link Dialog}, 这时在 {@link Service} 中就可以使用 {@link #getCurrentActivity()}
     * 如果返回为 {@code null}, 说明没有前台 {@link Activity} (用户返回桌面或者打开了其他 IApplication 会出现此状况), 则不做任何操作, 不为 {@code null}, 则弹出 {@link Dialog}
     *
     * @return
     */
    public Activity getCurrentActivity() {
        return mCurrentActivity != null ? mCurrentActivity : null;
    }

    /**
     * 获取最近启动的一个 {@link Activity}, 此方法不保证获取到的 {@link Activity} 正处于前台可见状态
     * 即使 IApplication 进入后台或在这个 {@link Activity} 中打开一个之前已经存在的 {@link Activity}, 这时调用此方法
     * 还是会返回这个最近启动的 {@link Activity}, 因此基本不会出现 {@code null} 的情况
     * 比较适合大部分的使用场景, 如 startActivity
     * <p>
     * Tips: mActivityList 容器中的顺序仅仅是 Activity 的创建顺序, 并不能保证和 Activity 任务栈顺序一致
     *
     * @return
     */
    public Activity getTopActivity() {
        if (mActivityList == null) {
            Timber.e("mActivityList == null when getTopActivity()");
            return null;
        }
        return mActivityList.size() > 0 ? mActivityList.get(mActivityList.size() - 1) : null;
    }


    /**
     * 返回一个存储所有未销毁的 {@link Activity} 的集合
     *
     * @return
     */
    public List<Activity> getActivityList() {
        if (mActivityList == null) {
            mActivityList = new LinkedList<>();
        }
        return mActivityList;
    }


    /**
     * 添加 {@link Activity} 到集合
     */
    public void addActivity(Activity activity) {
        synchronized (ActivityHelper.class) {
            List<Activity> activities = getActivityList();
            if (!activities.contains(activity)) {
                activities.add(activity);
            }
        }
    }

    /**
     * 删除集合里的指定的 {@link Activity} 实例
     *
     * @param {@link Activity}
     */
    public void removeActivity(Activity activity) {
        if (mActivityList == null) {
            Timber.e("mActivityList == null when removeActivity(Activity)");
            return;
        }
        synchronized (ActivityHelper.class) {
            if (mActivityList.contains(activity)) {
                mActivityList.remove(activity);
            }
        }
    }

    /**
     * 删除集合里的指定位置的 {@link Activity}
     *
     * @param location
     */
    public Activity removeActivity(int location) {
        if (mActivityList == null) {
            Timber.e("mActivityList == null when removeActivity(int)");
            return null;
        }
        synchronized (ActivityHelper.class) {
            if (location > 0 && location < mActivityList.size()) {
                return mActivityList.remove(location);
            }
        }
        return null;
    }

    /**
     * 关闭指定的 {@link Activity} class 的所有的实例
     *
     * @param activityClass
     */
    public void killActivity(Class<?> activityClass) {
        if (mActivityList == null) {
            Timber.e("mActivityList == null when killActivity(Class)");
            return;
        }
        synchronized (ActivityHelper.class) {
            Iterator<Activity> iterator = getActivityList().iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next();

                if (next.getClass().equals(activityClass)) {
                    iterator.remove();
                    next.finish();
                }
            }
        }
    }


    /**
     * 指定的 {@link Activity} 实例是否存活
     *
     * @param {@link Activity}
     * @return
     */
    public boolean activityInstanceIsLive(Activity activity) {
        if (mActivityList == null) {
            Timber.e("mActivityList == null when activityInstanceIsLive(Activity)");
            return false;
        }
        return mActivityList.contains(activity);
    }


    /**
     * 指定的 {@link Activity} class 是否存活(同一个 {@link Activity} class 可能有多个实例)
     *
     * @param activityClass
     * @return
     */
    public boolean activityClassIsLive(Class<?> activityClass) {
        if (mActivityList == null) {
            Timber.e("mActivityList == null when activityClassIsLive(Class)");
            return false;
        }
        for (Activity activity : mActivityList) {
            if (activity.getClass().equals(activityClass)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取指定 {@link Activity} class 的实例,没有则返回 null(同一个 {@link Activity} class 有多个实例,则返回最早创建的实例)
     *
     * @param activityClass
     * @return
     */
    public Activity findActivity(Class<?> activityClass) {
        if (mActivityList == null) {
            Timber.e("mActivityList == null when findActivity(Class)");
            return null;
        }
        for (Activity activity : mActivityList) {
            if (activity.getClass().equals(activityClass)) {
                return activity;
            }
        }
        return null;
    }


    /**
     * 关闭所有 {@link Activity}
     */
    public void killAllActivity() {
        synchronized (ActivityHelper.class) {
            Iterator<Activity> iterator = getActivityList().iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next();
                iterator.remove();
                next.finish();
            }
        }
    }

    /**
     * 关闭所有 {@link Activity},排除指定的 {@link Activity}
     *
     * @param excludeActivityClasses activity class
     */
    public void killAllActivity(Class<?>... excludeActivityClasses) {
        List<Class<?>> excludeList = Arrays.asList(excludeActivityClasses);
        synchronized (ActivityHelper.class) {
            Iterator<Activity> iterator = getActivityList().iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next();

                if (excludeList.contains(next.getClass()))
                    continue;

                iterator.remove();
                next.finish();
            }
        }
    }

    /**
     * 退出应用程序
     * <p>
     * 此方法经测试在某些机型上并不能完全杀死 App 进程, 几乎试过市面上大部分杀死进程的方式, 但都发现没卵用, 所以此
     * 方法如果不能百分之百保证能杀死进程, 就不能贸然调用 {@link #release()} 释放资源, 否则会造成其他问题, 如果您
     * 有测试通过的并能适用于绝大多数机型的杀死进程的方式, 望告知
     */
    public void appExit() {
        try {
            killAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        EventBus.getDefault().unregister(this);
        mActivityList.clear();
        mHandleListener = null;
        mActivityList = null;
        mCurrentActivity = null;
        mApplication = null;
    }
}