// Registro JavaScript

// Máscara para CPF
document.getElementById('cpf').addEventListener('input', function(e) {
    let value = e.target.value.replace(/\D/g, '');

    if (value.length <= 11) {
        value = value.replace(/(\d{3})(\d)/, '$1.$2');
        value = value.replace(/(\d{3})(\d)/, '$1.$2');
        value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    }

    e.target.value = value;
});

// substitui a URL no histórico sem recarregar a página
if (location.pathname.endsWith('.html')) {
  history.replaceState(null, '', '/Cadastro');
}

// Submeter formulário
document.getElementById('registroForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const senha = document.getElementById('senha').value;
    const confirmarSenha = document.getElementById('confirmarSenha').value;

    // Validar senhas
    if (senha !== confirmarSenha) {
        showAlert('As senhas não coincidem!', 'error');
        return;
    }

    // Coletar dados do formulário
    const dados = {
        nome: document.getElementById('nome').value,
        email: document.getElementById('email').value,
        senha: senha,
        matricula: document.getElementById('matricula').value,
        cpf: document.getElementById('cpf').value.replace(/\D/g, ''), // Remove formatação
        serie: document.getElementById('serie').value,
        colegio: document.getElementById('colegio').value,
        turma: document.getElementById('turma').value || null,
        dataNascimento: document.getElementById('dataNascimento').value || null
    };

    try {
        const response = await fetch('/api/registro', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dados)
        });

        const result = await response.json();

        if (response.ok) {
            showAlert(result.success + ' ' + result.message, 'success');

            // Redirecionar para login após 2 segundos
            setTimeout(() => {
                window.location.href = '/login.html';
            }, 2000);
        } else {
            showAlert(result.error || 'Erro ao realizar cadastro', 'error');
        }
    } catch (error) {
        console.error('Erro:', error);
        showAlert('Erro ao realizar cadastro. Tente novamente.', 'error');
    }
});

function showAlert(message, type) {
    const alert = document.getElementById('alert');
    alert.textContent = message;
    alert.className = `alert alert-${type}`;
    alert.style.display = 'block';

    // Scroll para o topo
    window.scrollTo({ top: 0, behavior: 'smooth' });
}
