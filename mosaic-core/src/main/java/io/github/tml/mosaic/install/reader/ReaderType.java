package io.github.tml.mosaic.install.reader;

/**
 * 描述: Reader类型枚举
 * @author suifeng
 * 日期: 2025/6/15
 */
public enum ReaderType {

    CODE("[CODE]"),
    JSON("[JSON]"),
    FILE("[FILE]");

    private final String prefix;

    ReaderType(String prefix) {
        this.prefix = prefix;
    }
    /**
     * 检查location是否匹配此类型
     */
    public boolean matches(String location) {
        return location != null && location.startsWith(this.prefix);
    }

    /**
     * 从location中移除前缀
     */
    public String removePrefix(String location) {
        if (matches(location)) {
            return location.substring(prefix.length());
        }
        return location;
    }

    public String getPrefix() {
        return prefix;
    }
}