package com.wellsfargo.oas.security;

public class OasUserDetails {

  String fn;

  String ssn;

  String ap;

  String dob;

  public OasUserDetails(String fn, String ssn, String ap, String dob, String requestUrl) {

    this.fn = fn;
    this.ssn = ssn;
    this.ap = ap;
    this.dob = dob;
  }

  public String getFn() {

    return fn;
  }

  public String getSsn() {

    return ssn;
  }

  public String getAp() {

    return ap;
  }

  public String getDob() {

    return dob;
  }

}
