package com.zmops.iot.async.wrapper.strategy.skip;


import com.zmops.iot.async.wrapper.WorkerWrapper;

import java.util.Set;

/**
 * @author create by TcSnZh on 2021/5/6-下午3:02
 */
@FunctionalInterface
public interface SkipStrategy {
    /**
     * 跳过策略函数。返回true将会使WorkerWrapper跳过执行。
     *
     * @param nextWrappers 下游WrapperSet
     * @param thisWrapper  本WorkerWrapper
     * @param fromWrapper  呼叫本Wrapper的上游Wrapper
     * @return 返回true将会使WorkerWrapper跳过执行。
     */
    boolean shouldSkip(Set<WorkerWrapper<?, ?>> nextWrappers, WorkerWrapper<?, ?> thisWrapper, WorkerWrapper<?, ?> fromWrapper);

    /**
     * 不跳过
     */
    SkipStrategy NOT_SKIP = new SkipStrategy() {
        @Override
        public boolean shouldSkip(Set<WorkerWrapper<?, ?>> nextWrappers, WorkerWrapper<?, ?> thisWrapper, WorkerWrapper<?, ?> fromWrapper) {
            return false;
        }

        @Override
        public String toString() {
            return "NOT_SKIP";
        }
    };

    /**
     * 距离为1的wrapper都不在初始化状态
     */
    SkipStrategy CHECK_ONE_LEVEL = new SkipStrategy() {

        @Override
        public boolean shouldSkip(Set<WorkerWrapper<?, ?>> nextWrappers, WorkerWrapper<?, ?> thisWrapper, WorkerWrapper<?, ?> fromWrapper) {
            return nextWrappers != null && !nextWrappers.isEmpty()
                    && nextWrappers.stream().allMatch(workerWrapper -> workerWrapper.getState().finished());
        }

        @Override
        public String toString() {
            return "CHECK_ONE_LEVEL";
        }
    };
}
