package com.wellsfargo.oas.security;

import java.io.Serializable;

public class ErrorInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  String statusCode;

  String messageCode;

  String message;

  String stackTrace;

  public ErrorInfo() {

    // TODO Auto-generated constructor stub
  }

  public ErrorInfo(String statusCode, String messageCode, String message,
      String stackTrace) {

    super();
    this.statusCode = statusCode;
    this.messageCode = messageCode;
    this.message = message;
    this.stackTrace = stackTrace;
  }

  public String getStatusCode() {

    return statusCode;
  }

  public void setStatusCode(String statusCode) {

    this.statusCode = statusCode;
  }

  public String getMessageCode() {

    return messageCode;
  }

  public void setMessageCode(String messageCode) {

    this.messageCode = messageCode;
  }

  public String getMessage() {

    return message;
  }

  public void setMessage(String message) {

    this.message = message;
  }

  public String getStackTrace() {

    return stackTrace;
  }

  public void setStackTrace(String stackTrace) {

    this.stackTrace = stackTrace;
  }

}
