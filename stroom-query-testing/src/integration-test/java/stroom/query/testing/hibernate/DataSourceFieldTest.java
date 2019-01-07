package stroom.query.testing.hibernate;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stroom.query.audit.model.IsDataSourceField;
import stroom.query.audit.model.QueryableEntity;
import stroom.query.testing.hibernate.app.TestQueryableHibernateEntity;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DataSourceFieldTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceFieldTest.class);

    @Test
    void test() {
        final Set<IsDataSourceField> annotations = QueryableEntity.getFields(TestQueryableHibernateEntity.class).collect(Collectors.toSet());
        LOGGER.info("Annotations Found: " + annotations);
        assertThat(annotations).isNotEmpty();
    }
}