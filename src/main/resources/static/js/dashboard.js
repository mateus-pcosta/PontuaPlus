// Dashboard JavaScript - Redesigned
const mesesNomes = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];

let notasChart = null;
let frequenciaChart = null;

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

    // Atualizar cards superiores
    if (data.pontuacao) {
        renderizarCardsSuperiores(data.pontuacao);
    }

    // Renderizar gráficos
    renderizarGraficoNotas(data.notas);
    renderizarGraficoFrequencia(data.frequencias);

    // Renderizar detalhamento da pontuação
    if (data.pontuacao) {
        renderizarDetalhamentoPontuacao(data.pontuacao);
    }

    // Renderizar desempenho por matéria
    renderizarDesempenhoPorMateria(data.notas);

    // Renderizar atividades extras
    renderizarAtividades(data.atividadesExtras);
}

// Renderizar cards superiores
function renderizarCardsSuperiores(pontuacao) {
    // Pontos Atuais
    document.getElementById('totalPontos').textContent = pontuacao.totalPontos;

    // Notas e Frequência (35 + 15 = 50 pontos máx)
    const notasFreq = pontuacao.pontosNotas + pontuacao.pontosFrequencia;
    document.getElementById('notasFrequencia').textContent = `${notasFreq}/50`;
    const notasFreqPercent = (notasFreq / 50) * 100;
    document.getElementById('notasFrequenciaBar').style.width = `${notasFreqPercent}%`;

    // Evolução e Esforço (atividades extras, 50 pontos máx)
    document.getElementById('evolucaoEsforco').textContent = `${pontuacao.pontosExtras}/50`;
    const evolucaoPercent = (pontuacao.pontosExtras / 50) * 100;
    document.getElementById('evolucaoEsforcoBar').style.width = `${evolucaoPercent}%`;

    // Ranking
    document.getElementById('rankingAtual').textContent = formatarRanking(pontuacao.ranking);
    document.getElementById('posicaoRanking').textContent = `${pontuacao.posicaoRanking}º Lugar`;
}

// Renderizar gráfico de evolução das notas
function renderizarGraficoNotas(notas) {
    const ctx = document.getElementById('notasChart');

    if (!notas || notas.length === 0) {
        ctx.parentElement.innerHTML = '<p style="text-align: center; padding: 40px; color: var(--cinza-escuro);">Nenhuma nota disponível</p>';
        return;
    }

    // Agrupar notas por bimestre e calcular média
    const notasPorBimestre = {};

    notas.forEach(nota => {
        if (!notasPorBimestre[nota.bimestre]) {
            notasPorBimestre[nota.bimestre] = [];
        }
        notasPorBimestre[nota.bimestre].push(nota.valor);
    });

    // Calcular médias por bimestre
    // Bimestre 1: Jan-Fev, Bimestre 2: Jun-Jul
    const meses = [];
    const medias = [];

    // Para cada bimestre que temos notas, distribuir nos meses correspondentes
    Object.keys(notasPorBimestre).forEach(bimestre => {
        const valores = notasPorBimestre[bimestre];
        const media = valores.reduce((a, b) => a + b, 0) / valores.length;

        // Bimestre 1: Janeiro e Fevereiro
        // Bimestre 2: Junho e Julho
        if (bimestre == 1) {
            meses.push('Jan', 'Fev');
            medias.push(media.toFixed(1), media.toFixed(1));
        } else if (bimestre == 2) {
            meses.push('Jun', 'Jul');
            medias.push(media.toFixed(1), media.toFixed(1));
        }
    });

    // Destruir gráfico anterior se existir
    if (notasChart) {
        notasChart.destroy();
    }

    // Criar novo gráfico
    notasChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: meses,
            datasets: [{
                label: 'Média das Notas',
                data: medias,
                borderColor: '#2A5BDA',
                backgroundColor: 'rgba(42, 91, 218, 0.1)',
                borderWidth: 3,
                tension: 0.4,
                fill: true,
                pointRadius: 5,
                pointHoverRadius: 7,
                pointBackgroundColor: '#2A5BDA',
                pointBorderColor: '#fff',
                pointBorderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(42, 91, 218, 0.9)',
                    padding: 12,
                    titleFont: {
                        size: 14,
                        weight: 'bold'
                    },
                    bodyFont: {
                        size: 13
                    },
                    callbacks: {
                        label: function(context) {
                            return 'Média: ' + context.parsed.y;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 10,
                    ticks: {
                        stepSize: 2,
                        font: {
                            size: 12
                        }
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                },
                x: {
                    ticks: {
                        font: {
                            size: 12
                        }
                    },
                    grid: {
                        display: false
                    }
                }
            }
        }
    });
}

// Renderizar gráfico de frequência mensal
function renderizarGraficoFrequencia(frequencias) {
    const ctx = document.getElementById('frequenciaChart');

    if (!frequencias || frequencias.length === 0) {
        ctx.parentElement.innerHTML = '<p style="text-align: center; padding: 40px; color: var(--cinza-escuro);">Nenhuma frequência disponível</p>';
        return;
    }

    // Preparar dados
    const meses = [];
    const presencas = [];
    const faltas = [];

    frequencias.forEach(freq => {
        meses.push(mesesNomes[freq.mes - 1].substring(0, 3));
        presencas.push(freq.presencas);
        faltas.push(freq.faltas);
    });

    // Destruir gráfico anterior se existir
    if (frequenciaChart) {
        frequenciaChart.destroy();
    }

    // Criar novo gráfico
    frequenciaChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: meses,
            datasets: [
                {
                    label: 'Presenças',
                    data: presencas,
                    backgroundColor: '#2A5BDA',
                    borderRadius: 6,
                    borderSkipped: false
                },
                {
                    label: 'Faltas',
                    data: faltas,
                    backgroundColor: '#FF4757',
                    borderRadius: 6,
                    borderSkipped: false
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        padding: 15,
                        font: {
                            size: 13,
                            weight: '600'
                        },
                        usePointStyle: true,
                        pointStyle: 'circle'
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(20, 23, 26, 0.9)',
                    padding: 12,
                    titleFont: {
                        size: 14,
                        weight: 'bold'
                    },
                    bodyFont: {
                        size: 13
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 5,
                        font: {
                            size: 12
                        }
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                },
                x: {
                    ticks: {
                        font: {
                            size: 12
                        }
                    },
                    grid: {
                        display: false
                    }
                }
            }
        }
    });
}

// Renderizar detalhamento da pontuação
function renderizarDetalhamentoPontuacao(pontuacao) {
    // Pontos por Notas (máx 35)
    document.getElementById('pontosNotas').textContent = `${pontuacao.pontosNotas}/35`;
    const notasPercent = (pontuacao.pontosNotas / 35) * 100;
    document.getElementById('pontosNotasBar').style.width = `${notasPercent}%`;

    // Pontos por Frequência (máx 15)
    document.getElementById('pontosFrequencia').textContent = `${pontuacao.pontosFrequencia}/15`;
    const frequenciaPercent = (pontuacao.pontosFrequencia / 15) * 100;
    document.getElementById('pontosFrequenciaBar').style.width = `${frequenciaPercent}%`;

    // Pontos por Atividades (máx 50)
    document.getElementById('pontosExtras').textContent = `${pontuacao.pontosExtras}/50`;
    const extrasPercent = (pontuacao.pontosExtras / 50) * 100;
    document.getElementById('pontosExtrasBar').style.width = `${extrasPercent}%`;
}

// Renderizar desempenho por matéria
function renderizarDesempenhoPorMateria(notas) {
    const materiasSection = document.getElementById('materiasSection');

    if (!notas || notas.length === 0) {
        materiasSection.innerHTML = '<p style="text-align: center; color: var(--cinza-escuro);">Nenhuma nota disponível</p>';
        return;
    }

    // Agrupar notas por disciplina e calcular média
    const materias = {};
    notas.forEach(nota => {
        if (!materias[nota.disciplina]) {
            materias[nota.disciplina] = [];
        }
        materias[nota.disciplina].push(nota.valor);
    });

    // Criar HTML para cada matéria
    let html = '';
    Object.keys(materias).forEach(disciplina => {
        const valores = materias[disciplina];
        const media = valores.reduce((a, b) => a + b, 0) / valores.length;
        const percentual = (media / 10) * 100;

        html += `
            <div class="materia-item">
                <div class="materia-nome">${disciplina}</div>
                <div class="materia-bar-container">
                    <div class="materia-bar-fill" style="width: ${percentual}%">
                        ${media.toFixed(1)}
                    </div>
                </div>
                <div class="materia-nota">${media.toFixed(1)}</div>
            </div>
        `;
    });

    materiasSection.innerHTML = html;
}

// Renderizar atividades extras
function renderizarAtividades(atividades) {
    const atividadesGrid = document.getElementById('atividadesGrid');

    if (!atividades || atividades.length === 0) {
        atividadesGrid.innerHTML = '<p style="text-align: center; color: var(--cinza-escuro); grid-column: 1 / -1;">Nenhuma atividade extra encontrada</p>';
        return;
    }

    atividadesGrid.innerHTML = atividades.map(atividade => `
        <div class="atividade-card">
            <div class="atividade-nome">${atividade.nome}</div>
            <div class="atividade-tipo">${atividade.tipo}</div>
            <div class="atividade-pontos">+${atividade.pontosConquistados} pontos</div>
        </div>
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

// Carregar dashboard ao carregar a página
window.addEventListener('DOMContentLoaded', carregarDashboard);
