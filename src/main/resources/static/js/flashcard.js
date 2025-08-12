// Lấy dữ liệu DTO đã được làm phẳng từ controller
const flashcards = /*[[${flashcards}]]*/ [];

let currentCardIndex = 0;

// Lấy các phần tử DOM
const flashcardEl = document.getElementById('flashcard');
const cardFrontText = document.getElementById('card-front-text');
const cardFrontPhonetic = document.getElementById('card-front-phonetic'); // Mới
const audioBtnFront = document.getElementById('audio-btn-front');
const cardBackText = document.getElementById('card-back-text');
const progressText = document.getElementById('progress-text');
const prevBtn = document.getElementById('prev-btn');
const nextBtn = document.getElementById('next-btn');
const audioPlayer = new Audio();

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

// Sự kiện lật thẻ: Ngăn lật khi click vào khối phiên âm
flashcardEl.addEventListener('click', (event) => {
    if (!event.target.closest('.phonetic-block') && flashcards.length > 0) {
        flashcardEl.classList.toggle('is-flipped');
    }
});

// Sự kiện cho nút audio: Giữ nguyên như cũ
audioBtnFront.addEventListener('click', (event) => {
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
            alert('Your browser does not support Text-to-Speech.');
        }
    }
});

// Các sự kiện điều hướng: Giữ nguyên như cũ
prevBtn.addEventListener('click', () => { /* ... */ });
nextBtn.addEventListener('click', () => { /* ... */ });

// Hiển thị thẻ đầu tiên
showCard(currentCardIndex);