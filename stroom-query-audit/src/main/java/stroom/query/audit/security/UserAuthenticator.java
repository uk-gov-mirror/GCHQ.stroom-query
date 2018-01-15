package stroom.query.audit.security;


import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtContext;

import java.util.Optional;

public class UserAuthenticator implements Authenticator<JwtContext, ServiceUser> {

    @Override
    public Optional<ServiceUser> authenticate(JwtContext context) throws AuthenticationException {
        //TODO: If we want to check anything else about the user we need to do it here.
        try {
            return Optional.of(new ServiceUser(
                    context.getJwtClaims().getSubject(),
                    context.getJwt()));
        } catch (MalformedClaimException e) {
            return Optional.empty();
        }
    }
}

