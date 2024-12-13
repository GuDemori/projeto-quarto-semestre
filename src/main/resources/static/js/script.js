function validateRegisterForm() {
    const fullName = document.getElementById('fullName').value.trim();
    const username = document.getElementById('regUsername').value.trim();
    const password = document.getElementById('regPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (fullName === '') {
        showPopup('error', 'O nome completo não pode ser vazio.');
        return false;
    }
    if (fullName.length > 255) {
        showPopup('error', 'O nome completo não pode ter mais de 255 caracteres.');
        return false;
    }
    if (username === '') {
        showPopup('error', 'O username não pode ser vazio.');
        return false;
    }
    if (username.length > 255) {
        showPopup('error', 'O username não pode ter mais de 255 caracteres.');
        return false;
    }
    if (password.length < 8) {
        showPopup('error', 'A senha deve ter pelo menos 8 caracteres.');
        return false;
    }
    if (password !== confirmPassword) {
        showPopup('error', 'As senhas não conferem.');
        return false;
    }
    return true; // Se passou nas validações do front, envia o form
}

function validateLoginForm() {
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;
    if (username === '') {
        showPopup('error', 'Informe o username.');
        return false;
    }
    if (username.length > 255) {
        showPopup('error', 'O username não pode ter mais de 255 caracteres.');
        return false;
    }
    if (password === '') {
        showPopup('error', 'Informe a senha.');
        return false;
    }
    return true;
}

function showPopup(type, message) {
    const popup = document.createElement('div');
    popup.classList.add('popup');
    if (type === 'error') {
        popup.classList.add('popup-error');
    } else if (type === 'success') {
        popup.classList.add('popup-success');
    }
    popup.textContent = message;
    document.body.appendChild(popup);

    setTimeout(() => {
        popup.remove();
    }, 5000);
}

window.addEventListener('load', () => {
    const existingPopups = document.querySelectorAll('.popup');
    existingPopups.forEach(p => {
        setTimeout(() => {
            p.remove();
        }, 5000);
    });
});
