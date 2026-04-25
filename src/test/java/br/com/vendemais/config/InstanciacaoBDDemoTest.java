package br.com.vendemais.config;

import br.com.vendemais.service.db.DbSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InstanciacaoBDDemoTest {

    @Test
    void runDelegatesToDbSeeder() throws Exception {
        DbSeeder dbSeeder = mock(DbSeeder.class);
        InstanciacaoBDDemo instanciacaoBDDemo = new InstanciacaoBDDemo(dbSeeder);

        instanciacaoBDDemo.run(new DefaultApplicationArguments(new String[0]));

        verify(dbSeeder).populateDb();
    }
}
