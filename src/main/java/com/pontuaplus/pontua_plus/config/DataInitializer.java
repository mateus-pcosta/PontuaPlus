package com.pontuaplus.pontua_plus.config;

import com.pontuaplus.pontua_plus.entity.*;
import com.pontuaplus.pontua_plus.enums.TipoAtividade;
import com.pontuaplus.pontua_plus.enums.TipoUsuario;
import org.springframework.context.annotation.Profile;
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

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AlunoRepository alunoRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final ResponsavelRepository responsavelRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final AtividadeExtraRepository atividadeExtraRepository;
    private final PontuacaoService pontuacaoService;
    private final PasswordEncoder passwordEncoder;

    private static final List<String> TEST_EMAILS = List.of(
            "mateus@pontua.com",
            "resp@pontua.com",
            "prof@pontua.com",
            "adm@pontua.com",
            "diretor@pontua.com",
            "dev@pontua.com"
    );

    @Override
    public void run(String... args) throws Exception {
        // Re-encoda senhas de todos os usuários de teste que já existem
        TEST_EMAILS.forEach(email ->
                usuarioRepository.findByEmail(email).ifPresent(u -> {
                    u.setSenha(passwordEncoder.encode("123456"));
                    usuarioRepository.save(u);
                })
        );

        // Cria usuários de teste que ainda não existem
        criarNaoAlunos();

        if (alunoRepository.findByEmail("mateus@pontua.com").isPresent()) {
            logCredenciais();
            return;
        }

        log.info("Inicializando dados de teste do aluno...");

        // ── ALUNO ────────────────────────────────────────────────────────────
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

        logCredenciais();
    }

    private void criarNaoAlunos() {
        if (!usuarioRepository.existsByEmail("resp@pontua.com")) {
            Responsavel resp = new Responsavel();
            resp.setNome("Ana Responsável");
            resp.setEmail("resp@pontua.com");
            resp.setSenha(passwordEncoder.encode("123456"));
            resp.setTipo(TipoUsuario.RESPONSAVEL);
            resp.setCpf("99988877700");
            responsavelRepository.save(resp);
            log.info("Responsável de teste criado: {}", resp.getEmail());
        }

        if (!usuarioRepository.existsByEmail("prof@pontua.com")) {
            Colaborador prof = new Colaborador();
            prof.setNome("Carlos Professor");
            prof.setEmail("prof@pontua.com");
            prof.setSenha(passwordEncoder.encode("123456"));
            prof.setTipo(TipoUsuario.PROFESSOR);
            prof.setMatricula("COL001");
            prof.setCpf("11122233300");
            prof.setColegio("Colégio Objetivo");
            colaboradorRepository.save(prof);
            log.info("Professor de teste criado: {}", prof.getEmail());
        }

        if (!usuarioRepository.existsByEmail("adm@pontua.com")) {
            Colaborador adm = new Colaborador();
            adm.setNome("Bianca Administradora");
            adm.setEmail("adm@pontua.com");
            adm.setSenha(passwordEncoder.encode("123456"));
            adm.setTipo(TipoUsuario.ADMINISTRADOR);
            adm.setMatricula("COL002");
            adm.setCpf("44455566600");
            adm.setColegio("Colégio Objetivo");
            colaboradorRepository.save(adm);
            log.info("Administrador de teste criado: {}", adm.getEmail());
        }

        if (!usuarioRepository.existsByEmail("diretor@pontua.com")) {
            Colaborador diretor = new Colaborador();
            diretor.setNome("Roberto Diretor");
            diretor.setEmail("diretor@pontua.com");
            diretor.setSenha(passwordEncoder.encode("123456"));
            diretor.setTipo(TipoUsuario.DIRETOR);
            diretor.setMatricula("COL003");
            diretor.setCpf("77788899900");
            diretor.setColegio("Colégio Objetivo");
            colaboradorRepository.save(diretor);
            log.info("Diretor de teste criado: {}", diretor.getEmail());
        }

        if (!usuarioRepository.existsByEmail("dev@pontua.com")) {
            Colaborador dev = new Colaborador();
            dev.setNome("Dev Teste");
            dev.setEmail("dev@pontua.com");
            dev.setSenha(passwordEncoder.encode("123456"));
            dev.setTipo(TipoUsuario.DEV);
            dev.setMatricula("COL004");
            dev.setCpf("12312312300");
            dev.setColegio("Colégio Objetivo");
            colaboradorRepository.save(dev);
            log.info("Dev de teste criado: {}", dev.getEmail());
        }
    }

    private void logCredenciais() {
        log.info("╔══════════════════════════════════════════════════════════════╗");
        log.info("║              [DEV] Credenciais de teste (senha: 123456)      ║");
        log.info("╠══════════════════════════════════════════════════════════════╣");
        log.info("║  ALUNO         → mateus@pontua.com    → /dashboard.html     ║");
        log.info("║  RESPONSAVEL   → resp@pontua.com      → /responsavel/...    ║");
        log.info("║  PROFESSOR     → prof@pontua.com      → /professor-dash...  ║");
        log.info("║  ADMINISTRADOR → adm@pontua.com       → /adm/dashboard.html ║");
        log.info("║  DIRETOR       → diretor@pontua.com   → /diretor/dash...    ║");
        log.info("║  DEV           → dev@pontua.com       → /dev/dashboard.html ║");
        log.info("╚══════════════════════════════════════════════════════════════╝");
    }
}
