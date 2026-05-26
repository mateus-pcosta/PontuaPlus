// Perfil TypeScript/JavaScript


// substitui a URL no histórico sem recarregar a página
if (location.pathname.endsWith('.html')) {
  history.replaceState(null, '', '/perfil');
}


// Carregar dados do perfil
async function carregarPerfil() {
    try {
        const response = await fetch('/api/colaborador/me');

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                window.location.href = '/login.html';
                return;
            }
            throw new Error('Erro ao carregar perfil');
        }

        const data = await response.json();
        renderizarPerfil(data);
    } catch (error) {
        alert('Erro ao carregar dados do perfil');
    }
}


// Renderizar dados do perfil
function renderizarPerfil(colaborador) {
    // Ocultar loading e mostrar conteúdo
    document.getElementById('loading').style.display = 'none';
    document.getElementById('perfilContent').style.display = 'block';

    // Atualizar nome do usuário no header
    document.getElementById('userName').textContent = colaborador.nome;
    const iniciais = colaborador.nome.split(' ').map(n => n[0]).slice(0, 2).join('');
    document.getElementById('userAvatar').textContent = iniciais;

    // Informações Pessoais
    document.getElementById('nomeCompleto').textContent = colaborador.nome;
    document.getElementById('email').textContent = colaborador.email;
    document.getElementById('dataNascimento').textContent = formatarData(colaborador.dataNascimento);

    // Informações Acadêmicas
    document.getElementById('matricula').textContent = colaborador.matricula;

    document.getElementById('colegio').textContent = colaborador.colegio;

    document.getElementById('dataIngresso').textContent = formatarData(colaborador.dataIngresso);
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

function showCustomConfirm(callback) {
    const modal = document.getElementById("customModal");
    const btnConfirm = document.getElementById("modalConfirm");
    const btnCancel = document.getElementById("modalCancel");

    modal.style.display = "flex";

    btnConfirm.onclick = () => {
        modal.style.display = "none";
        callback(true);
    };

    btnCancel.onclick = () => {
        modal.style.display = "none";
        callback(false);
    };
}



// Função de logout
// Função de logout usando modal customizado
function logout() {
    showCustomConfirm((confirmado) => {
        if (confirmado) {
            window.location.href = '/api/auth/logout';
        }
    });
}

// Carregar perfil ao carregar a página
window.addEventListener('DOMContentLoaded', carregarPerfil);