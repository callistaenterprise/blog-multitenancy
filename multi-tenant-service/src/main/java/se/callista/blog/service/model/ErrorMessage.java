package se.callista.blog.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A message containing more info why an operation failed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorMessage {

  @JsonProperty("timestamp")
  @NotNull
  private OffsetDateTime timestamp;

  @JsonProperty("status")
  @NotNull
  private Integer status;

  @JsonProperty("error")
  @NotNull
  private String error;

  @JsonProperty("message")
  @NotNull
  @Size(max=255)
  private String message;

  @JsonProperty("path")
  private String path;

}

