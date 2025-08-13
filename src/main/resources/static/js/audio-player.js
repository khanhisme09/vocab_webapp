// Biến này được khởi tạo một lần và tái sử dụng
const globalAudioPlayer = new Audio();

/**
 * Gắn sự kiện click cho tất cả các nút có class 'audio-btn' trên trang.
 * Các nút này phải có thuộc tính data-audio-url hoặc data-word.
 */
function initializeAudioButtons() {
    const audioButtons = document.querySelectorAll('.audio-btn');

    audioButtons.forEach(button => {
        button.addEventListener('click', (event) => {
            const currentButton = event.currentTarget;
            const audioUrl = currentButton.dataset.audioUrl;
            const word = currentButton.dataset.word;

            if (audioUrl) {
                playAudioFromUrl(audioUrl);
            } else if (word) {
                speakText(word);
            }
        });
    });
}

function playAudioFromUrl(audioUrl) {
    globalAudioPlayer.src = audioUrl;
    globalAudioPlayer.play();
}

function speakText(text) {
    if ('speechSynthesis' in window) {
        const utterance = new SpeechSynthesisUtterance(text);
        utterance.lang = 'en-US';
        window.speechSynthesis.speak(utterance);
    } else {
        alert('Your browser does not support Text-to-Speech.');
    }
}

// Chạy hàm khởi tạo khi file script được tải và DOM đã sẵn sàng
document.addEventListener('DOMContentLoaded', initializeAudioButtons);