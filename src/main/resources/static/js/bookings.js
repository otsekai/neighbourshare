let currentTab = 'inbound';

const bookingStatus = {
    pending: ['Ожидает решения', 'bg-warning'],
    approved: ['Подтверждена', 'bg-success'],
    rejected: ['Отклонена', 'bg-danger'],
    completed: ['Завершена', 'bg-secondary']
};

function bookingDate(value) {
    return value ? new Date(value).toLocaleString('ru-RU', {dateStyle: 'medium', timeStyle: 'short'}) : '—';
}

function loadBookings(tab) {
    const container = document.getElementById('bookingsContent');
    if (!container) return;
    setLoading(container);
    const endpoint = tab === 'inbound' ? '/bookings/inbound' : '/bookings/outbound';
    request(endpoint).then(bookings => {
        if (!bookings?.length) return renderEmpty(container, `Нет ${tab === 'inbound' ? 'входящих' : 'исходящих'} заявок`, 'calendar2-x');
        container.innerHTML = `<div class="card"><div class="table-responsive"><table class="table mb-0"><thead><tr><th>Вещь</th><th>${tab === 'inbound' ? 'Арендатор' : 'Владелец'}</th><th>Период</th><th>Статус</th><th class="text-end">Действия</th></tr></thead><tbody>${bookings.map(booking => {
            const [label, badge] = bookingStatus[booking.status] || [booking.status, 'bg-secondary'];
            const person = tab === 'inbound' ? booking.borrowerName : booking.ownerName;
            const actions = tab === 'inbound' && booking.status === 'pending'
                ? `<button class="btn btn-sm btn-success action-btn" data-action="approve" data-id="${booking.bookingId}" title="Подтвердить"><i class="bi bi-check-lg"></i></button><button class="btn btn-sm btn-outline-danger action-btn" data-action="reject" data-id="${booking.bookingId}" title="Отклонить"><i class="bi bi-x-lg"></i></button>`
                : booking.status === 'approved' ? `<button class="btn btn-sm btn-outline-primary action-btn" data-action="complete" data-id="${booking.bookingId}"><i class="bi bi-check2-circle me-1"></i>Завершить</button>` : '<span class="text-muted">—</span>';
            return `<tr><td><strong>${escapeHtml(booking.itemTitle)}</strong><div class="small text-muted">#${booking.bookingId}</div></td><td>${escapeHtml(person)}</td><td><div>${bookingDate(booking.startDate)}</div><div class="small text-muted">до ${bookingDate(booking.endDate)}</div></td><td><span class="badge ${badge}">${escapeHtml(label)}</span></td><td class="text-end"><div class="d-flex justify-content-end gap-2">${actions}</div></td></tr>`;
        }).join('')}</tbody></table></div></div>`;
        container.querySelectorAll('.action-btn').forEach(button => button.addEventListener('click', async () => {
            const labels = {approve: 'подтвердить заявку', reject: 'отклонить заявку', complete: 'завершить аренду'};
            if (!window.confirm(`Вы уверены, что хотите ${labels[button.dataset.action]}?`)) return;
            button.disabled = true;
            try {
                await request(`/bookings/${button.dataset.id}/${button.dataset.action}`, 'PATCH');
                showToast('Статус заявки обновлён', 'success');
                loadBookings(currentTab);
            } catch (error) {
                button.disabled = false;
                showToast(error.message, 'error');
            }
        }));
    }).catch(error => {
        renderEmpty(container, 'Не удалось загрузить бронирования', 'cloud-slash');
        showToast(error.message, 'error');
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const tabs = document.querySelectorAll('[data-tab]');
    tabs.forEach(tab => tab.addEventListener('click', () => {
        tabs.forEach(item => item.classList.remove('active'));
        tab.classList.add('active');
        currentTab = tab.dataset.tab;
        loadBookings(currentTab);
    }));
    if (tabs.length) loadBookings(currentTab);
});
