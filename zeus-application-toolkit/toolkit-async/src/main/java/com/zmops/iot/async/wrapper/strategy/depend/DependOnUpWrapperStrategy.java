package com.zmops.iot.async.wrapper.strategy.depend;

import com.zmops.iot.async.wrapper.WorkerWrapper;

/**
 * 由上游wrapper决定本wrapper行为的单参数策略。
 *
 * @author create by TcSnZh on 2021/5/1-下午11:16
 */
@FunctionalInterface
public interface DependOnUpWrapperStrategy {
    /**
     * 仅使用一个参数（即调用自身的上游wrapper）的判断方法
     *
     * @param fromWrapper 调用本Wrapper的上游Wrapper
     * @return 返回 {@link DependenceAction.WithProperty}
     */
    DependenceAction.WithProperty judge(WorkerWrapper<?, ?> fromWrapper);

    // ========== 送几个供链式调用的默认值 ==========

    /**
     * 成功时，交给下一个策略器判断。
     * 未运行时，休息。
     * 失败时，失败。
     */
    DependOnUpWrapperStrategy SUCCESS_CONTINUE = new DependOnUpWrapperStrategy() {
        @Override
        public DependenceAction.WithProperty judge(WorkerWrapper<?, ?> ww) {
            switch (ww.getWorkResult().getResultState()) {
                case SUCCESS:
                    return DependenceAction.JUDGE_BY_AFTER.emptyProperty();
                case DEFAULT:
                    return DependenceAction.TAKE_REST.emptyProperty();
                case EXCEPTION:
                case TIMEOUT:
                    return DependenceAction.FAST_FAIL.fastFailException(ww.getWorkResult().getResultState(), ww.getWorkResult().getEx());
                default:
            }
            throw new RuntimeException("不该执行到的代码 workResult.getResultState()=" + ww.getWorkResult().getResultState());
        }

        @Override
        public String toString() {
            return "SUCCESS_CONTINUE";
        }
    };
    /**
     * 成功时，开始工作。
     * 未运行时，交给下一个策略器判断。
     * 失败时，失败。
     */
    DependOnUpWrapperStrategy SUCCESS_START_INIT_CONTINUE = new DependOnUpWrapperStrategy() {
        @Override
        public DependenceAction.WithProperty judge(WorkerWrapper<?, ?> ww) {
            switch (ww.getWorkResult().getResultState()) {
                case SUCCESS:
                    return DependenceAction.START_WORK.emptyProperty();
                case DEFAULT:
                    return DependenceAction.JUDGE_BY_AFTER.emptyProperty();
                case EXCEPTION:
                case TIMEOUT:
                    return DependenceAction.FAST_FAIL.fastFailException(ww.getWorkResult().getResultState(), ww.getWorkResult().getEx());
                default:
            }
            throw new RuntimeException("不该执行到的代码 workResult.getResultState()=" + ww.getWorkResult().getResultState());
        }

        @Override
        public String toString() {
            return "SUCCESS_START_INIT_CONTINUE";
        }
    };
}
