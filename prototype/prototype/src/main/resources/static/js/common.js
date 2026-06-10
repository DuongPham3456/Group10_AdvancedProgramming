const API = {
    thietBi: '/api/thietbi',
    yeuCau: '/api/yeucau',
    keHoach: '/api/kehoach',
    congViec: '/api/congviec',
    chiPhi: '/api/chiphi',
    thongSo: '/api/thongso'
};

async function apiGet(url) {
    const res = await fetch(url);
    if (!res.ok) throw new Error('API error: ' + res.status);
    return res.json();
}

async function apiPost(url, body) {
    const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (!res.ok) throw new Error('API error: ' + res.status);
    return res.json();
}

async function apiPut(url, body) {
    const res = await fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (!res.ok) throw new Error('API error: ' + res.status);
    return res.json();
}

async function apiDelete(url) {
    const res = await fetch(url, { method: 'DELETE' });
    if (!res.ok) throw new Error('API error: ' + res.status);
}

function badgeClass(trangThai) {
    const map = {
        'Đang hoạt động': 'badge-success',
        'Hoàn thành': 'badge-success',
        'Đã duyệt': 'badge-success',
        'Chờ duyệt': 'badge-warning',
        'Đang xử lý': 'badge-info',
        'Bảo dưỡng': 'badge-warning',
        'Sự cố': 'badge-danger',
        'Từ chối': 'badge-danger',
        'Quá hạn': 'badge-danger',
        'Mới': 'badge-info'
    };
    return map[trangThai] || 'badge-neutral';
}

function badgeHtml(trangThai) {
    return `<span class="badge ${badgeClass(trangThai)}">${trangThai || ''}</span>`;
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    return dateStr.split('T')[0];
}

function formatCurrency(amount) {
    if (amount == null) return '';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

function setActiveNav(page) {
    document.querySelectorAll('.navbar-links a').forEach(a => {
        a.classList.toggle('active', a.dataset.page === page);
    });
}

function getField(id) {
    const el = document.getElementById(id);
    return el ? el.value.trim() : '';
}

function getNumberField(id) {
    const val = parseInt(document.getElementById(id).value);
    return isNaN(val) ? null : val;
}

function getFloatField(id) {
    const val = parseFloat(document.getElementById(id).value);
    return isNaN(val) ? null : val;
}

function clearFields(ids) {
    ids.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
}
