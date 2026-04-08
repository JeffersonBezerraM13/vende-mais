package br.com.vendemais.config;

import br.com.vendemais.service.db.DbSeeder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class InstanciacaoBDProd implements ApplicationRunner {

    private final DbSeeder semeadorBd;

    public InstanciacaoBDProd(DbSeeder semeadorBd) {
        this.semeadorBd = semeadorBd;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.semeadorBd.populateDb();
    }
}
