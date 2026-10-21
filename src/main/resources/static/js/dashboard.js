// Dashboard JavaScript - Redesigned

function escapeHtml(value) {
    const div = document.createElement('div');
    div.textContent = String(value ?? '');
    return div.innerHTML;
}

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
        const loading = document.getElementById('loading');
        if (loading) {
            loading.innerHTML = '<p style="color:#999; text-align:center; padding:24px;">Não foi possível carregar os dados deste perfil.</p>';
        }
    }
}

// Renderizar dados do dashboard
function renderizarDashboard(data) {
    // Ocultar loading e mostrar conteúdo
    document.getElementById('loading').style.display = 'none';
    document.getElementById('dashboardContent').style.display = 'block';

    // Atualizar nome do usuário
    const iniciais = data.aluno.nome.split(' ').map(n => n[0]).slice(0, 2).join('');
    document.getElementById('userAvatar').textContent = iniciais;

    // Atualizar cards superiores
    if (data.pontuacao) {
        renderizarCardsSuperiores(data.pontuacao);
    }

    // Renderizar gráficos
    renderizarGraficoNotas(data.notas, data.mediaTurma);
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

// Cor de destaque de cada nível (só apresentação — os pontos de corte
// de cada tier vêm prontos do backend, fonte única de verdade em Ranking.java)
const NIVEL_COR = { BRONZE: 'bronze', PRATA: 'prata', OURO: 'ouro', DIAMOND: 'diamante' };

// Renderizar cards superiores
function renderizarCardsSuperiores(pontuacao) {
    // Pontos Atuais
    document.getElementById('totalPontos').textContent = pontuacao.totalPontos;

    const badgeAtual = document.getElementById('nivelAtualBadge');
    badgeAtual.textContent = `Nível ${formatarRanking(pontuacao.ranking)}`;
    badgeAtual.className = `tier-nivel-badge badge-${pontuacao.ranking.toLowerCase()}`;

    // Notas e Frequência (35 + 15 = 50 pontos máx)
    const notasFreq = pontuacao.pontosNotas + pontuacao.pontosFrequencia;
    document.getElementById('notasFrequencia').textContent = `${notasFreq}/50`;
    const notasFreqPercent = (notasFreq / 50) * 100;
    document.getElementById('notasFrequenciaBar').style.width = `${notasFreqPercent}%`;

    // Evolução e Esforço (atividades extras, 50 pontos máx)
    document.getElementById('evolucaoEsforco').textContent = `${pontuacao.pontosExtras}/50`;
    const evolucaoPercent = (pontuacao.pontosExtras / 50) * 100;
    document.getElementById('evolucaoEsforcoBar').style.width = `${evolucaoPercent}%`;

    // Próximo Nível (proximoNivel/pontosParaProximo já vêm calculados da API)
    const proximoNivelEl = document.getElementById('proximoNivel');
    const pontosProximoNivelEl = document.getElementById('pontosProximoNivel');
    if (pontuacao.proximoNivel) {
        proximoNivelEl.textContent = formatarRanking(pontuacao.proximoNivel);
        proximoNivelEl.style.color = `var(--${NIVEL_COR[pontuacao.proximoNivel]})`;
        const rotuloPontos = pontuacao.pontosParaProximo === 1 ? 'ponto' : 'pontos';
        pontosProximoNivelEl.textContent = `${pontuacao.pontosParaProximo} ${rotuloPontos} para o próximo nível`;
    } else {
        proximoNivelEl.textContent = 'Nível máximo';
        proximoNivelEl.style.color = 'var(--diamante)';
        pontosProximoNivelEl.textContent = 'Você atingiu o nível mais alto!';
    }
}

// Renderizar gráfico de evolução das notas
function renderizarGraficoNotas(notas, mediaTurma) {
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

    // Média da turma por bimestre (vem pronta do backend)
    const mediaTurmaPorBimestre = {};
    (mediaTurma || []).forEach(item => {
        mediaTurmaPorBimestre[item.bimestre] = item.media;
    });

    // Calcular médias por bimestre
    const meses = [];
    const medias = [];
    const mediasTurma = [];

    const bimestreMeses = {
        1: ['Jan', 'Fev'],
        2: ['Jun', 'Jul'],
        3: ['Ago', 'Set'],
        4: ['Out', 'Nov', 'Dez'],
    };

    Object.keys(notasPorBimestre)
        .sort((a, b) => Number(a) - Number(b))
        .forEach(bimestre => {
            const valores = notasPorBimestre[bimestre];
            const media = valores.reduce((a, b) => a + b, 0) / valores.length;
            const nomesMeses = bimestreMeses[bimestre];
            if (!nomesMeses) return;
            const mediaTurmaBimestre = mediaTurmaPorBimestre[bimestre];
            nomesMeses.forEach(m => {
                meses.push(m);
                medias.push(media.toFixed(1));
                mediasTurma.push(mediaTurmaBimestre !== undefined ? mediaTurmaBimestre.toFixed(1) : null);
            });
        });

    // Destruir gráfico anterior (e cancelar sua animação de revelação, se ainda rodando)
    if (notasChart) {
        if (notasChart.$revelacaoRaf) cancelAnimationFrame(notasChart.$revelacaoRaf);
        notasChart.destroy();
    }

    const temMediaTurma = mediasTurma.some(v => v !== null);

    const datasets = [{
        label: 'Seu Desempenho',
        data: medias,
        borderColor: '#2A5BDA',
        borderWidth: 3,
        tension: 0.4,
        fill: false,
        pointRadius: 5,
        pointHoverRadius: 7,
        pointBackgroundColor: '#2A5BDA',
        pointBorderColor: '#fff',
        pointBorderWidth: 2
    }];

    if (temMediaTurma) {
        datasets.push({
            label: 'Média da Turma',
            data: mediasTurma,
            borderColor: '#8b5cf6',
            backgroundColor: 'rgba(139, 92, 246, 0.08)',
            borderWidth: 2,
            borderDash: [6, 4],
            tension: 0.4,
            fill: false,
            pointRadius: 4,
            pointHoverRadius: 6,
            pointBackgroundColor: '#8b5cf6',
            pointBorderColor: '#fff',
            pointBorderWidth: 2,
            spanGaps: true
        });
    }

    // Única animação mantida no produto: uma "cortina" revela o gráfico da
    // esquerda pra direita de forma contínua e suave, em vez de animar ponto a
    // ponto (que com poucos pontos — 1 por mês — fica visivelmente travado).
    // A linha inteira é desenhada na posição final desde o primeiro frame; um
    // clip (retângulo que cresce da esquerda pra direita) é quem esconde/revela
    // o que já foi "desenhado", redesenhado a cada requestAnimationFrame.
    const revelacaoEsquerdaDireita = {
        id: 'revelacaoEsquerdaDireita',
        beforeDatasetsDraw(chart) {
            const { ctx, chartArea } = chart;
            if (!chartArea) return;
            const progresso = chart.$revelacaoProgresso ?? 1;
            const largura = (chartArea.right - chartArea.left) * progresso;
            // Folga maior que raio+borda do ponto, senão a metade externa dos
            // pontos nas pontas (primeiro e último mês) fica cortada pelo clip
            const folga = 12;
            ctx.save();
            ctx.beginPath();
            ctx.rect(chartArea.left - folga, chartArea.top - 20, largura + folga * 2, chartArea.height + 40);
            ctx.clip();
        },
        afterDatasetsDraw(chart) {
            chart.ctx.restore();
        }
    };

    // Criar novo gráfico
    notasChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: meses,
            datasets: datasets
        },
        plugins: [revelacaoEsquerdaDireita],
        options: {
            responsive: true,
            maintainAspectRatio: true,
            animation: false,
            plugins: {
                legend: {
                    display: temMediaTurma,
                    position: 'bottom',
                    labels: {
                        usePointStyle: true,
                        pointStyle: 'circle',
                        padding: 16,
                        font: { size: 12 }
                    }
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
                            return context.dataset.label + ': ' + context.parsed.y;
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

    // Roda a cortina de 0% a 100% em 1s, com uma leve desaceleração no final
    const duracaoRevelacaoMs = 1000;
    const inicioRevelacao = performance.now();

    function animarRevelacao(agora) {
        const t = Math.min((agora - inicioRevelacao) / duracaoRevelacaoMs, 1);
        const suavizado = -(Math.cos(Math.PI * t) - 1) / 2; // ease-in-out senoidal: usa os 2s inteiros, sem "parar" no fim
        notasChart.$revelacaoProgresso = suavizado;
        notasChart.draw();
        if (t < 1) {
            notasChart.$revelacaoRaf = requestAnimationFrame(animarRevelacao);
        }
    }

    notasChart.$revelacaoProgresso = 0;
    notasChart.$revelacaoRaf = requestAnimationFrame(animarRevelacao);
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

    [...frequencias]
        .sort((a, b) => (a.ano * 12 + a.mes) - (b.ano * 12 + b.mes))
        .forEach(freq => {
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
            animation: {
                y: {
                    duration: 1000,
                    easing: 'easeOutQuart',
                    from: (context) => context.chart.scales.y.getPixelForValue(0)
                }
            },
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
                <div class="materia-nome">${escapeHtml(disciplina)}</div>
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
            <div class="atividade-nome">${escapeHtml(atividade.nome)}</div>
            <div class="atividade-tipo">${escapeHtml(atividade.tipo)}</div>
            <div class="atividade-pontos">+${escapeHtml(atividade.pontosConquistados)} pontos</div>
        </div>
    `).join('');
}

// Formatar ranking
function formatarRanking(ranking) {
    const rankingMap = {
        'BRONZE': 'Bronze',
        'PRATA': 'Prata',
        'OURO': 'Ouro',
        'DIAMOND': 'Diamante'
    };
    return rankingMap[ranking] || ranking;
}

// Carregar dashboard ao carregar a página
window.addEventListener('DOMContentLoaded', carregarDashboard);
