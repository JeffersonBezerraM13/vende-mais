package br.com.vendemais.config;

import br.com.vendemais.service.db.DbSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InstanciacaoBDProdTest {

    @Test
    void runDelegatesToDbSeeder() throws Exception {
        DbSeeder dbSeeder = mock(DbSeeder.class);
        InstanciacaoBDProd instanciacaoBDProd = new InstanciacaoBDProd(dbSeeder);

        instanciacaoBDProd.run(new DefaultApplicationArguments(new String[0]));

        verify(dbSeeder).populateDb();
    }
}
