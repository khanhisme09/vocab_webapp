// Khai báo biến toàn cục
let currentCardIndex = 0;
let flashcards = [];
const audioPlayer = new Audio();

// Khởi tạo khi DOM sẵn sàng
document.addEventListener('DOMContentLoaded', () => {
    // Lấy dữ liệu từ server (sẽ được truyền qua biến global)
    flashcards = window.flashcards || [];

    // Lấy các phần tử DOM
    const flashcardEl = document.getElementById('flashcard');
    const cardFrontText = document.getElementById('card-front-text');
    const cardFrontPhonetic = document.getElementById('card-front-phonetic');
    const audioBtnFront = document.getElementById('audio-btn-front');
    const cardBackText = document.getElementById('card-back-text');
    const progressText = document.getElementById('progress-text');
    const prevBtn = document.getElementById('prev-btn');
    const nextBtn = document.getElementById('next-btn');

    // Gán sự kiện
    flashcardEl.addEventListener('click', handleCardFlip);
    audioBtnFront.addEventListener('click', handleAudioPlay);
    prevBtn.addEventListener('click', showPrevCard);
    nextBtn.addEventListener('click', showNextCard);

    // Hiển thị thẻ đầu tiên
    showCard(currentCardIndex);

    // Các hàm xử lý
    function handleCardFlip(event) {
        if (!event.target.closest('.phonetic-block') && flashcards.length > 0) {
            flashcardEl.classList.toggle('is-flipped');
        }
    }

    function handleAudioPlay(event) {
        event.stopPropagation();
        const btn = event.currentTarget;
        const audioUrl = btn.dataset.audioUrl;
        const word = btn.dataset.word;

        if (audioUrl) {
            audioPlayer.src = audioUrl;
            audioPlayer.play();
        } else if (word) {
            if ('speechSynthesis' in window) {
                const utterance = new SpeechSynthesisUtterance(word);
                utterance.lang = 'en-US';
                window.speechSynthesis.speak(utterance);
            } else {
                alert('Trình duyệt không hỗ trợ đọc văn bản');
            }
        }
    }

    function showPrevCard() {
        if (flashcards.length > 0) {
            currentCardIndex = (currentCardIndex - 1 + flashcards.length) % flashcards.length;
            showCard(currentCardIndex);
        }
    }

    function showNextCard() {
        if (flashcards.length > 0) {
            currentCardIndex = (currentCardIndex + 1) % flashcards.length;
            showCard(currentCardIndex);
        }
    }

    function showCard(index) {
        if (flashcards.length === 0) {
            cardFrontText.innerText = "This list has no words.";
            cardBackText.innerText = "Add more words to practice.";
            cardFrontPhonetic.style.display = 'none'; // Ẩn khối phiên âm
            progressText.innerText = '0 / 0';
            return;
        }

        if (flashcardEl.classList.contains('is-flipped')) {
            flashcardEl.classList.remove('is-flipped');
        }

        setTimeout(() => {
            const currentCard = flashcards[index];

            // Cập nhật nội dung
            cardFrontText.innerText = currentCard.wordText;
            cardBackText.innerText = currentCard.definition;
            progressText.innerText = (index + 1) + ' / ' + flashcards.length;

            // Cập nhật phiên âm và nút audio
            if (currentCard.phonetic) {
                cardFrontPhonetic.innerText = currentCard.phonetic;
                cardFrontPhonetic.style.display = 'flex'; // Hiển thị lại nếu bị ẩn
            } else {
                cardFrontPhonetic.style.display = 'none'; // Ẩn nếu từ không có phiên âm
            }

            // Cập nhật dữ liệu cho nút audio
            audioBtnFront.dataset.word = currentCard.wordText;
            if(currentCard.audioUrl) {
                audioBtnFront.dataset.audioUrl = currentCard.audioUrl;
            } else {
                delete audioBtnFront.dataset.audioUrl;
            }

        }, 250);
    }
});