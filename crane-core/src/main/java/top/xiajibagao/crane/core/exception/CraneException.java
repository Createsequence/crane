package top.xiajibagao.crane.core.exception;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;

/**
 * @author huangchengxing
 * @date 2022/02/28 19:29
 */
public class CraneException extends RuntimeException {

    public CraneException() {
    }

    public CraneException(String message, Object...args) {
        super(CharSequenceUtil.format(message, args));
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

    public static void throwOf(String template, Object... params) {
        if (ArrayUtil.isNotEmpty(params)) {
            throw new CraneException(CharSequenceUtil.format(template, params));
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
