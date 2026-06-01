// Perfil TypeScript/JavaScript

// Carregar dados do perfil
async function carregarPerfil() {
    try {
        const response = await fetch('/api/auth/me');

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                window.location.href = '/login.html';
                return;
            }
            throw new Error('Erro ao carregar perfil');
        }

        const aluno = await response.json();
        renderizarPerfil(aluno);
    } catch (error) {
        alert('Erro ao carregar dados do perfil');
    }
}

// Renderizar dados do perfil
function renderizarPerfil(aluno) {
    // Ocultar loading e mostrar conteúdo
    document.getElementById('loading').style.display = 'none';
    document.getElementById('perfilContent').style.display = 'block';

    // Atualizar nome do usuário no header
    document.getElementById('userName').textContent = aluno.nome;
    const iniciais = aluno.nome.split(' ').map(n => n[0]).slice(0, 2).join('');
    document.getElementById('userAvatar').textContent = iniciais;

    // Informações Pessoais
    document.getElementById('nomeCompleto').textContent = aluno.nome;
    document.getElementById('email').textContent = aluno.email;
    document.getElementById('dataNascimento').textContent = formatarData(aluno.dataNascimento);

    // Informações Acadêmicas
    document.getElementById('matricula').textContent = aluno.matricula;
    document.getElementById('serie').textContent = aluno.serie;
    document.getElementById('colegio').textContent = aluno.colegio;
    document.getElementById('turma').textContent = aluno.turma || 'Não informada';
    document.getElementById('dataIngresso').textContent = formatarData(aluno.dataIngresso);
}

// Formatar data
function formatarData(dataString) {
    if (!dataString) return '-';

    const data = new Date(dataString + 'T00:00:00');
    const dia = String(data.getDate()).padStart(2, '0');
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const ano = data.getFullYear();

    return `${dia}/${mes}/${ano}`;
}

// Função de logout
function logout() {
    if (confirm('Tem certeza que deseja sair?')) {
        window.location.href = '/api/auth/logout';
    }
}

async function carregarEmblemas() {
    try {
        const response = await fetch('/api/recompensas/emblemas');
        if (!response.ok) return;
        const emblemas = await response.json();
        renderizarEmblemas(emblemas);
    } catch (_) {}
}

function renderizarEmblemas(emblemas) {
    const grid = document.getElementById('emblemasGrid');
    if (!emblemas || emblemas.length === 0) {
        grid.innerHTML = '<p style="color: var(--cinza-escuro); font-size: 14px;">Nenhum emblema conquistado ainda.</p>';
        return;
    }

    const tierCores = {
        DIAMOND: { bg: 'var(--diamante-light)', cor: 'var(--diamante)',    label: 'Diamante' },
        OURO:    { bg: '#fef9c3',               cor: 'var(--ouro)',         label: 'Ouro'     },
        PRATA:   { bg: '#f1f5f9',               cor: 'var(--prata)',        label: 'Prata'    },
        BRONZE:  { bg: '#fef3c7',               cor: 'var(--bronze)',       label: 'Bronze'   },
    };

    grid.innerHTML = emblemas.map(e => {
        const cfg = tierCores[e.ranking] ?? { bg: '#f5f5f5', cor: '#666', label: e.ranking };
        return `
            <div class="emblema-item" style="background: ${cfg.bg}; border-color: ${cfg.cor};">
                <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="${cfg.cor}" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" style="margin-bottom:8px;">
                    <circle cx="12" cy="8" r="6"/>
                    <path d="M15.477 12.89 17 22l-5-3-5 3 1.523-9.11"/>
                </svg>
                <div class="emblema-titulo">${escapeHtmlPerfil(e.titulo)}</div>
                <div class="emblema-bimestre">${e.bimestre}º bimestre / ${e.ano}</div>
            </div>
        `;
    }).join('');
}

function escapeHtmlPerfil(value) {
    const div = document.createElement('div');
    div.textContent = String(value ?? '');
    return div.innerHTML;
}

// Carregar perfil ao carregar a página
window.addEventListener('DOMContentLoaded', () => {
    carregarPerfil();
    carregarEmblemas();
});
