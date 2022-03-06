package top.xiajibagao.crane.exception;

import top.xiajibagao.crane.helper.ArrayUtils;

/**
 * @author huangchengxing
 * @date 2022/02/28 19:29
 */
public class CraneException extends RuntimeException {

    public CraneException() {
    }

    public CraneException(String message) {
        super(message);
    }

    public CraneException(String message, Throwable cause) {
        super(message, cause);
    }

    public CraneException(Throwable cause) {
        super(cause);
    }

    public CraneException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static void throwOf(String template, Object... params) {
        if (ArrayUtils.notEmpty(params)) {
            throw new CraneException(String.format(template, params));
        } else {
            throw new CraneException(template);
        }
    }

    public static void throwIfFalse(boolean condition, String template, Object... params) {
        throwIfTrue(!condition, template, params);
    }

    public static void throwIfTrue(boolean condition, String template, Object... params) {
        if (condition) {
            throw new CraneException(String.format(template, params));
        }
    }

}
