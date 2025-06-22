package io.github.tml.mosaic.core.model;

/**
 * @author welsir
 * @description :
 * @date 2025/6/21
 */
public class LineChange {
    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    int lineNumber;
    String before;
    String after;
}