/**
 * File JavaScript dành riêng cho các tương tác trên trang list-details.html
 */

// Hàm ẩn/hiện form sửa tên list
function toggleEditForm(event) {
    if (event) {
        event.preventDefault(); // Ngăn hành vi mặc định (nếu có)
    }
    const displayDiv = document.getElementById('list-name-display');
    const formDiv = document.getElementById('edit-name-form');

    // Dùng class 'd-none' của Bootstrap để ẩn/hiện
    displayDiv.classList.toggle('d-none');
    formDiv.classList.toggle('d-none');
}

function initializeRemoveWordButtons() {
    document.querySelectorAll('.remove-word-btn').forEach(button => {
        button.addEventListener('click', (event) => {
            if (!confirm('Are you sure you want to remove this word?')) {
                return;
            }

            const btn = event.currentTarget;
            const listId = btn.dataset.listId;
            const wordId = btn.dataset.wordId;
            const csrfToken = btn.dataset.csrfToken;
            const csrfHeader = btn.dataset.csrfHeader;

            // Tạo một đối tượng FormData để gửi dữ liệu
            const formData = new FormData();
            formData.append('wordId', wordId);
            // Spring Security sẽ tự tìm CSRF token trong request body hoặc header
            // Để chắc chắn, chúng ta có thể gửi nó trong cả hai
            formData.append('_csrf', csrfToken);

            // Gửi request POST đến endpoint mới
            fetch(`/my-lists/${listId}/remove-word`, {
                method: 'POST',
                headers: {
                    // Gửi CSRF token trong header
                    [csrfHeader]: csrfToken
                },
                // Gửi wordId và token trong body
                body: new URLSearchParams(formData)
            })
                .then(response => {
                    // Kiểm tra xem request có thành công không (status 200-299)
                    if (response.ok) {
                        return response.json(); // Nếu thành công, đọc body JSON
                    } else {
                        // Nếu thất bại, ném ra một lỗi để catch xử lý
                        return response.json().then(errorData => {
                            throw new Error(errorData.error || 'Failed to remove word.');
                        });
                    }
                })
                .then(data => {
                    // Chỉ xóa hàng khỏi giao diện KHI VÀ CHỈ KHI backend xác nhận thành công
                    console.log(data.message); // In ra thông báo thành công
                    const row = btn.closest('tr');
                    row.style.transition = 'opacity 0.5s ease';
                    row.style.opacity = '0';
                    setTimeout(() => row.remove(), 500);

                    // Hiển thị toast thông báo thành công
                    if (typeof showToast === 'function') {
                        showToast('Word removed successfully!', 'success');
                    }
                })
                .catch(error => {
                    // Bắt lỗi và hiển thị cho người dùng
                    console.error('Error:', error);
                    if (typeof showToast === 'function') {
                        showToast(error.message, 'danger');
                    } else {
                        alert(error.message);
                    }
                });
        });
    });
}

// Đợi cho toàn bộ cây HTML được tải xong rồi mới chạy script
document.addEventListener('DOMContentLoaded', () => {

    // --- GẮN SỰ KIỆN CHO CÁC NÚT SỬA TÊN ---
    const editButton = document.getElementById('edit-name-btn');
    const cancelButton = document.getElementById('cancel-edit-btn');

    // Chỉ gắn sự kiện nếu nút tồn tại
    if (editButton) {
        editButton.addEventListener('click', toggleEditForm);
    }
    if (cancelButton) {
        cancelButton.addEventListener('click', toggleEditForm);
    }

    // --- KHỞI TẠO CÁC NÚT XÓA TỪ ---
    initializeRemoveWordButtons();
});