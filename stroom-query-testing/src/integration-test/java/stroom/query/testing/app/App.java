package stroom.query.testing.app;

import event.logging.EventLoggingService;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import stroom.query.audit.AuditedQueryBundle;
import stroom.query.audit.authorisation.AuthorisationService;
import stroom.query.audit.rest.AuditedDocRefResourceImpl;
import stroom.query.audit.rest.AuditedQueryResourceImpl;
import stroom.query.audit.security.RobustJwtAuthFilter;
import stroom.query.audit.security.ServiceUser;
import stroom.query.audit.security.TokenConfig;
import stroom.query.audit.service.DocRefService;
import stroom.query.audit.service.QueryService;

import javax.inject.Inject;

public class App extends Application<Config> {
    public static final class AuditedElasticDocRefResource extends AuditedDocRefResourceImpl<TestDocRefEntity> {

        @Inject
        public AuditedElasticDocRefResource(final DocRefService<TestDocRefEntity> service,
                                            final EventLoggingService eventLoggingService,
                                            final AuthorisationService authorisationService) {
            super(service, eventLoggingService, authorisationService);
        }
    }

    public static final class AuditedElasticQueryResource extends AuditedQueryResourceImpl<TestDocRefEntity> {

        @Inject
        public AuditedElasticQueryResource(final EventLoggingService eventLoggingService,
                                           final QueryService service,
                                           final AuthorisationService authorisationService,
                                           final DocRefService<TestDocRefEntity> docRefService) {
            super(eventLoggingService, service, authorisationService, docRefService);
        }
    }

    private final AuditedQueryBundle<Config,
            TestDocRefEntity,
            TestQueryServiceImpl,
            AuditedElasticQueryResource,
            TestDocRefServiceImpl,
            AuditedElasticDocRefResource> auditedQueryBundle =
            new AuditedQueryBundle<>(
                    TestDocRefEntity.class,
                    TestQueryServiceImpl.class,
                    AuditedElasticQueryResource.class,
                    TestDocRefServiceImpl.class,
                    AuditedElasticDocRefResource.class);

    @Override
    public void run(final Config configuration,
                    final Environment environment) throws Exception {

        configureAuthentication(configuration.getTokenConfig(), environment);
    }

    @Override
    public void initialize(final Bootstrap<Config> bootstrap) {
        super.initialize(bootstrap);

        // This allows us to use templating in the YAML configuration.
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor(false)));

        bootstrap.addBundle(this.auditedQueryBundle);

    }

    private static void configureAuthentication(final TokenConfig tokenConfig,
                                                final Environment environment) {
        environment.jersey().register(
                new AuthDynamicFeature(
                        new RobustJwtAuthFilter(tokenConfig)
                ));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(ServiceUser.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
    }
}