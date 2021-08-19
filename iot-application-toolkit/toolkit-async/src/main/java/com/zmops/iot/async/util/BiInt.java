package com.zmops.iot.async.util;

import java.util.Comparator;

/**
 * 两个int值包装类。重写了{@link #hashCode()}与{@link #equals(Object)}
 *
 * @author create by TcSnZh on 2021/5/16-上午1:50
 */
public final class BiInt {
    // properties

    private final int m;
    private final int n;

    public static final Comparator<BiInt> cmp_m_asc         = Comparator.comparingInt(BiInt::getM);
    public static final Comparator<BiInt> cmp_n_asc         = Comparator.comparingInt(BiInt::getN);
    public static final Comparator<BiInt> cmp_m_desc        = cmp_m_asc.reversed();
    public static final Comparator<BiInt> cmp_n_desc        = cmp_n_asc.reversed();
    public static final Comparator<BiInt> cmp_m_asc_n_asc   =
            cmp_m_asc.thenComparing(cmp_n_asc);
    public static final Comparator<BiInt> cmp_m_asc_n_desc  =
            cmp_m_asc.thenComparing(cmp_n_desc);
    public static final Comparator<BiInt> cmp_m_desc_n_asc  =
            cmp_m_desc.thenComparing(cmp_n_asc);
    public static final Comparator<BiInt> cmp_m_desc_n_desc =
            cmp_m_desc.thenComparing(cmp_n_desc);
    public static final Comparator<BiInt> cmp_n_asc_m_asc   =
            cmp_n_asc.thenComparing(cmp_m_asc);
    public static final Comparator<BiInt> cmp_n_asc_m_desc  =
            cmp_n_asc.thenComparing(cmp_m_desc);
    public static final Comparator<BiInt> cmp_n_desc_m_asc  =
            cmp_n_desc.thenComparing(cmp_m_asc);
    public static final Comparator<BiInt> cmp_n_desc_m_desc =
            cmp_n_desc.thenComparing(cmp_m_desc);

    /**
     * private constructor , please use {@link #of(int, int)} to build Idx object.
     */
    private BiInt(int m, int n) {
        this.m = m;
        this.n = n;
    }

    // getter

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }

    // hashcode and equals

    @Override
    public int hashCode() {
        return m ^ n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BiInt))
            return false;
        BiInt idx = (BiInt) o;
        return m == idx.m && n == idx.n;
    }

    // toString

    @Override
    public String toString() {
        return "(" + m + ',' + n + ')';
    }

    // ========== static ==========


    // 工厂方法

    public static BiInt of(int m, int n) {
        if (m == Integer.MIN_VALUE && n == Integer.MAX_VALUE) {
            return MIN_TO_MAX;
        }
        if (m >= 0 && m < CACHE_RANGE_M && n >= 0 && n < CACHE_RANGE_M) {
            return cache[m * CACHE_RANGE_M + n];
        }
        return new BiInt(m, n);
    }

    // 缓存区间

    private static final BiInt   MIN_TO_MAX    = new BiInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    private static final BiInt[] cache; // m from 0 to 31 , n from 0 to 31 , total 1023 .
    private static final int     CACHE_RANGE_M = 32; // 0 to 31
    private static final int     CACHE_RANGE_N = 32; // 0 to 31

    static {
        cache = new BiInt[CACHE_RANGE_M * CACHE_RANGE_N];
        for (int i = 0; i < CACHE_RANGE_M; i++) {
            for (int j = 0; j < CACHE_RANGE_N; j++) {
                cache[i * CACHE_RANGE_M + j] = new BiInt(i, j);
            }
        }
    }
}
