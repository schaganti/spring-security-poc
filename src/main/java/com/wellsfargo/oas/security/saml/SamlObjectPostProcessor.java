package com.wellsfargo.oas.security.saml;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.ObjectPostProcessor;

public class SamlObjectPostProcessor implements ObjectPostProcessor<AffirmativeBased> {

  AccessDecisionVoter samlAccessDecisionVoter;

  public SamlObjectPostProcessor(AccessDecisionVoter<Object> samlAccessDecisionVoter) {

    this.samlAccessDecisionVoter = samlAccessDecisionVoter;
  }

  @Override
  public AffirmativeBased postProcess(AffirmativeBased affirmativeBased) {

    affirmativeBased.getDecisionVoters().add(0, samlAccessDecisionVoter); // add
    return affirmativeBased;

  }

}
