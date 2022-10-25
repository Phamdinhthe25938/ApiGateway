package com.example.api_gateway.response;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class APIResponse {

  private final String status;

  private final String messageCode;

  private final String message;

  private final Object result;


  @Override
  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      return "{\"status\":\"" + status + "\", \"messageCode\":\"" + messageCode
          + "\", \"message\":\"" + message
          + "\", \"result\":" + result + "}";
    }
  }
}
