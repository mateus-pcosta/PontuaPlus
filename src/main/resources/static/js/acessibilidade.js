(function () {
    'use strict';

    var STORAGE_KEY = 'pontuaplus_a11y';
    var DEFAULTS = {
        tamanhoFonte: 'MEDIA',
        altoContraste: false,
        reduzMovimento: false,
        sublinharLinks: false,
        libras: false,
        leituraVoz: false
    };

    function carregarPrefsLocais() {
        try {
            var salvo = JSON.parse(localStorage.getItem(STORAGE_KEY) || '{}');
            return Object.assign({}, DEFAULTS, salvo);
        } catch (e) {
            return Object.assign({}, DEFAULTS);
        }
    }

    function salvarPrefsLocais(prefs) {
        try {
            localStorage.setItem(STORAGE_KEY, JSON.stringify(prefs));
        } catch (e) {
            // localStorage indisponível (modo privado, quota etc.) — segue só em memória
        }
    }

    function prefereMovimentoReduzidoNoSO() {
        return window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    }

    // ---- Libras (VLibras) ----

    function carregarVLibras() {
        if (document.getElementById('vlibras-wrapper')) return;

        var wrapper = document.createElement('div');
        wrapper.id = 'vlibras-wrapper';
        wrapper.setAttribute('vw', '');
        wrapper.className = 'enabled';
        wrapper.innerHTML =
            '<div vw-access-button class="active"></div>' +
            '<div vw-plugin-wrapper><div class="vw-plugin-top-wrapper"></div></div>';
        document.body.appendChild(wrapper);

        var script = document.createElement('script');
        script.id = 'vlibras-script';
        script.src = 'https://vlibras.gov.br/app/vlibras-plugin.js';
        script.onload = function () {
            if (window.VLibras) new window.VLibras.Widget('https://vlibras.gov.br/app');
        };
        document.body.appendChild(script);
    }

    function removerVLibras() {
        var wrapper = document.getElementById('vlibras-wrapper');
        if (wrapper) wrapper.remove();
        var script = document.getElementById('vlibras-script');
        if (script) script.remove();
    }

    // ---- Leitura em voz alta (Web Speech API) ----

    function falar(texto) {
        if (!texto || !('speechSynthesis' in window)) return;
        window.speechSynthesis.cancel();
        var utter = new SpeechSynthesisUtterance(texto.trim());
        utter.lang = 'pt-BR';
        window.speechSynthesis.speak(utter);
    }

    function aoFocarElemento(e) {
        var el = e.target;
        var texto = el.getAttribute('aria-label') || el.value || el.placeholder || el.innerText;
        if (texto) falar(texto);
    }

    function aoSelecionarTexto() {
        var selecao = window.getSelection().toString();
        if (selecao) falar(selecao);
    }

    function ativarLeituraVoz() {
        document.addEventListener('focusin', aoFocarElemento);
        document.addEventListener('mouseup', aoSelecionarTexto);
    }

    function desativarLeituraVoz() {
        document.removeEventListener('focusin', aoFocarElemento);
        document.removeEventListener('mouseup', aoSelecionarTexto);
        if ('speechSynthesis' in window) window.speechSynthesis.cancel();
    }

    // ---- Aplicação das preferências no documento ----

    var leituraAtiva = false;

    function aplicarPreferencias(prefs) {
        var html = document.documentElement;
        html.classList.remove('a11y-fonte-pequena', 'a11y-fonte-grande');
        if (prefs.tamanhoFonte === 'PEQUENA') html.classList.add('a11y-fonte-pequena');
        if (prefs.tamanhoFonte === 'GRANDE') html.classList.add('a11y-fonte-grande');

        var body = document.body;
        body.classList.toggle('a11y-alto-contraste', !!prefs.altoContraste);
        body.classList.toggle('a11y-reduz-movimento', !!prefs.reduzMovimento || prefereMovimentoReduzidoNoSO());
        body.classList.toggle('a11y-sublinhar-links', !!prefs.sublinharLinks);

        if (prefs.libras) carregarVLibras(); else removerVLibras();

        if (prefs.leituraVoz && !leituraAtiva) {
            ativarLeituraVoz();
            leituraAtiva = true;
        } else if (!prefs.leituraVoz && leituraAtiva) {
            desativarLeituraVoz();
            leituraAtiva = false;
        }
    }

    // ---- Estado + sincronização com o backend ----

    var prefsAtuais = carregarPrefsLocais();
    aplicarPreferencias(prefsAtuais);

    fetch('/api/acessibilidade', { credentials: 'same-origin' })
        .then(function (res) { return res.ok ? res.json() : null; })
        .then(function (dto) {
            if (!dto) return; // não autenticado (página pública) — mantém só o localStorage
            prefsAtuais = Object.assign({}, DEFAULTS, dto);
            salvarPrefsLocais(prefsAtuais);
            aplicarPreferencias(prefsAtuais);
        })
        .catch(function () { /* backend indisponível — segue com o localStorage */ });

    function definirPreferencia(chave, valor) {
        prefsAtuais = Object.assign({}, prefsAtuais);
        prefsAtuais[chave] = valor;
        salvarPrefsLocais(prefsAtuais);
        aplicarPreferencias(prefsAtuais);

        fetch('/api/acessibilidade', {
            method: 'PUT',
            credentials: 'same-origin',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(prefsAtuais)
        }).catch(function () { /* fica salvo localmente; sincroniza na próxima carga */ });
    }

    window.PontuaA11y = {
        obter: function () { return Object.assign({}, prefsAtuais); },
        definir: definirPreferencia
    };
})();
