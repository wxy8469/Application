package com.dawncos.android.di.component;

import com.dawncos.android.home.activity.UserActivity;
import com.dawncos.android.di.module.UserModule;
import com.dawncos.dcmodule.base.dagger2.component.AppComponent;
import com.dawncos.dcmodule.base.dagger2.scope.ActivityScope;

import dagger.Component;

/**
 * ================================================
 * 展示 Component 的用法
 *
 * @see <a href="https://github.com/JessYanCoding/MVPArms/wiki#2.4.6">Component wiki 官方文档</a>
 * Created by JessYan on 09/04/2016 11:17
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
@ActivityScope
@Component(modules = UserModule.class, dependencies = AppComponent.class)
public interface UserComponent {
    void inject(UserActivity activity);
}
