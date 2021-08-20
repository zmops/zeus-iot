package com.zmops.iot.async.wrapper.strategy.depend;

import com.zmops.iot.async.worker.ResultState;
import com.zmops.iot.async.worker.WorkResult;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.async.wrapper.WorkerWrapperGroup;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 依赖策略接口。
 * <p/>
 * 提供了多个默认值可以作为单例模式使用。
 * <p>
 * 工作原理示例：
 * <p>
 * ====    一个简单示例    ====
 * 现有三个WorkerWrapper：A、B、C，其中 {@code A{dependWrappers=[B,C],}  }
 * 当B执行完成后调用A时，根据依赖关系ALL_DEPENDENCIES_ALL_SUCCESS，还需等待C的结果。
 * 然后，当C执行完成后调用A时，根据依赖关系ALL_DEPENDENCIES_ALL_SUCCESS： 此时如果C成功了，A就开工，此时如果C失败了，A就失败。
 * ====    简单示例2      ====
 *
 * </p>
 *
 * @author create by TcSnZh on 2021/5/1-下午10:48
 */
@FunctionalInterface
public interface DependenceStrategy {
    /**
     * 核心判断策略
     *
     * @param dependWrappers thisWrapper.dependWrappers的属性值。
     * @param thisWrapper    thisWrapper，即为“被催促”的WorkerWrapper
     * @param fromWrapper    调用来源Wrapper。
     *                       <p>
     *                       该参数不会为null。
     *                       因为在{@link WorkerWrapper#work(ExecutorService, long, WorkerWrapperGroup)}方法中传入的的第一批无依赖的Wrapper，
     *                       不会被该策略器所判断，而是不论如何直接执行。
     *                       </p>
     * @return 返回枚举值内部类，WorkerWrapper将会根据其值来决定自己如何响应这次调用。 {@link DependenceAction.WithProperty}
     */
    DependenceAction.WithProperty judgeAction(Set<WorkerWrapper<?, ?>> dependWrappers,
                                              WorkerWrapper<?, ?> thisWrapper,
                                              WorkerWrapper<?, ?> fromWrapper);

    /**
     * 如果本策略器的judge方法返回了JUDGE_BY_AFTER，则交给下一个策略器来判断。
     *
     * @param after 下层策略器
     * @return 返回一个“封装的多层策略器”
     */
    default DependenceStrategy thenJudge(DependenceStrategy after) {
        DependenceStrategy that = this;
        return new DependenceStrategy() {
            @Override
            public DependenceAction.WithProperty judgeAction(Set<WorkerWrapper<?, ?>> dependWrappers,
                                                             WorkerWrapper<?, ?> thisWrapper,
                                                             WorkerWrapper<?, ?> fromWrapper) {
                DependenceAction.WithProperty judge = that.judgeAction(dependWrappers, thisWrapper, fromWrapper);
                if (judge.getDependenceAction() == DependenceAction.JUDGE_BY_AFTER) {
                    return after.judgeAction(dependWrappers, thisWrapper, fromWrapper);
                }
                return judge;
            }

            @Override
            public String toString() {
                return that + " ----> " + after;
            }
        };
    }

    // ========== 以下是一些默认实现 ==========

    /**
     * 被依赖的所有Wrapper都必须成功才能开始工作。
     * 如果其中任一Wrapper还没有执行且不存在失败，则休息。
     * 如果其中任一Wrapper失败则立即失败。
     */
    DependenceStrategy ALL_DEPENDENCIES_ALL_SUCCESS = new DependenceStrategy() {
        @Override
        public DependenceAction.WithProperty judgeAction(Set<WorkerWrapper<?, ?>> dependWrappers,
                                                         WorkerWrapper<?, ?> thisWrapper,
                                                         WorkerWrapper<?, ?> fromWrapper) {
            boolean hasWaiting = false;
            for (final WorkerWrapper<?, ?> dependWrapper : dependWrappers) {
                WorkResult<?> workResult = dependWrapper.getWorkResult();
                switch (workResult.getResultState()) {
                    case DEFAULT:
                        hasWaiting = true;
                        break;
                    case SUCCESS:
                        break;
                    case TIMEOUT:
                    case EXCEPTION:
                        return DependenceAction.FAST_FAIL.fastFailException(workResult.getResultState(), workResult.getEx());
                    default:
                        throw new RuntimeException("不该执行到的代码 workResult.getResultState()=" + workResult.getResultState());
                }
            }
            if (hasWaiting) {
                return DependenceAction.TAKE_REST.emptyProperty();
            }
            return DependenceAction.START_WORK.emptyProperty();
        }

        @Override
        public String toString() {
            return "ALL_DEPENDENCIES_ALL_SUCCESS";
        }
    };

    /**
     * 被依赖的Wrapper中任意一个成功了就可以开始工作。
     * 如果其中所有Wrapper还没有执行，则休息。
     * 如果其中一个Wrapper失败且不存在成功则立即失败。
     */
    DependenceStrategy ALL_DEPENDENCIES_ANY_SUCCESS = new DependenceStrategy() {
        @Override
        public DependenceAction.WithProperty judgeAction(Set<WorkerWrapper<?, ?>> dependWrappers,
                                                         WorkerWrapper<?, ?> thisWrapper,
                                                         WorkerWrapper<?, ?> fromWrapper) {
            boolean     hasFailed         = false;
            Exception   fastFailException = null;
            ResultState resultState       = null;
            for (final WorkerWrapper<?, ?> dependWrapper : dependWrappers) {
                WorkResult<?> workResult = dependWrapper.getWorkResult();
                switch (workResult.getResultState()) {
                    case DEFAULT:
                        break;
                    case SUCCESS:
                        return DependenceAction.START_WORK.emptyProperty();
                    case TIMEOUT:
                    case EXCEPTION:
                        resultState = !hasFailed ? workResult.getResultState() : resultState;
                        fastFailException = !hasFailed ? workResult.getEx() : fastFailException;
                        // 跳过不算失败
                        hasFailed = true;
                        break;
                    default:
                        throw new RuntimeException("不该执行到的代码 workResult.getResultState()=" + workResult.getResultState());
                }
            }
            if (hasFailed) {
                return DependenceAction.FAST_FAIL.fastFailException(resultState, fastFailException);
            }
            return DependenceAction.TAKE_REST.emptyProperty();
        }

        @Override
        public String toString() {
            return "ALL_DEPENDENCIES_ANY_SUCCESS";
        }
    };

    /**
     * 如果被依赖的工作中任一失败，则立即失败。
     * 否则就开始工作（不论之前的工作有没有开始）。
     */
    @SuppressWarnings("unused")
    DependenceStrategy ALL_DEPENDENCIES_NONE_FAILED = new DependenceStrategy() {
        @Override
        public DependenceAction.WithProperty judgeAction(Set<WorkerWrapper<?, ?>> dependWrappers,
                                                         WorkerWrapper<?, ?> thisWrapper,
                                                         WorkerWrapper<?, ?> fromWrapper) {
            for (WorkerWrapper<?, ?> dependWrapper : dependWrappers) {
                WorkResult<?> workResult = dependWrapper.getWorkResult();
                switch (workResult.getResultState()) {
                    case TIMEOUT:
                    case EXCEPTION:
                        return DependenceAction.FAST_FAIL.fastFailException(workResult.getResultState(), workResult.getEx());
                    default:
                }
            }
            return DependenceAction.START_WORK.emptyProperty();
        }

        @Override
        public String toString() {
            return "ALL_DEPENDENCIES_NONE_FAILED";
        }
    };

    /**
     * 只有当指定的这些Wrapper都成功时，才会开始工作。
     * 任一失败会快速失败。
     * 任一还没有执行且不存在失败，则休息。
     *
     * @param theseWrapper 该方法唯一有效参数。
     * @return 返回生成的 {@link DependenceAction.WithProperty)
     */
    @SuppressWarnings("unused")
    static DependenceStrategy theseWrapperAllSuccess(Set<WorkerWrapper<?, ?>> theseWrapper) {
        return new DependenceStrategy() {
            private final Set<WorkerWrapper<?, ?>> theseWrappers;
            private final String toString;

            {
                theseWrappers = Collections.unmodifiableSet(theseWrapper);
                toString = "THESE_WRAPPER_MUST_SUCCESS:" + theseWrappers.stream().map(WorkerWrapper::getId).collect(Collectors.toList());
            }

            @Override
            public DependenceAction.WithProperty judgeAction(Set<WorkerWrapper<?, ?>> dependWrappers,
                                                             WorkerWrapper<?, ?> thisWrapper,
                                                             WorkerWrapper<?, ?> fromWrapper) {
                boolean hasWaiting = false;
                for (WorkerWrapper<?, ?> wrapper : theseWrappers) {
                    ResultState resultState = wrapper.getWorkResult().getResultState();
                    switch (resultState) {
                        case DEFAULT:
                            hasWaiting = true;
                            break;
                        case SUCCESS:
                            break;
                        case TIMEOUT:
                        case EXCEPTION:
                            return DependenceAction.FAST_FAIL.fastFailException(resultState, wrapper.getWorkResult().getEx());
                        default:
                            throw new RuntimeException("不该执行到的代码 workResult.getResultState()=" + resultState);
                    }
                }
                if (hasWaiting) {
                    return DependenceAction.TAKE_REST.emptyProperty();
                }
                return DependenceAction.START_WORK.emptyProperty();
            }


            @Override
            public String toString() {
                return toString;
            }
        };
    }

    /**
     * 此值用于适配v1.4及之前的must开关模式，
     * 当`wrapperStrategy`的`dependMustStrategyMapper`的`mustDependSet`不为空时，
     * 则休息（因为能判断到这个责任链说明set中存在不满足的值L）。
     * 为空时，则任一成功则执行。
     *
     * @deprecated 不推荐使用must开关
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    DependenceStrategy IF_MUST_SET_NOT_EMPTY_ALL_SUCCESS_ELSE_ANY = new DependenceStrategy() {
        @Override
        public DependenceAction.WithProperty judgeAction(Set<WorkerWrapper<?, ?>> dependWrappers,
                                                         WorkerWrapper<?, ?> thisWrapper,
                                                         WorkerWrapper<?, ?> fromWrapper) {
            DependMustStrategyMapper mustMapper = thisWrapper.getWrapperStrategy().getDependMustStrategyMapper();
            if (mustMapper != null && !mustMapper.getMustDependSet().isEmpty()) {
                //  至少有一个must，则因为must未完全完成而等待。
                return DependenceAction.TAKE_REST.emptyProperty();
            }
            // 如果一个must也没有，则认为应该是ANY模式。
            return DependenceStrategy.ALL_DEPENDENCIES_ANY_SUCCESS.judgeAction(dependWrappers, thisWrapper, fromWrapper);
        }

        @Override
        public String toString() {
            return "IF_MUST_SET_NOT_EMPTY_ALL_SUCCESS_ELSE_ANY";
        }
    };

}
