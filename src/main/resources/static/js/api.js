const API_BASE = '/api';

async function request(endpoint, method = 'GET', body = null) {
    const token = localStorage.getItem('token');
    const headers = {'Content-Type': 'application/json'};
    if (token) headers.Authorization = `Bearer ${token}`;

    const options = {method, headers};
    if (body !== null) options.body = JSON.stringify(body);

    const response = await fetch(`${API_BASE}${endpoint}`, options);
    const contentType = response.headers.get('content-type') || '';
    const data = contentType.includes('application/json') ? await response.json() : await response.text();

    if (response.status === 401) {
        localStorage.removeItem('token');
        if (!['/login.html', '/register.html'].includes(window.location.pathname)) {
            window.location.href = '/login.html';
        }
        throw new Error('Сессия истекла. Войдите снова.');
    }

    if (!response.ok) {
        throw new Error(typeof data === 'object' ? data.message : data || `HTTP ${response.status}`);
    }
    return data;
}

function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer') || (() => {
        const element = document.createElement('div');
        element.id = 'toastContainer';
        element.className = 'toast-container position-fixed bottom-0 end-0 p-3';
        document.body.appendChild(element);
        return element;
    })();
    const toast = document.createElement('div');
    toast.className = 'toast show';
    toast.setAttribute('role', 'status');
    toast.innerHTML = `<div class="d-flex"><div class="toast-body"><i class="bi bi-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'} me-2"></i>${escapeHtml(message)}</div><button type="button" class="btn-close me-2 m-auto" aria-label="Закрыть"></button></div>`;
    toast.querySelector('button').addEventListener('click', () => toast.remove());
    container.appendChild(toast);
    window.setTimeout(() => toast.remove(), 4500);
}

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>'"]/g, character => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        "'": '&#39;',
        '"': '&quot;'
    }[character]));
}

function setLoading(container, message = 'Загружаем данные…') {
    container.innerHTML = `<div class="w-100"><div class="loading"><div class="spinner-border spinner-border-sm mb-2" role="status"></div><div>${message}</div></div></div>`;
}

function renderEmpty(container, message, icon = 'inbox') {
    container.innerHTML = `<div class="w-100"><div class="empty-state"><div class="empty-icon"><i class="bi bi-${icon}"></i></div><div>${message}</div></div></div>`;
}
