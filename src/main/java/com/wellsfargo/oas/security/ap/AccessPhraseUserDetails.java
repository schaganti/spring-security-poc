package com.wellsfargo.oas.security.ap;

public class AccessPhraseUserDetails {

  String fn;

  String ssn;

  String ap;

  String dob;

  public AccessPhraseUserDetails() {

  }

  public AccessPhraseUserDetails(String fn, String ssn, String ap, String dob,
      String requestUrl) {

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

  public void setFn(String fn) {

    this.fn = fn;
  }

  public void setSsn(String ssn) {

    this.ssn = ssn;
  }

  public void setAp(String ap) {

    this.ap = ap;
  }

  public void setDob(String dob) {

    this.dob = dob;
  }

}
