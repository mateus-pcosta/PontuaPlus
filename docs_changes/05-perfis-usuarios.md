# 05 — Perfis de Usuário (Refatoração e Expansão)

## Motivação

O sistema originalmente tinha 4 perfis (ALUNO, PROFESSOR, COORDENADOR, DIRETOR). Com a evolução do produto, os perfis foram redefinidos para refletir melhor as responsabilidades de cada ator na plataforma.

---

## Mudanças realizadas

### Enum `TipoUsuario`

| Antes | Depois | Motivo |
|---|---|---|
| ALUNO | ALUNO | Mantido |
| — | RESPONSAVEL | Novo — pai/mãe/tutor com acesso à conta do filho |
| PROFESSOR | PROFESSOR | Mantido |
| COORDENADOR | ADMINISTRADOR | Renomeado — papel de gestão operacional da escola |
| — | COORDENADOR | Readicionado — existia no banco; remoção quebrava usuários existentes; redireciona para `/adm/dashboard.html` |
| DIRETOR | DIRETOR | Mantido |
| — | DEV | Novo — acesso total a todos os perfis e funcionalidades |

### Novos arquivos criados

- `entity/Responsavel.java` — entidade com CPF, telefone e lista de filhos (ManyToMany→Aluno)
- `repository/ResponsavelRepository.java`
- `dto/RegistroResponsavelDTO.java`
- `dto/VincularAlunoDTO.java`
- `service/ResponsavelService.java`
- `controller/RegistroResponsavelController.java` → endpoint: `POST /api/registro/responsavel`
- `controller/ResponsavelAuthController.java` → endpoints: `GET /api/responsavel/me`, `GET /api/responsavel/filhos`, `POST /api/responsavel/vincular`

### Arquivos alterados

- `enums/TipoUsuario.java` — adicionados RESPONSAVEL, DEV e COORDENADOR (readicionado); COORDENADOR renomeado para ADMINISTRADOR para novos registros
- `security/CustomSuccessHandler.java` — redirects atualizados:
  - `ROLE_RESPONSAVEL` → `/responsavel/dashboard.html`
  - `ROLE_ADMINISTRADOR` → `/adm/dashboard.html`
  - `ROLE_COORDENADOR` → `/adm/dashboard.html` (fallback para usuários legados)
  - `ROLE_DEV` → `/dev/dashboard.html`
- `security/SecurityConfig.java` — `@EnableMethodSecurity` adicionado; `/responsavel-registro.html` adicionado a `permitAll`
- `static/colaborador-registro.html` — dropdown de cargo atualizado (COORDENADOR → ADMINISTRADOR, DEV adicionado)
- `config/DataInitializer.java` — cria usuários de teste para todos os perfis (ver `06-correcoes-bugs-e-dados-teste.md`)
- `docs/ARQUITETURA.md` — enum e controllers documentados corretamente
- `docs/DIAGRAMA_ER.md` — tabelas `responsaveis`, `colaboradores` e `responsaveis_alunos` documentadas

---

## Tabelas criadas no banco

```sql
CREATE TABLE responsaveis (
    id       BIGINT PRIMARY KEY REFERENCES usuarios(id),
    cpf      VARCHAR(14) NOT NULL UNIQUE,
    telefone VARCHAR(20)
);

CREATE TABLE responsaveis_alunos (
    responsavel_id BIGINT REFERENCES responsaveis(id),
    aluno_id       BIGINT REFERENCES alunos(id),
    PRIMARY KEY (responsavel_id, aluno_id)
);
```

---

## Permissões por perfil (referência)

| Ação | ALUNO | RESPONSAVEL | PROFESSOR | ADMINISTRADOR | DIRETOR | DEV |
|---|---|---|---|---|---|---|
| Ver próprios dados / dados do filho | ✓ | ✓ | — | — | — | ✓ |
| Submeter atividade extra | ✓ | — | — | — | — | ✓ |
| Avaliar submissões da turma | — | — | ✓ | — | — | ✓ |
| Sugerir nova atividade ao ADM | — | — | ✓ | — | — | ✓ |
| Aprovar/gerenciar catálogo de atividades | — | — | — | ✓ | — | ✓ |
| Escolher atividades do semestre | — | — | — | ✓ | — | ✓ |
| Abrir e fechar semestre/ciclo | — | — | — | ✓ | — | ✓ |
| Visão geral da escola (métricas) | — | — | — | — | ✓ | ✓ |
| Acesso total a todos os perfis | — | — | — | — | — | ✓ |

---

## Pendente (próximas implementações)

- Painel do professor (`/professor-dashboard.html`) com visão de turma e lançamento de atividades
- Telas de gestão no painel ADM (aprovar/rejeitar submissões, gerenciar catálogo)
- Notificações passivas ao responsável (e-mail ao filho ganhar pontos)
