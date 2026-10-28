package dev.xichen.backend.common;

/**
 * 当前为单用户演示版本，所有数据归属同一个用户；接入登录后由认证上下文替换。
 */
public final class CurrentUser {
    public static final long ID = 1L;

    private CurrentUser() {
    }
}
