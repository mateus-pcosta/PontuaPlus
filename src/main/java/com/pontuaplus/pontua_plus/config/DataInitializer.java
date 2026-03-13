package com.pontuaplus.pontua_plus.config;

import com.pontuaplus.pontua_plus.entity.*;
import com.pontuaplus.pontua_plus.enums.TipoAtividade;
import com.pontuaplus.pontua_plus.enums.TipoUsuario;
import com.pontuaplus.pontua_plus.repository.*;
import com.pontuaplus.pontua_plus.service.PontuacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AlunoRepository alunoRepository;
    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final AtividadeExtraRepository atividadeExtraRepository;
    private final PontuacaoService pontuacaoService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verificar se já existe o aluno Mateus
        if (alunoRepository.findByEmail("mateus@pontua.com").isPresent()) {
            System.out.println("Dados já inicializados!");
            return;
        }

        System.out.println("Inicializando dados do usuário Mateus...");

        // Criar aluno Mateus
        Aluno mateus = new Aluno();
        mateus.setNome("Mateus Pessoa Costa");
        mateus.setEmail("mateus@pontua.com");
        mateus.setSenha(passwordEncoder.encode("123456")); // Senha: 123456
        mateus.setTipo(TipoUsuario.ALUNO);
        mateus.setMatricula("2024001");
        mateus.setCpf("12345678900");
        mateus.setSerie("9º Ano");
        mateus.setColegio("Colégio Objetivo");
        mateus.setDataNascimento(LocalDate.of(2009, 5, 15));
        mateus.setDataIngresso(LocalDate.of(2024, 1, 1));
        mateus.setTurma("9A");
        mateus.setBimestreAtual(2); // 2º Bimestre (Junho/Julho)

        mateus = alunoRepository.save(mateus);
        System.out.println("Aluno Mateus criado com sucesso!");

        // Criar notas do 2º Bimestre
        List<String> disciplinas = Arrays.asList("Matemática", "Inglês", "Português", "Ciências", "História");
        List<Double> notasValores = Arrays.asList(8.0, 7.5, 7.0, 8.0, 7.0); // Média 7.5

        for (int i = 0; i < disciplinas.size(); i++) {
            Nota nota = new Nota();
            nota.setAluno(mateus);
            nota.setDisciplina(disciplinas.get(i));
            nota.setValor(notasValores.get(i));
            nota.setBimestre(2);
            notaRepository.save(nota);
        }
        System.out.println("Notas do 2º Bimestre criadas com sucesso!");

        // Criar frequências de Junho e Julho (2º Bimestre)
        // Junho: 20 aulas, 18 presenças, 2 faltas = 90%
        Frequencia junho = new Frequencia();
        junho.setAluno(mateus);
        junho.setMes(6);
        junho.setAno(2024);
        junho.setBimestre(2);
        junho.setTotalAulas(20);
        junho.setPresencas(18);
        junho.setFaltas(2);
        frequenciaRepository.save(junho);

        // Julho: 20 aulas, 17 presenças, 3 faltas = 85%
        Frequencia julho = new Frequencia();
        julho.setAluno(mateus);
        julho.setMes(7);
        julho.setAno(2024);
        julho.setBimestre(2);
        julho.setTotalAulas(20);
        julho.setPresencas(17);
        julho.setFaltas(3);
        frequenciaRepository.save(julho);

        System.out.println("Frequências de Junho e Julho criadas com sucesso!");

        // Criar atividades extracurriculares do 2º Bimestre
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

        System.out.println("Atividades extracurriculares do 2º Bimestre criadas com sucesso!");

        // Calcular pontuação
        pontuacaoService.calcularPontuacaoAluno(mateus);
        pontuacaoService.atualizarRankings();
        System.out.println("Pontuação calculada com sucesso!");

        System.out.println("===========================================");
        System.out.println("DADOS INICIALIZADOS COM SUCESSO!");
        System.out.println("===========================================");
        System.out.println("Login: mateus@pontua.com");
        System.out.println("Senha: 123456");
        System.out.println("===========================================");
    }
}
