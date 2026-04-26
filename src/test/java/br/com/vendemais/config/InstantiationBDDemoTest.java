package br.com.vendemais.config;

import br.com.vendemais.service.db.DbSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InstantiationBDDemoTest {

    @Test
    void runDelegatesToDbSeeder() throws Exception {
        DbSeeder dbSeeder = mock(DbSeeder.class);
        InstantiationBDDemo instantiationBDDemo = new InstantiationBDDemo(dbSeeder);

        instantiationBDDemo.run(new DefaultApplicationArguments(new String[0]));

        verify(dbSeeder).populateDb();
    }
}
