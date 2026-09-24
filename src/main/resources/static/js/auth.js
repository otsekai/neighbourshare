document.addEventListener('DOMContentLoaded', () => {
    const submitForm = async (form, endpoint, payload, redirect) => {
        const button = form.querySelector('[type="submit"]');
        const error = document.getElementById('error');
        button.disabled = true;
        error?.classList.add('d-none');
        try {
            const data = await request(endpoint, 'POST', payload);
            if (data.token) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('role', data.role || 'USER');
            }
            window.location.href = redirect;
        } catch (err) {
            if (error) {
                error.textContent = err.message || 'Что-то пошло не так';
                error.classList.remove('d-none');
            }
        } finally {
            button.disabled = false;
        }
    };

    document.getElementById('loginForm')?.addEventListener('submit', event => {
        event.preventDefault();
        submitForm(event.currentTarget, '/auth/login', {
            email: document.getElementById('email').value.trim(),
            password: document.getElementById('password').value
        }, '/');
    });
    document.getElementById('registerForm')?.addEventListener('submit', event => {
        event.preventDefault();
        submitForm(event.currentTarget, '/auth/register', {
            name: document.getElementById('name').value.trim(),
            email: document.getElementById('email').value.trim(),
            password: document.getElementById('password').value
        }, '/login.html');
    });
    document.getElementById('logoutBtn')?.addEventListener('click', () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        window.location.href = '/login.html';
    });
});
