package com.zmops.iot.async.wrapper.strategy.depend;

import com.zmops.iot.async.worker.ResultState;
import com.zmops.iot.async.wrapper.WorkerWrapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 这是一个“向历史妥协”的策略器。以兼容must开关模式。
 *
 * @author create by TcSnZh on 2021/5/4-下午1:24
 */
@SuppressWarnings("UnusedReturnValue")
public class DependMustStrategyMapper implements DependenceStrategy {

    private final Set<WorkerWrapper<?, ?>> mustDependSet = new LinkedHashSet<>();

    /**
     * 在{@link #mustDependSet} 中的must依赖。
     * <p>
     * 如果{@code mustDependSet == null || mustDependSet.size() < 1}，返回{@link DependenceAction#JUDGE_BY_AFTER}
     * <p>
     * 如果所有的Wrapper已经完成，本Wrapper将会开始工作。
     * <p>
     * 如果任一{@link #mustDependSet}中的Wrapper失败，则返回{@link DependenceAction#FAST_FAIL}。
     * 具体超时/异常则根据{@link ResultState}的值进行判断。
     * <p>
     * 如果存在Wrapper未完成 且 所有的Wrapper都未失败，则返回{@link DependenceAction#JUDGE_BY_AFTER}。
     * </p>
     */
    @Override
    public DependenceAction.WithProperty judgeAction(Set<WorkerWrapper<?, ?>> dependWrappers,
                                                     WorkerWrapper<?, ?> thisWrapper,
                                                     WorkerWrapper<?, ?> fromWrapper) {
        if (mustDependSet.size() < 1) {
            return DependenceAction.JUDGE_BY_AFTER.emptyProperty();
        }
        boolean allSuccess = true;
        for (WorkerWrapper<?, ?> wrapper : mustDependSet) {
            switch (wrapper.getWorkResult().getResultState()) {
                case TIMEOUT:
                    return DependenceAction.FAST_FAIL.fastFailException(ResultState.TIMEOUT, null);
                case EXCEPTION:
                    return DependenceAction.FAST_FAIL.fastFailException(ResultState.EXCEPTION, wrapper.getWorkResult().getEx());
                case DEFAULT:
                    allSuccess = false;
                case SUCCESS:
                default:
            }
        }
        if (allSuccess) {
            return DependenceAction.START_WORK.emptyProperty();
        }
        return DependenceAction.JUDGE_BY_AFTER.emptyProperty();
    }

    /**
     * 新增must依赖。
     *
     * @param mustDependWrapper WorkerWrapper
     * @return 返回自身
     */
    public DependMustStrategyMapper addDependMust(WorkerWrapper<?, ?> mustDependWrapper) {
        if (mustDependWrapper == null) {
            return this;
        }
        mustDependSet.add(mustDependWrapper);
        return this;
    }

    public DependMustStrategyMapper addDependMust(Collection<WorkerWrapper<?, ?>> wrappers) {
        if (wrappers == null) {
            return this;
        }
        mustDependSet.addAll(wrappers);
        return this;
    }

    public DependMustStrategyMapper addDependMust(WorkerWrapper<?, ?>... wrappers) {
        if (wrappers == null) {
            return this;
        }
        return addDependMust(Arrays.asList(wrappers));
    }

    public Set<WorkerWrapper<?, ?>> getMustDependSet() {
        return mustDependSet;
    }

    @Override
    public String toString() {
        return "DependMustStrategyMapper{" +
                "mustDependSet::getId=" + mustDependSet.stream().map(WorkerWrapper::getId).collect(Collectors.toList()) +
                '}';
    }
}
