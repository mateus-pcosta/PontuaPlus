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
        console.error('Erro:', error);
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

// Carregar perfil ao carregar a página
window.addEventListener('DOMContentLoaded', carregarPerfil);
