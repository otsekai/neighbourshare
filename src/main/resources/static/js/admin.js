function loadUsers() {
    const tbody = document.querySelector('#usersTable tbody');
    if (!tbody) return;
    tbody.innerHTML = '<tr><td colspan="6" class="text-center py-5"><span class="spinner-border spinner-border-sm"></span></td></tr>';
    request('/admin/users').then(users => {
        if (!users?.length) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-5">Пользователей пока нет</td></tr>';
            return;
        }
        tbody.innerHTML = users.map(user => `<tr><td class="text-muted">#${user.userId}</td><td><strong>${escapeHtml(user.name)}</strong></td><td>${escapeHtml(user.email)}</td><td><span class="badge bg-secondary">${escapeHtml(user.role)}</span></td><td><span class="badge ${user.banned ? 'bg-danger' : 'bg-success'}">${user.banned ? 'Заблокирован' : 'Активен'}</span></td><td><button class="btn btn-sm btn-outline-secondary toggle-ban" data-id="${user.userId}">${user.banned ? '<i class="bi bi-unlock me-1"></i>Разблокировать' : '<i class="bi bi-slash-circle me-1"></i>Заблокировать'}</button></td></tr>`).join('');
        tbody.querySelectorAll('.toggle-ban').forEach(button => button.addEventListener('click', async () => {
            if (!window.confirm('Изменить статус блокировки пользователя?')) return;
            button.disabled = true;
            try {
                await request(`/admin/users/${button.dataset.id}/ban`, 'PATCH');
                showToast('Статус пользователя обновлён', 'success');
                loadUsers();
            } catch (error) {
                button.disabled = false;
                showToast(error.message, 'error');
            }
        }));
    }).catch(() => {
        showToast('Недостаточно прав для просмотра панели', 'error');
        setTimeout(() => window.location.href = '/', 900);
    });
}

document.addEventListener('DOMContentLoaded', loadUsers);
