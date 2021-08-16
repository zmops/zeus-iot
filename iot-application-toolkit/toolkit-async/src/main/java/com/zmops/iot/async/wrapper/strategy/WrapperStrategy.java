package com.zmops.iot.async.wrapper.strategy;


import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.async.wrapper.strategy.depend.DependMustStrategyMapper;
import com.zmops.iot.async.wrapper.strategy.depend.DependOnUpWrapperStrategyMapper;
import com.zmops.iot.async.wrapper.strategy.depend.DependenceAction;
import com.zmops.iot.async.wrapper.strategy.depend.DependenceStrategy;
import com.zmops.iot.async.wrapper.strategy.skip.SkipStrategy;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author create by TcSnZh on 2021/5/17-下午6:23
 */
public interface WrapperStrategy extends DependenceStrategy, SkipStrategy {
    // ========== 这三个策略器用于链式判断是否要开始工作 ==========

    // 从前往后依次判断的顺序为 dependWrapperStrategyMapper -> dependMustStrategyMapper -> dependenceStrategy

    /**
     * 设置对特殊Wrapper专用的依赖响应策略。
     *
     * @return 该值允许为null
     */
    DependOnUpWrapperStrategyMapper getDependWrapperStrategyMapper();

    /**
     * 对必须完成的（must的）Wrapper的依赖响应策略。
     * 这是一个不得不向历史妥协的属性。用于适配must开关方式。
     *
     * @return 该值允许为null
     * @deprecated 不推荐使用，很有可能被遗弃
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    default DependMustStrategyMapper getDependMustStrategyMapper() {
        return null;
    }

    /**
     * 底层全局策略。
     *
     * @return 该值不允许为null
     */
    DependenceStrategy getDependenceStrategy();

    // ========== 这是跳过策略 ==========

    /**
     * 跳过策略
     *
     * @return 不允许为null
     */
    SkipStrategy getSkipStrategy();

    @Override
    default DependenceAction.WithProperty judgeAction(Set<WorkerWrapper<?, ?>> dependWrappers,
                                                      WorkerWrapper<?, ?> thisWrapper,
                                                      WorkerWrapper<?, ?> fromWrapper) {
        // 如果存在依赖，则调用三层依赖响应策略进行判断

        DependenceStrategy strategy = getDependWrapperStrategyMapper();
        if (getDependMustStrategyMapper() != null) {
            strategy = strategy == null ? getDependMustStrategyMapper() : strategy.thenJudge(getDependenceStrategy());
        }
        if (getDependenceStrategy() != null) {
            strategy = strategy == null ? getDependenceStrategy() : strategy.thenJudge(getDependenceStrategy());
        }
        if (strategy == null) {
            throw new IllegalStateException("配置无效，三层判断策略均为null，请开发者检查自己的Builder是否逻辑错误！");
        }
        return strategy.judgeAction(dependWrappers, thisWrapper, fromWrapper);
    }

    @Override
    default boolean shouldSkip(Set<WorkerWrapper<?, ?>> nextWrappers, WorkerWrapper<?, ?> thisWrapper, WorkerWrapper<?, ?> fromWrapper) {
        return getSkipStrategy() != null && getSkipStrategy().shouldSkip(nextWrappers, thisWrapper, fromWrapper);
    }

    default void setDependWrapperStrategyMapper(DependOnUpWrapperStrategyMapper dependOnUpWrapperStrategyMapper) {
        throw new UnsupportedOperationException();
    }

    default void setDependMustStrategyMapper(DependMustStrategyMapper dependMustStrategyMapper) {
        throw new UnsupportedOperationException();
    }

    default void setDependenceStrategy(DependenceStrategy dependenceStrategy) {
        throw new UnsupportedOperationException();
    }

    default void setSkipStrategy(SkipStrategy skipStrategy) {
        throw new UnsupportedOperationException();
    }

    /**
     * 抽象策略器，实现了toString
     */
    abstract class AbstractWrapperStrategy implements WrapperStrategy {
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(128)
                    .append(this.getClass().getSimpleName()).append('{');
            final AtomicBoolean needAppendSplit = new AtomicBoolean();
            appendNotNullProperty(sb, "dependWrapperStrategyMapper=",
                    getDependWrapperStrategyMapper(), needAppendSplit, ", ");
            appendNotNullProperty(sb, "dependMustStrategyMapper=",
                    getDependMustStrategyMapper(), needAppendSplit, ", ");
            appendNotNullProperty(sb, "dependenceStrategy=",
                    getDependenceStrategy(), needAppendSplit, ", ");
            appendNotNullProperty(sb, "skipStrategy=",
                    getSkipStrategy(), needAppendSplit, ", ");
            return sb.append('}').toString();
        }

        private static void appendNotNullProperty(StringBuilder sb,
                                                  String propPrefix,
                                                  Object prop,
                                                  AtomicBoolean needAppendSplit,
                                                  @SuppressWarnings("SameParameterValue") String split) {
            if (prop == null) {
                return;
            }
            if (needAppendSplit.get()) {
                sb.append(split);
            }
            sb.append(propPrefix).append(prop);
            needAppendSplit.set(true);
        }
    }

    /**
     * 默认策略器，用默认值实现了所有属性。
     */
    class DefaultWrapperStrategy extends AbstractWrapperStrategy {
        @Override
        public DependOnUpWrapperStrategyMapper getDependWrapperStrategyMapper() {
            return null;
        }

        @SuppressWarnings("deprecation")
        @Override
        public DependMustStrategyMapper getDependMustStrategyMapper() {
            return null;
        }

        @Override
        public DependenceStrategy getDependenceStrategy() {
            return DependenceStrategy.ALL_DEPENDENCIES_ALL_SUCCESS;
        }

        @Override
        public SkipStrategy getSkipStrategy() {
            return SkipStrategy.CHECK_ONE_LEVEL;
        }
    }
}
