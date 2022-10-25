package com.example.api_gateway.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponseModel {

    private String errCode;
    private String err;
    private String errDetail;
    private String data;
    private Date receiveTime;
}
