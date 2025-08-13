/**
 * Hiển thị một popup (toast) ở góc trên bên phải màn hình.
 * @param {string} message - Nội dung thông báo.
 * @param {string} type - Loại thông báo ('success', 'warning', hoặc 'danger').
 */
function showToast(message, type = 'success') {
    const toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        console.error('Toast container not found!');
        return;
    }

    const bgColor = type === 'success' ? 'bg-success' : (type === 'warning' ? 'bg-warning' : 'bg-danger');
    const toastId = 'toast-' + Date.now();

    const toastHTML = `
        <div id="${toastId}" class="toast align-items-center text-white ${bgColor} border-0" 
             role="alert" aria-live="assertive" aria-atomic="true"
             style="min-width: 500px; font-size: 1.2rem; padding: 1rem;">
            <div class="d-flex">
                <div class="toast-body">
            ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" 
                data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    </div>

    `;

    toastContainer.insertAdjacentHTML('beforeend', toastHTML);

    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, {
        delay: 5000 // Tự động ẩn sau 5 giây
    });
    toast.show();

    toastElement.addEventListener('hidden.bs.toast', () => {
        toastElement.remove();
    });
}