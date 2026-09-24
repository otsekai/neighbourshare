function initTheme() {
    const saved = localStorage.getItem('theme');
    const theme = saved === 'dark' ? 'dark' : 'light';
    document.documentElement.dataset.theme = theme;
    document.querySelectorAll('.theme-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.theme === theme);
    });
}

function toggleTheme(e) {
    const theme = e.currentTarget.dataset.theme;
    document.documentElement.dataset.theme = theme;
    localStorage.setItem('theme', theme);
    document.querySelectorAll('.theme-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.theme === theme);
    });
}

function initThemeSwitcher() {
    const btns = document.querySelectorAll('.theme-btn');
    if (btns.length) {
        btns.forEach(btn => btn.addEventListener('click', toggleTheme));
    }
}

function initNavigation() {
    const path = window.location.pathname;
    const publicPages = ['/login.html', '/register.html'];
    const token = localStorage.getItem('token');
    if (!token && !publicPages.includes(path)) {
        window.location.href = '/login.html';
        return false;
    }
    if (token && publicPages.includes(path)) {
        window.location.href = '/';
        return false;
    }
    document.querySelectorAll('.nav-link[href]').forEach(link => link.classList.toggle('active', link.getAttribute('href') === path || (path === '/' && link.getAttribute('href') === '/')));
    if (path === '/' || path === '/index.html') loadAvailableItems();
    if (path === '/my-items.html') loadMyItems();

    const adminLink = document.getElementById('adminNavLink');
    if (adminLink && localStorage.getItem('role') === 'ADMIN') {
        adminLink.classList.remove('d-none');
    }
    if (path === '/admin.html' && localStorage.getItem('role') !== 'ADMIN') {
        window.location.href = '/';
        return false;
    }
    return true;
}

document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    initThemeSwitcher();
    if (!initNavigation()) return;
});