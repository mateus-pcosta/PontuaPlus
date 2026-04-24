package com.pontuaplus.pontua_plus.config;

import com.pontuaplus.pontua_plus.entity.*;
import com.pontuaplus.pontua_plus.enums.TipoAtividade;
import com.pontuaplus.pontua_plus.enums.TipoUsuario;
import com.pontuaplus.pontua_plus.repository.*;
import com.pontuaplus.pontua_plus.service.PontuacaoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AlunoRepository alunoRepository;
    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final AtividadeExtraRepository atividadeExtraRepository;
    private final PontuacaoService pontuacaoService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (alunoRepository.findByEmail("mateus@pontua.com").isPresent()) {
            log.info("Dados de teste já inicializados, pulando.");
            return;
        }

        log.info("Inicializando dados de teste...");

        Aluno mateus = new Aluno();
        mateus.setNome("Mateus Pessoa Costa");
        mateus.setEmail("mateus@pontua.com");
        mateus.setSenha(passwordEncoder.encode("123456"));
        mateus.setTipo(TipoUsuario.ALUNO);
        mateus.setMatricula("2024001");
        mateus.setCpf("12345678900");
        mateus.setSerie("9º Ano");
        mateus.setColegio("Colégio Objetivo");
        mateus.setDataNascimento(LocalDate.of(2009, 5, 15));
        mateus.setDataIngresso(LocalDate.of(2024, 1, 1));
        mateus.setTurma("9A");
        mateus.setBimestreAtual(2);

        mateus = alunoRepository.save(mateus);
        log.info("Aluno criado: {}", mateus.getEmail());

        List<String> disciplinas = Arrays.asList("Matemática", "Inglês", "Português", "Ciências", "História");
        List<Double> notasValores = Arrays.asList(8.0, 7.5, 7.0, 8.0, 7.0);

        for (int i = 0; i < disciplinas.size(); i++) {
            Nota nota = new Nota();
            nota.setAluno(mateus);
            nota.setDisciplina(disciplinas.get(i));
            nota.setValor(notasValores.get(i));
            nota.setBimestre(2);
            notaRepository.save(nota);
        }

        Frequencia junho = new Frequencia();
        junho.setAluno(mateus);
        junho.setMes(6);
        junho.setAno(2024);
        junho.setBimestre(2);
        junho.setTotalAulas(20);
        junho.setPresencas(18);
        junho.setFaltas(2);
        frequenciaRepository.save(junho);

        Frequencia julho = new Frequencia();
        julho.setAluno(mateus);
        julho.setMes(7);
        julho.setAno(2024);
        julho.setBimestre(2);
        julho.setTotalAulas(20);
        julho.setPresencas(17);
        julho.setFaltas(3);
        frequenciaRepository.save(julho);

        AtividadeExtra liderTurma = new AtividadeExtra();
        liderTurma.setAluno(mateus);
        liderTurma.setNome("Líder de Turma");
        liderTurma.setTipo(TipoAtividade.LIDER_TURMA);
        liderTurma.setBimestre(2);
        atividadeExtraRepository.save(liderTurma);

        AtividadeExtra companheiros = new AtividadeExtra();
        companheiros.setAluno(mateus);
        companheiros.setNome("Sistema de Companheiros");
        companheiros.setTipo(TipoAtividade.SISTEMA_COMPANHEIROS);
        companheiros.setBimestre(2);
        atividadeExtraRepository.save(companheiros);

        AtividadeExtra obmep = new AtividadeExtra();
        obmep.setAluno(mateus);
        obmep.setNome("Participação na OBMEP");
        obmep.setTipo(TipoAtividade.OBMEP);
        obmep.setBimestre(2);
        atividadeExtraRepository.save(obmep);

        AtividadeExtra conteudoAudio = new AtividadeExtra();
        conteudoAudio.setAluno(mateus);
        conteudoAudio.setNome("Criação de Conteúdo de Áudio");
        conteudoAudio.setTipo(TipoAtividade.CRIACAO_CONTEUDO_AUDIO);
        conteudoAudio.setBimestre(2);
        atividadeExtraRepository.save(conteudoAudio);

        pontuacaoService.calcularPontuacaoAluno(mateus);
        pontuacaoService.atualizarRankings();

        log.info("Dados de teste inicializados. Login: mateus@pontua.com / Senha: 123456");
    }
}
