const globalAudioPlayer = new Audio();

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

document.addEventListener('DOMContentLoaded', initializeAudioButtons);