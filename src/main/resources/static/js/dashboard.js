// Dashboard TypeScript/JavaScript
const mesesNomes = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];


// substitui a URL no histórico sem recarregar a página
if (location.pathname.endsWith('.html')) {
  history.replaceState(null, '', '/home');
}


// Carregar dados do dashboard
async function carregarDashboard() {
    try {
        const response = await fetch('/api/dashboard');

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                window.location.href = '/login.html';
                return;
            }
            throw new Error('Erro ao carregar dados');
        }

        const data = await response.json();
        renderizarDashboard(data);
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao carregar dados do dashboard');
    }
}

// Renderizar dados do dashboard
function renderizarDashboard(data) {
    // Ocultar loading e mostrar conteúdo
    document.getElementById('loading').style.display = 'none';
    document.getElementById('dashboardContent').style.display = 'block';

    // Atualizar nome do usuário
    document.getElementById('userName').textContent = data.aluno.nome;
    const iniciais = data.aluno.nome.split(' ').map(n => n[0]).slice(0, 2).join('');
    document.getElementById('userAvatar').textContent = iniciais;

    // Atualizar métricas principais
    if (data.pontuacao) {
        document.getElementById('totalPontos').textContent = data.pontuacao.totalPontos;
        document.getElementById('rankingAtual').textContent = formatarRanking(data.pontuacao.ranking);
        document.getElementById('posicaoRanking').textContent = data.pontuacao.posicaoRanking + 'º';

        // Atualizar resumo de pontos
        document.getElementById('pontosNotas').textContent = data.pontuacao.pontosNotas;
        document.getElementById('pontosFrequencia').textContent = data.pontuacao.pontosFrequencia;
        document.getElementById('pontosExtras').textContent = data.pontuacao.pontosExtras;
    }

    // Renderizar notas
    renderizarNotas(data.notas);

    // Renderizar frequências
    renderizarFrequencias(data.frequencias);

    // Renderizar atividades extras
    renderizarAtividades(data.atividadesExtras);
}

// Renderizar tabela de notas
function renderizarNotas(notas) {
    const tbody = document.getElementById('notasTable');

    if (!notas || notas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">Nenhuma nota encontrada</td></tr>';
        return;
    }

    tbody.innerHTML = notas.map(nota => `
        <tr>
            <td>${nota.disciplina}</td>
            <td>${nota.valor.toFixed(1)}</td>
            <td>${nota.bimestre}º Bimestre</td>
            <td><strong>${nota.pontosConquistados}</strong> pontos</td>
        </tr>
    `).join('');
}

// Renderizar tabela de frequências
function renderizarFrequencias(frequencias) {
    const tbody = document.getElementById('frequenciaTable');

    if (!frequencias || frequencias.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">Nenhuma frequência encontrada</td></tr>';
        return;
    }

    tbody.innerHTML = frequencias.map(freq => `
        <tr>
            <td>${mesesNomes[freq.mes - 1]}/${freq.ano}</td>
            <td>${freq.totalAulas}</td>
            <td>${freq.presencas}</td>
            <td>${freq.faltas}</td>
            <td>${freq.percentualFrequencia.toFixed(1)}%</td>
            <td><strong>${freq.pontosConquistados}</strong> pontos</td>
        </tr>
    `).join('');
}

// Renderizar tabela de atividades extras
function renderizarAtividades(atividades) {
    const tbody = document.getElementById('atividadesTable');

    if (!atividades || atividades.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" style="text-align: center;">Nenhuma atividade extra encontrada</td></tr>';
        return;
    }

    tbody.innerHTML = atividades.map(atividade => `
        <tr>
            <td>${atividade.nome}</td>
            <td>${formatarTipoAtividade(atividade.tipo)}</td>
            <td><strong>${atividade.pontosConquistados}</strong> pontos</td>
        </tr>
    `).join('');
}

// Formatar ranking
function formatarRanking(ranking) {
    const rankingMap = {
        'BRONZE': 'Bronze',
        'PRATA': 'Prata',
        'OURO': 'Ouro',
        'DIAMOND': 'Diamond'
    };
    return rankingMap[ranking] || ranking;
}

// Formatar tipo de atividade
function formatarTipoAtividade(tipo) {
    const tipoMap = {
        'OLIMPIADA': 'Olimpíada',
        'CLUBE': 'Clube',
        'VOLUNTARIADO': 'Voluntariado'
    };
    return tipoMap[tipo] || tipo;
}

// Carregar dashboard ao carregar a página
window.addEventListener('DOMContentLoaded', carregarDashboard);
