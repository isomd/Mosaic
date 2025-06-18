package io.github.tml.mosaic.core.tools.param.validator.core;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * 描述: 校验结果封装
 * @author suifeng
 * 日期: 2025/6/18
 */
@Data
public class ValidationResult {
    
    private boolean valid;
    private List<String> errorMessages;
    private String configItemName;
    
    private ValidationResult(boolean valid, List<String> errorMessages) {
        this.valid = valid;
        this.errorMessages = errorMessages != null ? errorMessages : new ArrayList<>();
    }
    
    public static ValidationResult success() {
        return new ValidationResult(true, new ArrayList<>());
    }
    
    public static ValidationResult failure(String errorMessage) {
        List<String> errors = new ArrayList<>();
        errors.add(errorMessage);
        return new ValidationResult(false, errors);
    }
    
    public static ValidationResult failure(List<String> errorMessages) {
        return new ValidationResult(false, new ArrayList<>(errorMessages));
    }
    
    public ValidationResult addError(String errorMessage) {
        if (this.errorMessages == null) {
            this.errorMessages = new ArrayList<>();
        }
        this.errorMessages.add(errorMessage);
        this.valid = false;
        return this;
    }
    
    public ValidationResult merge(ValidationResult other) {
        if (other == null || other.isValid()) {
            return this;
        }
        
        this.valid = false;
        if (this.errorMessages == null) {
            this.errorMessages = new ArrayList<>();
        }
        if (other.getErrorMessages() != null) {
            this.errorMessages.addAll(other.getErrorMessages());
        }
        return this;
    }
    
    public String getFormattedErrorMessage() {
        if (errorMessages == null || errorMessages.isEmpty()) {
            return "";
        }
        
        StringJoiner joiner = new StringJoiner("; ");
        for (String message : errorMessages) {
            joiner.add(message);
        }
        
        if (configItemName != null && !configItemName.isEmpty()) {
            return String.format("Config item '%s': %s", configItemName, joiner.toString());
        }
        return joiner.toString();
    }
    
    public ValidationResult withConfigItemName(String configItemName) {
        this.configItemName = configItemName;
        return this;
    }
}