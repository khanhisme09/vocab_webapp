// Hàm ẩn/hiện form sửa tên
function toggleEditForm(event) {
    event.preventDefault();
    var displayDiv = document.getElementById('list-name-display');
    var formDiv = document.getElementById('edit-name-form');
    if (formDiv.style.display === 'none') {
        displayDiv.style.display = 'none';
        formDiv.style.display = 'block';
    } else {
        displayDiv.style.display = 'block';
        formDiv.style.display = 'none';
    }
}

// Gắn sự kiện cho các nút audio một cách an toàn
document.addEventListener('DOMContentLoaded', () => {
    const audioButtons = document.querySelectorAll('.audio-btn');
    const audioPlayer = new Audio();

    audioButtons.forEach(button => {
        button.addEventListener('click', (event) => {
            const currentButton = event.currentTarget;
            const audioUrl = currentButton.dataset.audioUrl;
            const word = currentButton.dataset.word;

            if (audioUrl) {
                audioPlayer.src = audioUrl;
                audioPlayer.play();
            } else if (word) {
                if ('speechSynthesis' in window) {
                    const utterance = new SpeechSynthesisUtterance(word);
                    utterance.lang = 'en-US';
                    window.speechSynthesis.speak(utterance);
                } else {
                    alert('Your browser does not support Text-to-Speech.');
                }
            }
        });
    });
});