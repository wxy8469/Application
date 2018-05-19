package com.dawncos.cmodule.base.lifecycle;

import javax.annotation.Generated;

import dagger.internal.Factory;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class FragmentLifecycleForRxLifecycle_Factory
    implements Factory<FragmentLifecycleForRxLifecycle> {
  private static final FragmentLifecycleForRxLifecycle_Factory INSTANCE =
      new FragmentLifecycleForRxLifecycle_Factory();

  @Override
  public FragmentLifecycleForRxLifecycle get() {
    return new FragmentLifecycleForRxLifecycle();
  }

  public static FragmentLifecycleForRxLifecycle_Factory create() {
    return INSTANCE;
  }
}
