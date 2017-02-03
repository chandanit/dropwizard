package io.dropwizard.migrations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.sourceforge.argparse4j.inf.Namespace;
import org.assertj.core.util.Files;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DbGenerateDocsCommandTest extends AbstractMigrationTest {

    private DbGenerateDocsCommand<TestMigrationConfiguration> generateDocsCommand = new DbGenerateDocsCommand<>(
        TestMigrationConfiguration::getDataSource, TestMigrationConfiguration.class, "migrations.xml");

    @Test
    public void testRun() throws Exception {
        final File temporaryFolder = Files.newTemporaryFolder();

        generateDocsCommand.run(null, new Namespace(ImmutableMap.of(
            "output", ImmutableList.of(temporaryFolder.toString()))),
            createConfiguration("jdbc:h2:mem:" + UUID.randomUUID()));

        Files.fileNamesIn(temporaryFolder.toString(), false).contains("index.html");
    }

    @Test
    public void testHelpPage() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        createSubparser(generateDocsCommand).printHelp(new PrintWriter(baos, true));
        assertThat(baos.toString()).isEqualTo(String.format(
            "usage: db generate-docs [-h] [--migrations MIGRATIONS-FILE]%n" +
                "          [--catalog CATALOG] [--schema SCHEMA] [file] output%n" +
                "%n" +
                "Generate documentation about the database state.%n" +
                "%n" +
                "positional arguments:%n" +
                "  file                   application configuration file%n" +
                "  output                 output directory%n" +
                "%n" +
                "optional arguments:%n" +
                "  -h, --help             show this help message and exit%n" +
                "  --migrations MIGRATIONS-FILE%n" +
                "                         the file containing  the  Liquibase migrations for%n" +
                "                         the application%n" +
                "  --catalog CATALOG      Specify  the   database   catalog   (use  database%n" +
                "                         default if omitted)%n" +
                "  --schema SCHEMA        Specify the database schema  (use database default%n" +
                "                         if omitted)%n"));
    }
}
