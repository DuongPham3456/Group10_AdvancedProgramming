# Design: Maintenance Lifecycle Website — BT.01

**Date:** 2026-06-17
**Source document:** QUY TRÌNH QUẢN LÝ MÁY MÓC, THIẾT BỊ (Mã số: BT.01) — Tín Thành Environment Technology Co., Ltd

---

## Context

The current codebase has 8 pages and 6 entities but many do not correspond to any step in the official business process document BT.01. The goal is to strip the app down to exactly what the process requires and fill in the gaps (lifecycle transitions) that are missing.

BT.01 defines two processes:
- **5.1** Quy trình bảo dưỡng, sửa chữa thiết bị định kỳ (Periodic Maintenance)
- **5.2** Quy trình khắc phục sự cố thiết bị (Incident Response)

---

## Removals

The following entities, controllers, repositories, and pages have no counterpart in BT.01 and must be deleted:

| Entity | Controller | Page |
|--------|-----------|------|
| `ThongSoMay` | `ThongSoController` | `giam-sat.html`, `lich-su.html` |
| `ChiPhi` | `ChiPhiController` | `chi-phi.html` |
| `CongViec` | `CongViecController` | `cong-viec.html` |
| — | — | `bao-cao.html` |

Also remove `ThongSoRepository`, `ChiPhiRepository`, `CongViecRepository` and their sample data from `DataInitializer`.

---

## Kept & Modified Features

### Equipment Management (BM.BT.01.01 + BM.BT.01.02)

**Entity:** `ThietBiLyLich` — no changes required.
**Page:** `thiet-bi.html` — keep as-is. Already supports full CRUD and PDF export for both BM.BT.01.01 and BM.BT.01.02.

---

## Process 5.1 — Periodic Maintenance

**Page:** `ke-hoach.html` | **Entity:** `KeHoachBaoTri` | **Form:** BM.BT.01.03

### State machine

```
[Chờ duyệt] ──approve──► [Đã duyệt] ──start──► [Đang thực hiện] ──done──► [Hoàn thành]
      │
      └──reject──► [Từ chối]  ← terminal: BP.QLTB creates a NEW plan separately
```

- Default on create: `Chờ duyệt`
- `Từ chối` is a **terminal state** — the rejected plan is closed and read-only. BP.QLTB uses the normal create form to submit a replacement plan.
- Only plans in `Chờ duyệt` can be deleted (before any director action).
- `Hoàn thành` records are read-only.

### New fields on `KeHoachBaoTri`

| Field | Type | Phase | Notes |
|-------|------|-------|-------|
| `lyDoTuChoi` | String (TEXT) | Rejection | Reason from director |
| `donViThueNgoai` | String | Execution | External contractor name (optional) |
| `nguoiThucHien` | String | Execution | BP.QLTB executor name |
| `ngayThucHienThucTe` | LocalDate | Execution | Actual execution date |
| `ketQuaBaoDuong` | String (TEXT) | Execution | Result notes → feeds BM.BT.01.05 |

### New API endpoints

| Method | Path | Body | Effect |
|--------|------|------|--------|
| `PUT` | `/api/kehoach/{id}/duyet` | — | `Chờ duyệt` → `Đã duyệt` (exists) |
| `PUT` | `/api/kehoach/{id}/tuchoi` | `{lyDoTuChoi}` | `Chờ duyệt` → `Từ chối` |
| `PUT` | `/api/kehoach/{id}/thuchien` | `{donViThueNgoai, nguoiThucHien, ngayThucHienThucTe}` | `Đã duyệt` → `Đang thực hiện` |
| `PUT` | `/api/kehoach/{id}/hoanthanh` | `{ketQuaBaoDuong}` | `Đang thực hiện` → `Hoàn thành` |

### UI — `ke-hoach.html`

Table action buttons are conditional on status:

| Status | Buttons shown |
|--------|--------------|
| `Chờ duyệt` | Duyệt `(GIAM_DOC)`, Từ chối `(GIAM_DOC, modal)`, Xóa `(BP_QLTB)` |
| `Từ chối` | — (read-only, shows rejection reason) |
| `Đã duyệt` | Bắt đầu thực hiện `(BP_QLTB, modal)` |
| `Đang thực hiện` | Hoàn thành `(BP_QLTB/CONG_NHAN/QUAN_LY_VUNG, modal)` |
| `Hoàn thành` | — |

Modals needed: **Từ chối** (reason textarea), **Thực hiện** (contractor + executor + date), **Hoàn thành** (result notes).

PDF export: existing `/api/pdf/kehoach` for BM.BT.01.03.

---

## Process 5.2 — Incident Response

**Page:** `su-co.html` (rename from `yeu-cau.html`) | **Entity:** `YeuCauBaoTri` | **Form:** BM.BT.01.04

### State machine

```
[Phát hiện] ──submit plan──► [Chờ duyệt] ──approve──► [Đang sửa chữa] ──done──► [Hoàn thành]
                                    │
                                    └──reject──► [Từ chối]  ← terminal: BP.QLTB creates a NEW repair record
```

- Default on create (incident report): `Phát hiện`
- `Từ chối` is a **terminal state** — the rejected record is closed. BP.QLTB files a new incident/repair record to retry.
- Responsible: Công nhân vận hành reports, BP.QLTB drafts repair plan, Giám đốc approves.

### New fields on `YeuCauBaoTri`

| Field | Type | Phase | Notes |
|-------|------|-------|-------|
| `phuongAnSuaChua` | String (TEXT) | Plan | Repair plan drafted by BP.QLTB |
| `lyDoTuChoi` | String (TEXT) | Rejection | Director's rejection reason |
| `donViThueNgoai` | String | Repair | External contractor (if needed) |
| `ngaySuaChua` | LocalDate | Repair | Actual repair date |
| `ketQuaSuaChua` | String (TEXT) | Acceptance | Result notes → BM.BT.01.04 |
| `nguoiNghiemThu` | String | Acceptance | Quản lý Trạm who signs off |

### New/changed API endpoints

| Method | Path | Body | Effect |
|--------|------|------|--------|
| `PUT` | `/api/yeucau/{id}/lapkehoach` | `{phuongAnSuaChua}` | `Phát hiện` → `Chờ duyệt` |
| `PUT` | `/api/yeucau/{id}/duyet` | — | `Chờ duyệt` → `Đang sửa chữa` (update existing) |
| `PUT` | `/api/yeucau/{id}/tuchoi` | `{lyDoTuChoi}` | `Chờ duyệt` → `Từ chối` (update existing) |
| `PUT` | `/api/yeucau/{id}/suachua` | `{donViThueNgoai, ngaySuaChua}` | `Đang sửa chữa` → stay, add repair details |
| `PUT` | `/api/yeucau/{id}/nghiemthu` | `{ketQuaSuaChua, nguoiNghiemThu}` | `Đang sửa chữa` → `Hoàn thành` |

### UI — `su-co.html`

Table action buttons conditional on status:

| Status | Buttons shown |
|--------|--------------|
| `Phát hiện` | Lập phương án `(BP_QLTB/QUAN_LY_TRAM, modal)`, Xóa `(CONG_NHAN)` |
| `Chờ duyệt` | Duyệt `(GIAM_DOC)`, Từ chối `(GIAM_DOC, modal)` |
| `Từ chối` | — (read-only, shows rejection reason) |
| `Đang sửa chữa` | Nghiệm thu `(CONG_NHAN/QUAN_LY_TRAM, modal)` |
| `Hoàn thành` | — |

---

## Authentication & Role-Based Authorization

### Stack
Spring Security with form-based login. Passwords stored BCrypt-hashed. Session-based (no JWT).

### Roles

| Role constant | Vietnamese label | Who they are |
|--------------|-----------------|--------------|
| `QUAN_LY_TRAM` | Quản lý Trạm | Station manager |
| `BP_QLTB` | BP. QLTB | Equipment management dept |
| `GIAM_DOC` | Giám đốc/Trưởng phòng | Director / dept head |
| `CONG_NHAN` | Công nhân vận hành | Operating worker |
| `QUAN_LY_VUNG` | Quản lý Vùng | Regional manager |

### New entities & files

- **`User`** entity: `id` (UUID), `username` (unique), `password` (BCrypt), `hoTen` (display name), `role` (String)
- **`UserRepository`** extends JpaRepository
- **`UserDetailsServiceImpl`** implements Spring Security `UserDetailsService`
- **`SecurityConfig`** — HTTP security rules (see below)
- **`login.html`** — simple username/password form

### API authorization rules

| Endpoint | Allowed roles |
|----------|--------------|
| `POST /api/thietbi`, `PUT /api/thietbi/{id}`, `DELETE /api/thietbi/{id}` | `QUAN_LY_TRAM` |
| `GET /api/thietbi/**` | all authenticated |
| `POST /api/kehoach` | `BP_QLTB` |
| `PUT /api/kehoach/{id}` (edit) | `BP_QLTB` |
| `PUT /api/kehoach/{id}/duyet` | `GIAM_DOC` |
| `PUT /api/kehoach/{id}/tuchoi` | `GIAM_DOC` |
| `PUT /api/kehoach/{id}/thuchien` | `BP_QLTB` |
| `PUT /api/kehoach/{id}/hoanthanh` | `BP_QLTB`, `CONG_NHAN`, `QUAN_LY_VUNG` |
| `DELETE /api/kehoach/{id}` | `BP_QLTB` |
| `POST /api/yeucau` | `CONG_NHAN` |
| `PUT /api/yeucau/{id}/lapkehoach` | `BP_QLTB`, `QUAN_LY_TRAM` |
| `PUT /api/yeucau/{id}/duyet` | `GIAM_DOC` |
| `PUT /api/yeucau/{id}/tuchoi` | `GIAM_DOC` |
| `PUT /api/yeucau/{id}/nghiemthu` | `CONG_NHAN`, `QUAN_LY_TRAM` |
| `GET /api/yeucau/**` | all authenticated |
| `GET /api/me` | all authenticated — returns current user info |

### Frontend behavior

- On page load, call `GET /api/me` to get `{username, hoTen, role}`.
- Show/hide action buttons based on role. Example: the **Duyệt** and **Từ chối** buttons only render if `role === 'GIAM_DOC'`.
- Navbar shows logged-in user name and a **Đăng xuất** link (`POST /logout`).
- If unauthenticated, Spring Security redirects to `/login`.

### Sample users (seeded by DataInitializer)

| Username | Password | Role |
|----------|----------|------|
| `admin.tram` | `123456` | `QUAN_LY_TRAM` |
| `bp.qltb` | `123456` | `BP_QLTB` |
| `giamdoc` | `123456` | `GIAM_DOC` |
| `congnhan` | `123456` | `CONG_NHAN` |
| `ql.vung` | `123456` | `QUAN_LY_VUNG` |

---

## Navigation

Three-item navbar replacing the current five-item one:

```
[Thiết bị]   [Bảo dưỡng định kỳ]   [Khắc phục sự cố]
```

Landing page (`index.html`) updated to show only these three cards.

---

## Status Badge Colors

| Status | Color |
|--------|-------|
| `Chờ duyệt` / `Phát hiện` | warning (yellow) |
| `Đã duyệt` | info (blue) |
| `Từ chối` | danger (red) |
| `Đang thực hiện` / `Đang sửa chữa` | primary (purple) |
| `Hoàn thành` | success (green) |

---

## Verification

1. Start app: `mvn spring-boot:run` in `prototype/prototype/`
2. Navigate to `http://localhost:8080` — should redirect to `/login`.
3. **Auth flow:**
   - Login as `giamdoc / 123456` — verify navbar shows name and Đăng xuất.
   - Go to `/pages/ke-hoach.html` — verify **Duyệt/Từ chối** buttons are visible.
   - Logout. Login as `bp.qltb / 123456` — verify **Duyệt/Từ chối** buttons are hidden.
4. **Equipment (as `admin.tram`):**
   - Create, edit, delete equipment — all work.
   - Login as `congnhan` — verify create/edit/delete buttons are hidden.
5. **Periodic maintenance lifecycle (switch users per step):**
   - As `bp.qltb`: Create a plan → status `Chờ duyệt`
   - As `giamdoc`: Click **Từ chối** → enter reason → status `Từ chối` (record is now read-only, reason shown)
   - As `bp.qltb`: Create a **new** plan → status `Chờ duyệt`
   - As `giamdoc`: Click **Duyệt** → status `Đã duyệt`
   - As `bp.qltb`: Click **Bắt đầu thực hiện** → fill modal → status `Đang thực hiện`
   - As `congnhan`: Click **Hoàn thành** → status `Hoàn thành`
6. **Incident lifecycle (switch users per step):**
   - As `congnhan`: Report incident → status `Phát hiện`
   - As `bp.qltb`: Click **Lập phương án** → status `Chờ duyệt`
   - As `giamdoc`: Click **Duyệt** → status `Đang sửa chữa`
   - As `congnhan`: Click **Nghiệm thu** → status `Hoàn thành`
7. Confirm removed pages (`/pages/giam-sat.html`, `/pages/chi-phi.html`, etc.) return 404.
