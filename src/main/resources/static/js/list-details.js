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

// Hàm xử lý việc xóa từ bằng AJAX
function initializeRemoveWordButtons() {
    document.querySelectorAll('.remove-word-btn').forEach(button => {
        button.addEventListener('click', (event) => {
            if (!confirm('Are you sure you want to remove this word?')) {
                return;
            }

            const btn = event.currentTarget;
            const listId = btn.dataset.listId;
            const wordId = btn.dataset.wordId;
            const token = btn.dataset.csrfToken;
            const header = btn.dataset.csrfHeader;

            fetch(`/my-lists/${listId}/remove-word/${wordId}`, {
                method: 'DELETE',
                headers: {
                    [header]: token
                }
            })
                .then(response => {
                    if (response.ok) {
                        const row = btn.closest('tr');
                        row.style.transition = 'opacity 0.5s ease';
                        row.style.opacity = '0';
                        setTimeout(() => row.remove(), 500);
                        // Cần cập nhật lại số lượng từ trên trang
                        // (Đây là một cải tiến nâng cao có thể làm sau)
                    } else {
                        alert('Failed to remove word. Please try again.');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('An error occurred while removing the word.');
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