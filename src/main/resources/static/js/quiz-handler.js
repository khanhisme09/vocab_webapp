document.addEventListener('DOMContentLoaded', () => {
    const questionsWrapper = document.getElementById('questions-wrapper');
    if (!questionsWrapper) return; // Thoát nếu không phải trang quiz

    const questionCards = document.querySelectorAll('.question-card');
    const progressBar = document.getElementById('quiz-progress-bar');
    const quizNavigation = document.getElementById('quiz-navigation');
    const nextButton = document.getElementById('next-question-btn');
    const finishForm = document.getElementById('finish-quiz-form');

    let currentQuestionIndex = 0;
    const totalQuestions = questionCards.length;
    const userAnswers = {};

    function updateProgress() {
        const percentage = ((currentQuestionIndex + 1) / totalQuestions) * 100;
        progressBar.style.width = percentage + '%';
        progressBar.innerText = `${currentQuestionIndex + 1}/${totalQuestions}`;
    }

    function showQuestion(index) {
        questionCards.forEach((card, i) => {
            card.classList.toggle('d-none', i !== index);
        });
        updateProgress();
    }

    function handleAnswer(isCorrect, answerValue) {
        userAnswers[currentQuestionIndex] = answerValue;
        quizNavigation.classList.remove('d-none');

        if (currentQuestionIndex >= totalQuestions - 1) {
            nextButton.innerText = 'Finish Quiz';
        }
    }

    // Gắn sự kiện cho các lựa chọn trắc nghiệm
    document.querySelectorAll('.quiz-option').forEach(option => {
        option.addEventListener('click', () => {
            const parentContainer = option.closest('.options-container');
            if (parentContainer.classList.contains('answered')) return;

            parentContainer.classList.add('answered');
            const correctAnswer = parentContainer.dataset.correctAnswer;
            const selectedAnswer = option.dataset.optionValue;

            // Hiển thị đáp án đúng/sai
            parentContainer.querySelectorAll('.quiz-option').forEach(opt => {
                if (opt.dataset.optionValue === correctAnswer) {
                    opt.classList.add('correct');
                } else if (opt === option) {
                    opt.classList.add('incorrect');
                }
            });

            handleAnswer(selectedAnswer === correctAnswer, selectedAnswer);
        });
    });

    // Gắn sự kiện cho các nút "Check" của câu hỏi điền từ
    document.querySelectorAll('.check-answer-btn').forEach(button => {
        button.addEventListener('click', () => {
            const parentCard = button.closest('.card-body');
            const input = parentCard.querySelector('input[type="text"]');
            const feedbackEl = parentCard.querySelector('.feedback-message');
            if (input.disabled) return;

            const correctAnswer = button.dataset.correctAnswer;
            const userAnswer = input.value.trim();

            input.disabled = true;
            button.disabled = true;

            if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                feedbackEl.innerText = 'Correct!';
                feedbackEl.className = 'feedback-message correct';
                handleAnswer(true, userAnswer);
            } else {
                feedbackEl.innerText = `Incorrect. The correct answer is: ${correctAnswer}`;
                feedbackEl.className = 'feedback-message incorrect';
                handleAnswer(false, userAnswer);
            }
        });
    });

    // String prototype để so sánh không phân biệt hoa thường
    String.prototype.equalsIgnoreCase = function(str) {
        return this.toLowerCase() === str.toLowerCase();
    }

    // Sự kiện cho nút Next/Finish
    nextButton.addEventListener('click', () => {
        if (currentQuestionIndex < totalQuestions - 1) {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
            quizNavigation.classList.add('d-none');
        } else {
            // Submit form
            for(const key in userAnswers) {
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = `answers[${key}]`;
                input.value = userAnswers[key];
                finishForm.appendChild(input);
            }
            finishForm.submit();
        }
    });

    // Khởi tạo
    showQuestion(0);
});