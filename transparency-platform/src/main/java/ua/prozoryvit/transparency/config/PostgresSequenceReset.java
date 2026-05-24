package ua.prozoryvit.transparency.config;

import javax.sql.DataSource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("demo")
public class PostgresSequenceReset implements ApplicationRunner {

    private final DataSource dataSource;

    public PostgresSequenceReset(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        try (var connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            if (!url.contains("postgresql")) {
                return;
            }
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            jdbc.execute("SELECT setval('campaign_id_seq', (SELECT COALESCE(MAX(id), 1) FROM campaign))");
            jdbc.execute(
                    "SELECT setval('campaign_report_id_seq', (SELECT COALESCE(MAX(id), 1) FROM campaign_report))");
            jdbc.execute("SELECT setval('fundraiser_organizer_id_seq', (SELECT COALESCE(MAX(id), 1) FROM fundraiser_organizer))");
        } catch (Exception ignored) {
            // not critical for demo
        }
    }
}
