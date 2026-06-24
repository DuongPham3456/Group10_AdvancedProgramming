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

function hasAnyRole(...roles) {
    return currentUser != null && roles.some(role => currentUser.role === role);
}

function isPageAllowed(page) {
    if (!currentUser) return false;
    const rule = {
        'thiet-bi': ['QUAN_LY_TRAM', 'CONG_NHAN'],
        'ke-hoach': ['BP_QLTB', 'GIAM_DOC'],
        'su-co':    ['BP_QLTB', 'CONG_NHAN', 'GIAM_DOC']
    };
    return !page || !rule[page] || rule[page].includes(currentUser.role);
}

async function doLogout() {
    await fetch('/logout', { method: 'POST' });
    window.location.href = '/login.html?logout';
}

async function initNav(activePage) {
    const user = await getCurrentUser();
    if (!user) return;
    const el = document.getElementById('navUser');
    const roleNames = {
        'QUAN_LY_TRAM': 'Admin Trạm',
        'BP_QLTB': 'BP QLTB',
        'CONG_NHAN': 'Công nhân',
        'GIAM_DOC': 'Giám đốc'
    };
    if (el) el.textContent = `${user.hoTen} (${roleNames[user.role] || user.role})`;

    document.querySelectorAll('.navbar-links a[data-page]').forEach(a => {
        const allowed = isPageAllowed(a.dataset.page);
        a.style.display = allowed ? 'inline-flex' : 'none';
        a.classList.toggle('active', a.dataset.page === activePage);
    });

    document.querySelectorAll('.feature-card').forEach(card => {
        const roles = card.dataset.roles;
        if (!roles) return;
        const allowed = roles.split(',').map(r => r.trim()).includes(user.role);
        card.style.display = allowed ? 'flex' : 'none';
    });
}

async function apiGet(url) {
    const res = await fetch(url, { credentials: 'same-origin', redirect: 'manual' });
    if (res.status === 401) { window.location.href = '/login.html'; return null; }
    if (!res.ok) throw new Error('API error: ' + res.status);
    return res.json();
}

async function apiPost(url, body) {
    const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
        credentials: 'same-origin',
        redirect: 'manual'
    });
    if (res.status === 401) { window.location.href = '/login.html'; return null; }
    if (!res.ok) {
        const txt = await res.text().catch(() => '');
        throw new Error('API error: ' + res.status + ' - ' + txt);
    }
    return res.json();
}

async function apiPut(url, body) {
    const res = await fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
        credentials: 'same-origin',
        redirect: 'manual'
    });
    if (res.status === 401) { window.location.href = '/login.html'; return null; }
    if (res.status === 403) { alert('Bạn không có quyền thực hiện thao tác này.'); return null; }
    if (!res.ok) {
        const txt = await res.text().catch(() => '');
        throw new Error('API error: ' + res.status + ' - ' + txt);
    }
    return res.json();
}

async function apiDelete(url) {
    const res = await fetch(url, { method: 'DELETE', credentials: 'same-origin', redirect: 'manual' });
    if (res.status === 401) { window.location.href = '/login.html'; return null; }
    if (res.status === 403) { alert('Bạn không có quyền thực hiện thao tác này.'); return null; }
    if (!res.ok) {
        const txt = await res.text().catch(() => '');
        throw new Error('API error: ' + res.status + ' - ' + txt);
    }
    return true;
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

function openSoTheoDoi() { openPdf('/api/pdf/so-theo-doi'); }

function showModal(id) { document.getElementById(id).style.display = 'flex'; }
function closeModal(id) { document.getElementById(id).style.display = 'none'; }

// Global delete helpers — call server endpoints added in controllers
async function deleteDeviceByMa(ma) {
    if (!ma) { alert('Không có mã thiết bị.'); return; }
    if (!confirm('Xóa thiết bị có mã ' + ma + ' ?')) return;
    await apiDelete(`${API.thietBi}/byMa/${encodeURIComponent(ma)}`);
}

async function deleteKeHoachByMa(ma) {
    if (!ma) { alert('Không có mã thiết bị.'); return; }
    if (!confirm('Xóa tất cả kế hoạch liên quan tới ' + ma + ' ?')) return;
    await apiDelete(`${API.keHoach}/byMa/${encodeURIComponent(ma)}`);
}

async function deleteYeuCauByMa(ma) {
    if (!ma) { alert('Không có mã thiết bị.'); return; }
    if (!confirm('Xóa tất cả báo cáo sự cố liên quan tới ' + ma + ' ?')) return;
    await apiDelete(`${API.suCo}/byMa/${encodeURIComponent(ma)}`);
}
