function categoryIcon(category) {
    return ({
        Toys: 'controller',
        Instruments: 'wrench-adjustable',
        Kitchen: 'cup-hot',
        Tourism: 'backpack',
        Devices: 'phone',
        Renovation: 'hammer'
    })[category] || 'box-seam';
}

function statusLabel(status) {
    return ({available: 'Доступна', rented: 'Занята', hidden: 'Скрыта'})[status] || status;
}

function statusClass(status) {
    return status === 'available' ? 'bg-success' : status === 'rented' ? 'bg-warning' : 'bg-secondary';
}

function renderItemCard(item, withBooking = false) {
    return `<div class="col">
        <article class="card item-card">
            <div class="card-body d-flex flex-column">
                <div class="item-icon"><i class="bi bi-${categoryIcon(item.category)}"></i></div>
                <div class="d-flex justify-content-between gap-2 align-items-start">
                    <h3 class="card-title">${escapeHtml(item.title)}</h3>
                    <span class="badge ${statusClass(item.actualStatus)}">${escapeHtml(statusLabel(item.actualStatus))}</span>
                </div>
                <p class="item-meta mb-1">${escapeHtml(item.category)}</p>
                <p class="item-meta mb-4"><i class="bi bi-person me-1"></i>${escapeHtml(item.ownerName)}</p>
                ${withBooking ? `<button class="btn btn-primary mt-auto book-btn" data-item-id="${Number(item.id)}">Запросить бронь</button>` : ''}
            </div>
        </article>
    </div>`;
}

function loadAvailableItems() {
    const container = document.getElementById('itemsList');
    if (!container) return;
    setLoading(container);
    request('/items').then(items => {
        if (!items?.length) return renderEmpty(container, 'Пока нет доступных вещей', 'box-seam');
        container.innerHTML = items.map(item => renderItemCard(item, item.actualStatus === 'available')).join('');
        document.querySelectorAll('.book-btn').forEach(button => button.addEventListener('click', () => {
            document.getElementById('bookingItemId').value = button.dataset.itemId;
            document.getElementById('bookingEndDate').min = new Date().toISOString().slice(0, 16);
            bootstrap.Modal.getOrCreateInstance(document.getElementById('bookingModal')).show();
        }));
    }).catch(error => {
        renderEmpty(container, 'Не удалось загрузить вещи', 'cloud-slash');
        showToast(error.message, 'error');
    });
}

function loadMyItems() {
    const container = document.getElementById('myItemsList');
    if (!container) return;
    setLoading(container);
    request('/items/my').then(items => {
        if (!items?.length) return renderEmpty(container, 'Добавьте первую вещь, которой готовы поделиться', 'box-seam');
        container.innerHTML = items.map(item => renderItemCard(item)).join('');
    }).catch(error => {
        renderEmpty(container, 'Не удалось загрузить ваши вещи', 'cloud-slash');
        showToast(error.message, 'error');
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const addForm = document.getElementById('addItemForm');
    addForm?.addEventListener('submit', async event => {
        event.preventDefault();
        const button = addForm.querySelector('[type="submit"]');
        button.disabled = true;
        try {
            await request('/items', 'POST', {
                title: document.getElementById('itemTitle').value.trim(),
                category: document.getElementById('itemCategory').value
            });
            addForm.reset();
            bootstrap.Modal.getInstance(document.getElementById('addItemModal'))?.hide();
            showToast('Вещь добавлена в каталог', 'success');
            loadMyItems();
        } catch (error) {
            showToast(error.message, 'error');
        } finally {
            button.disabled = false;
        }
    });

    const bookingForm = document.getElementById('bookingForm');
    bookingForm?.addEventListener('submit', async event => {
        event.preventDefault();
        const button = bookingForm.querySelector('[type="submit"]');
        button.disabled = true;
        try {
            await request('/bookings', 'POST', {
                itemId: Number(document.getElementById('bookingItemId').value),
                endDate: document.getElementById('bookingEndDate').value
            });
            bookingForm.reset();
            bootstrap.Modal.getInstance(document.getElementById('bookingModal'))?.hide();
            showToast('Заявка отправлена владельцу', 'success');
            loadAvailableItems();
        } catch (error) {
            showToast(error.message, 'error');
        } finally {
            button.disabled = false;
        }
    });
});
