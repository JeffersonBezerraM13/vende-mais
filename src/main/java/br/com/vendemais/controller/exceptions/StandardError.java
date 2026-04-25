package br.com.vendemais.controller.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents the standard API error payload returned when a CRM request fails
 * outside field-level validation scenarios.
 */
@Schema(name = "StandardError", description = "Formato padrao de erro retornado pela API.")
public class StandardError  {
    @Schema(example = "1712678400000")
    private Long timestamp;

    @Schema(example = "404")
    private Integer status;

    @Schema(example = "Object Not Found")
    private String error;

    @Schema(example = "Lead não encontrado.")
    private String message;

    @Schema(example = "/leads/99")
    private String path;

    public StandardError() {
    }

    public StandardError(Long timestamp, Integer status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
