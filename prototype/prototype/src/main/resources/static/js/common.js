const API = {
    thietBi: '/api/thietbi',
    suCo:    '/api/yeucau',
    keHoach: '/api/kehoach'
};

let currentUser = null;

async function getCurrentUser() {
    if (currentUser) return currentUser;
    try {
        const res = await fetch('/api/me');
        if (res.status === 401) { window.location.href = '/login.html'; return null; }
        currentUser = await res.json();
    } catch (e) {
        window.location.href = '/login.html';
        return null;
    }
    return currentUser;
}

function hasRole(...roles) {
    return currentUser != null && roles.includes(currentUser.role);
}

async function doLogout() {
    await fetch('/logout', { method: 'POST' });
    window.location.href = '/login.html?logout';
}

async function initNav(activePage) {
    const user = await getCurrentUser();
    if (!user) return;
    const el = document.getElementById('navUser');
    if (el) el.textContent = user.hoTen;
    document.querySelectorAll('.navbar-links a[data-page]').forEach(a => {
        a.classList.toggle('active', a.dataset.page === activePage);
    });
}

async function apiGet(url) {
    const res = await fetch(url);
    if (res.status === 401) { window.location.href = '/login.html'; return null; }
    if (!res.ok) throw new Error('API error: ' + res.status);
    return res.json();
}

async function apiPost(url, body) {
    const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (res.status === 401) { window.location.href = '/login.html'; return null; }
    if (!res.ok) throw new Error('API error: ' + res.status);
    return res.json();
}

async function apiPut(url, body) {
    const res = await fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (res.status === 401) { window.location.href = '/login.html'; return null; }
    if (res.status === 403) { alert('Bạn không có quyền thực hiện thao tác này.'); return null; }
    if (!res.ok) throw new Error('API error: ' + res.status);
    return res.json();
}

async function apiDelete(url) {
    const res = await fetch(url, { method: 'DELETE' });
    if (res.status === 401) { window.location.href = '/login.html'; return; }
    if (res.status === 403) { alert('Bạn không có quyền thực hiện thao tác này.'); return; }
    if (!res.ok) throw new Error('API error: ' + res.status);
}

function badgeClass(trangThai) {
    const map = {
        'Đang hoạt động': 'badge-success',
        'Hoàn thành':     'badge-success',
        'Đã duyệt':       'badge-info',
        'Chờ duyệt':      'badge-warning',
        'Phát hiện':      'badge-warning',
        'Đang thực hiện': 'badge-info',
        'Đang sửa chữa':  'badge-info',
        'Từ chối':        'badge-danger',
        'Sự cố':          'badge-danger',
        'Bảo dưỡng':      'badge-warning'
    };
    return map[trangThai] || 'badge-neutral';
}

function badgeHtml(trangThai) {
    return `<span class="badge ${badgeClass(trangThai)}">${trangThai || ''}</span>`;
}

function formatDate(d) { return d ? d.split('T')[0] : ''; }

function getField(id) {
    const el = document.getElementById(id);
    return el ? el.value.trim() : '';
}

function getNumberField(id) {
    const v = parseInt(document.getElementById(id)?.value);
    return isNaN(v) ? null : v;
}

function clearFields(ids) {
    ids.forEach(id => { const el = document.getElementById(id); if (el) el.value = ''; });
}

function openPdf(url) { window.open(url, '_blank'); }

function showModal(id) { document.getElementById(id).style.display = 'flex'; }
function closeModal(id) { document.getElementById(id).style.display = 'none'; }
