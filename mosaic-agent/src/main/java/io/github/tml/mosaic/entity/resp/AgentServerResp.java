package io.github.tml.mosaic.entity.resp;

/**
 * @author welsir
 * @description :
 * @date 2025/6/23
 */
public class AgentServerResp {

    Boolean isSuccess;
    String errorMessage;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}