function escapeHtml(value) {
    const div = document.createElement('div');
    div.textContent = String(value ?? '');
    return div.innerHTML;
}

const TIER_CONFIG = {
    DIAMOND: {
        label: 'Diamante',
        range: '81 – 100 pontos',
        cssClass: 'tier-diamond',
        icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                 <path d="M6 3h12l4 6-10 12L2 9z"/>
                 <line x1="2" y1="9" x2="22" y2="9"/>
                 <path d="M6 3L2 9m16-6l4 6"/>
                 <path d="M12 3L9 9m3-6l3 6"/>
                 <path d="M6 9l6 12m6-12l-6 12"/>
               </svg>`,
    },
    OURO: {
        label: 'Ouro',
        range: '65 – 80 pontos',
        cssClass: 'tier-ouro',
        icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                 <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/>
                 <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/>
                 <path d="M4 22h16"/>
                 <path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/>
                 <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/>
                 <path d="M18 2H6v7a6 6 0 0 0 12 0V2z"/>
               </svg>`,
    },
    PRATA: {
        label: 'Prata',
        range: '26 – 64 pontos',
        cssClass: 'tier-prata',
        icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                 <path d="M7.21 15L2.66 7.14a2 2 0 0 1 .13-2.2L4.4 2.8A2 2 0 0 1 6 2h12a2 2 0 0 1 1.6.8l1.6 2.14a2 2 0 0 1 .14 2.2L16.79 15"/>
                 <path d="M11 12 5.12 2.2"/>
                 <path d="m13 12 5.88-9.8"/>
                 <path d="M8 7h8"/>
                 <circle cx="12" cy="17" r="5"/>
                 <path d="M12 15v4"/>
                 <path d="M10 17h4"/>
               </svg>`,
    },
    BRONZE: {
        label: 'Bronze',
        range: '0 – 25 pontos',
        cssClass: 'tier-bronze',
        icon: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                 <circle cx="12" cy="8" r="6"/>
                 <path d="M15.477 12.89 17 22l-5-3-5 3 1.523-9.11"/>
               </svg>`,
    },
};

async function carregarRanking() {
    try {
        const response = await fetch('/api/ranking');

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                window.location.href = '/login.html';
                return;
            }
            throw new Error('Erro ao carregar ranking');
        }

        const data = await response.json();
        renderizarRanking(data);

    } catch (err) {
        console.error(err);
        document.getElementById('loading').innerHTML =
            '<p style="color:var(--vermelho-alerta);">Erro ao carregar ranking. Tente novamente.</p>';
    }
}

function renderizarRanking(data) {
    const subtitle = document.getElementById('rankingSubtitle');
    const labelTier = TIER_CONFIG[data.meuRanking]?.label ?? data.meuRanking;
    subtitle.textContent = `Você está no nível ${labelTier} · Posição #${data.minhaPosicao} geral`;

    atualizarAvatar(data.tiers);

    const container = document.getElementById('tiersContainer');
    container.innerHTML = '';

    data.tiers.forEach(tier => {
        container.appendChild(criarCardTier(tier, data.meuRanking));
    });

    document.getElementById('loading').style.display = 'none';
    document.getElementById('rankingContent').style.display = 'block';
}

function atualizarAvatar(tiers) {
    const euMesmo = tiers.flatMap(t => t.alunos ?? []).find(a => a.euMesmo);
    if (!euMesmo) return;
    const avatar = document.getElementById('userAvatar');
    const iniciais = euMesmo.nome.split(' ').map(n => n[0]).slice(0, 2).join('');
    avatar.textContent = iniciais;
}

function criarCardTier(tier, meuRanking) {
    const cfg = TIER_CONFIG[tier.nome] ?? { label: tier.nome, range: '', cssClass: '', icon: '🏅' };
    const ehMeuTier = tier.nome === meuRanking;
    const section = document.createElement('div');
    section.className = `tier-section ${cfg.cssClass}`;

    const badgeHtml = ehMeuTier
        ? `<span class="tier-badge">Seu Nível</span>`
        : '';

    const lockHtml = !tier.acessivel
        ? `<span class="tier-lock-icon">
               <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                   <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                   <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
               </svg>
           </span>`
        : '';

    const percentualHtml = `<span class="tier-percent">${escapeHtml(tier.percentual)}% dos alunos</span>`;

    section.innerHTML = `
        <div class="tier-header ${!tier.acessivel ? 'blocked' : ''}">
            <div class="tier-left">
                <span class="tier-icon">${cfg.icon ?? ''}</span>
                <div>
                    <div class="tier-name">${escapeHtml(cfg.label)} ${badgeHtml}</div>
                    <div class="tier-range">${escapeHtml(cfg.range)}</div>
                </div>
            </div>
            <div class="tier-right">
                ${percentualHtml}
                <span class="tier-alunos">${escapeHtml(tier.totalAlunos)} aluno${tier.totalAlunos !== 1 ? 's' : ''}</span>
                ${lockHtml}
                ${tier.acessivel ? `<svg class="tier-chevron" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"/></svg>` : ''}
            </div>
        </div>
        <div class="tier-body">
            <ol class="ranking-list"></ol>
        </div>
    `;

    if (tier.acessivel) {
        const header = section.querySelector('.tier-header');
        header.addEventListener('click', () => toggleTier(tier, section));

        // Auto-expand o tier do próprio aluno
        if (ehMeuTier) {
            preencherLista(tier, section);
            section.querySelector('.tier-body').classList.add('open');
            const chevron = section.querySelector('.tier-chevron');
            if (chevron) chevron.style.transform = 'rotate(180deg)';
        }
    }

    return section;
}

function toggleTier(tier, section) {
    const body = section.querySelector('.tier-body');
    const chevron = section.querySelector('.tier-chevron');
    const isOpen = body.classList.contains('open');

    if (!isOpen) {
        preencherLista(tier, section);
        body.classList.add('open');
        if (chevron) chevron.style.transform = 'rotate(180deg)';
    } else {
        body.classList.remove('open');
        if (chevron) chevron.style.transform = '';
    }
}

function preencherLista(tier, section) {
    const list = section.querySelector('.ranking-list');
    if (list.dataset.preenchido) return;
    list.dataset.preenchido = '1';

    if (!tier.alunos || tier.alunos.length === 0) {
        list.innerHTML = '<li style="padding:12px;color:var(--cinza-escuro);list-style:none;">Nenhum aluno neste nível ainda.</li>';
        return;
    }

    tier.alunos.forEach(aluno => {
        const li = document.createElement('li');
        li.className = `ranking-item${aluno.euMesmo ? ' eu-mesmo' : ''}`;

        const posicao = aluno.posicaoRanking;
        const top3Class = posicao <= 3 ? ' top3' : '';

        li.innerHTML = `
            <span class="ranking-position${top3Class}">#${escapeHtml(posicao)}</span>
            <span class="ranking-nome">
                ${escapeHtml(aluno.nome)}
                ${aluno.euMesmo ? '<span style="font-size:12px;opacity:0.8;margin-left:6px;">(você)</span>' : ''}
            </span>
            <span class="ranking-pontos">${escapeHtml(aluno.totalPontos)} pts</span>
        `;

        list.appendChild(li);
    });
}

carregarRanking();
