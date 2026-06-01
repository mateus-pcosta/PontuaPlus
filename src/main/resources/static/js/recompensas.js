function escapeHtml(value) {
    const div = document.createElement('div');
    div.textContent = String(value ?? '');
    return div.innerHTML;
}

const TIER_CONFIG = {
    DIAMOND: { label: 'Diamante', cssClass: 'tier-diamond', badgeClass: 'badge-diamond' },
    OURO:    { label: 'Ouro',     cssClass: 'tier-ouro',    badgeClass: 'badge-ouro'    },
    PRATA:   { label: 'Prata',    cssClass: 'tier-prata',   badgeClass: 'badge-prata'   },
    BRONZE:  { label: 'Bronze',   cssClass: 'tier-bronze',  badgeClass: 'badge-bronze'  },
};

async function carregarRecompensas() {
    try {
        const response = await fetch('/api/recompensas');
        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                window.location.href = '/login.html';
                return;
            }
            throw new Error('Erro ao carregar recompensas');
        }
        const data = await response.json();
        renderizarRecompensas(data);
    } catch (err) {
        console.error(err);
        document.getElementById('loading').innerHTML =
            '<p style="color:var(--vermelho-alerta);">Erro ao carregar recompensas. Tente novamente.</p>';
    }
}

function renderizarRecompensas(data) {
    renderizarStatus(data.statusAtual);
    atualizarAvatar(data);

    const container = document.getElementById('tiersContainer');
    container.innerHTML = '';
    data.tiers.forEach(tier => container.appendChild(criarSecaoTier(tier, data.statusAtual.rankingAtual)));

    document.getElementById('loading').style.display = 'none';
    document.getElementById('recompensasContent').style.display = 'block';
}

function atualizarAvatar(data) {
    const todasRecompensas = data.tiers.flatMap(t => t.recompensas ?? []);
    // o nome vem do status ou da página de perfil — fallback às iniciais do email
    // apenas atualiza se vier do backend no futuro; por ora mantém o padrão
}

function renderizarStatus(status) {
    const cfg = TIER_CONFIG[status.rankingAtual] ?? { label: status.rankingAtual, badgeClass: '' };

    document.getElementById('statusPontos').textContent = status.totalPontos;

    const badge = document.getElementById('statusBadge');
    badge.textContent = 'Nível ' + cfg.label;
    badge.className = 'tier-nivel-badge ' + cfg.badgeClass;

    const proximoContainer = document.getElementById('statusProximoContainer');
    if (status.proximoNivel) {
        const proximoCfg = TIER_CONFIG[status.proximoNivel] ?? { label: status.proximoNivel };
        document.getElementById('statusParaProximo').textContent = status.pontosParaProximo;
        document.getElementById('statusProximoLabel').textContent = 'Para ' + proximoCfg.label;

        const pontoMinimoProximo = getPontoMinimo(status.proximoNivel);
        const progressoPct = pontoMinimoProximo > 0
            ? Math.min(100, Math.round((status.totalPontos / pontoMinimoProximo) * 100))
            : 100;
        document.getElementById('statusProgressoTexto').textContent =
            status.totalPontos + '/' + pontoMinimoProximo + ' pontos';
        document.getElementById('statusProgressoBar').style.width = progressoPct + '%';
    } else {
        proximoContainer.innerHTML =
            '<span class="status-valor" style="color:var(--diamante);">MAX</span>' +
            '<span class="status-label">Nível Máximo</span>';
        document.getElementById('statusProgressoTexto').textContent = '100/100 pontos';
        document.getElementById('statusProgressoBar').style.width = '100%';
    }
}

function getPontoMinimo(tier) {
    const map = { BRONZE: 0, PRATA: 26, OURO: 65, DIAMOND: 81 };
    return map[tier] ?? 0;
}

function criarSecaoTier(tier, meuRanking) {
    const cfg = TIER_CONFIG[tier.tier] ?? { label: tier.tier, cssClass: '', badgeClass: '' };
    const ehMeuTier = tier.tier === meuRanking;
    const tiers = ['DIAMOND', 'OURO', 'PRATA', 'BRONZE'];
    const meuIndice = tiers.indexOf(meuRanking);
    const tierIndice = tiers.indexOf(tier.tier);
    const acimaDeMim = tierIndice < meuIndice;
    const abaixoDeMim = tierIndice > meuIndice;

    const secao = document.createElement('div');
    secao.className = 'recompensa-secao ' + cfg.cssClass;

    const badgeHtml = ehMeuTier
        ? '<span class="tier-badge">Seu Nível</span>'
        : acimaDeMim
            ? '<span class="tier-badge-bloqueado">Bloqueado</span>'
            : '<span class="tier-badge-conquistado">Conquistado</span>';

    const pontoLabel = tier.tier === 'DIAMOND' ? '81–100 pontos' :
                       tier.tier === 'OURO'    ? '65–80 pontos'  :
                       tier.tier === 'PRATA'   ? '26–64 pontos'  : '0–25 pontos';

    secao.innerHTML = `
        <div class="recompensa-secao-header ${acimaDeMim ? 'bloqueado' : ''}">
            <div>
                <div class="recompensa-secao-titulo">
                    ${escapeHtml(cfg.label)} ${badgeHtml}
                </div>
                <div class="recompensa-secao-sub">Requer ${escapeHtml(pontoLabel)}</div>
            </div>
            ${acimaDeMim ? `
            <span class="tier-lock-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
            </span>` : ''}
        </div>
        <div class="recompensa-grid" id="grid-${escapeHtml(tier.tier)}"></div>
    `;

    const grid = secao.querySelector(`#grid-${tier.tier}`);

    if (tier.recompensas && tier.recompensas.length > 0) {
        tier.recompensas.forEach(r => {
            grid.appendChild(criarCardRecompensa(r, ehMeuTier, acimaDeMim));
        });
    } else if (tier.tier === 'BRONZE') {
        grid.innerHTML = `
            <div class="recompensa-bronze-msg">
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="var(--bronze)" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="8" r="6"/>
                    <path d="M15.477 12.89 17 22l-5-3-5 3 1.523-9.11"/>
                </svg>
                <p>Nível Bronze — conquiste o <strong>Emblema Digital de Bronze</strong> e suba de nível para desbloquear recompensas!</p>
            </div>
        `;
    }

    return secao;
}

function criarCardRecompensa(recompensa, desbloqueado, bloqueado) {
    const card = document.createElement('div');
    card.className = 'recompensa-card' + (bloqueado ? ' bloqueado' : '');

    const iconeMap = {
        gamepad:    `<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><line x1="6" y1="12" x2="10" y2="12"/><line x1="8" y1="10" x2="8" y2="14"/><circle cx="15" cy="12" r="1"/><circle cx="18" cy="10" r="1"/><path d="M17.32 5H6.68a4 4 0 0 0-3.978 3.59c-.006.052-.01.101-.017.152C2.604 9.416 2 14.456 2 16a3 3 0 0 0 3 3c1 0 1.5-.5 2-1l1.414-1.414A2 2 0 0 1 9.828 16h4.344a2 2 0 0 1 1.414.586L17 18c.5.5 1 1 2 1a3 3 0 0 0 3-3c0-1.545-.604-6.584-.685-7.258-.007-.05-.011-.1-.017-.151A4 4 0 0 0 17.32 5z"/></svg>`,
        graduation: `<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>`,
        monitor:    `<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>`,
        book:       `<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/></svg>`,
        film:       `<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="2" width="20" height="20" rx="2.18" ry="2.18"/><line x1="7" y1="2" x2="7" y2="22"/><line x1="17" y1="2" x2="17" y2="22"/><line x1="2" y1="12" x2="22" y2="12"/><line x1="2" y1="7" x2="7" y2="7"/><line x1="2" y1="17" x2="7" y2="17"/><line x1="17" y1="17" x2="22" y2="17"/><line x1="17" y1="7" x2="22" y2="7"/></svg>`,
        pencil:     `<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="2" x2="22" y2="6"/><path d="M7.5 20.5 19 9l-4-4L3.5 16.5 2 22z"/></svg>`,
        tag:        `<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>`,
        coffee:     `<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8h1a4 4 0 0 1 0 8h-1"/><path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"/><line x1="6" y1="1" x2="6" y2="4"/><line x1="10" y1="1" x2="10" y2="4"/><line x1="14" y1="1" x2="14" y2="4"/></svg>`,
    };

    const icone = iconeMap[recompensa.icone] ?? iconeMap['tag'];
    const lockIcon = bloqueado
        ? `<svg class="card-lock" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>`
        : '';

    card.innerHTML = `
        <div class="recompensa-card-icone">${icone}</div>
        ${lockIcon}
        <div class="recompensa-card-nome">${escapeHtml(recompensa.nome)}</div>
        <div class="recompensa-card-desc">${escapeHtml(recompensa.descricao)}</div>
        <div class="recompensa-parceiro">${escapeHtml(recompensa.parceiro)}</div>
        ${desbloqueado
            ? `<button class="btn btn-primary btn-sm recompensa-btn" onclick="document.getElementById('recompensaModal').style.display='flex'">
                   Resgatar
               </button>`
            : bloqueado
                ? `<span class="recompensa-status-bloqueado">Bloqueado</span>`
                : `<span class="recompensa-status-conquistado">Disponível</span>`
        }
    `;

    return card;
}

carregarRecompensas();
