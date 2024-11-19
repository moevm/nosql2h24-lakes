// Простой слайдер изображений с использованием стрелок
const images = document.querySelectorAll('.slider-image');
const leftArrow = document.querySelector('.left-arrow');
const rightArrow = document.querySelector('.right-arrow');
let currentIndex = 0;

function showImage(index) {
    images.forEach((img, i) => {
        img.style.display = i === index ? 'block' : 'none';
    });
}

leftArrow.addEventListener('click', () => {
    currentIndex = (currentIndex - 1 + images.length) % images.length;
    showImage(currentIndex);
});

rightArrow.addEventListener('click', () => {
    currentIndex = (currentIndex + 1) % images.length;
    showImage(currentIndex);
});

showImage(currentIndex);

// Обработчик кнопки "На главную"
const returnButton = document.getElementById("back-button");
returnButton.addEventListener("click", () => {
    window.location.href = '/main';
});

// Обработчики для кнопок "хочу посетить" и "уже посетил"
const heartButton = document.getElementById('heart-button');
const heartIcon = document.getElementById('heart-icon');
let isHeartFilled = false;
heartButton.addEventListener('click', () => {
    isHeartFilled = !isHeartFilled;
    heartIcon.src = isHeartFilled ? '/assets/heart_filled.png' : '/assets/heart.png';
    alert(isHeartFilled ? 'Добавлено в "Хочу посетить"' : 'Удалено из "Хочу посетить"');
});

const eyeButton = document.getElementById('eye-button');
const eyeIcon = document.getElementById('eye-icon');
let isEyeFilled = false;
eyeButton.addEventListener('click', () => {
    isEyeFilled = !isEyeFilled;
    eyeIcon.src = isEyeFilled ? '/assets/eye_filled.png' : '/assets/eye.png';
    alert(isEyeFilled ? 'Добавлено в "Уже посетил"' : 'Удалено из "Уже посетил"');
});

// Обработчик отправки отзыва
const submitButton = document.getElementById('submit-review-btn');
const reviewInput = document.getElementById('review-input');
const starRating = document.getElementById('star-rating');
const pathParts = window.location.pathname.split('/');
const lakeId = pathParts[pathParts.length - 1]; 

let selectedValue = 0;

document.addEventListener('DOMContentLoaded', () => {
    const pathParts = window.location.pathname.split('/');
    const lakeId = pathParts[pathParts.length - 1];
    //const lakeId = window.location.pathname.split('/').pop(); // Извлекаем lakeId из URL
    fetch(`/lakes/lake_page/${lakeId}/reviews`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка при загрузке отзывов');
            }
            return response.json();
        })
        .then(reviews => {
            console.log(reviews);
            // После получения отзывов, добавляем их на страницу
            const reviewsList = document.getElementById('reviews-list');
            reviews.forEach(review => {
                const reviewElement = createReviewElement(review);
                reviewsList.appendChild(reviewElement);
            });
        })
        .catch(error => {
            console.error('Ошибка:', error);
        });
});

// Функция для создания элемента отзыва
function createReviewElement(review) {
    const nickname = review.user?.nickname || 'Анонимный пользователь';
    const reviewElement = document.createElement('div');
    reviewElement.classList.add('review');
    reviewElement.innerHTML = `
        <div class="review-user">
            <img src="/assets/avatar.jpg" alt="Фото пользователя" class="user-photo">
            <div class="user-info">
                <h4>${nickname}</h4>
                <p>${new Date(review.date).toLocaleDateString('ru-RU')}</p>
            </div>
        </div>
        <div class="review-content">
            <div class="review-rating">
                <span>${'★'.repeat(review.stars)}</span>
                <span>${'☆'.repeat(5 - review.stars)}</span>
            </div>
            <div class="review-text">
                <p>${review.message}</p>
            </div>
        </div>
    `;
    return reviewElement;
}


submitButton.addEventListener('click', () => {
    // Проверяем, выбрал ли пользователь количество звезд и написал ли отзыв
    if (selectedValue === 0 || reviewInput.value.trim() === '') {
        alert('Пожалуйста, оцените озеро и напишите отзыв!');
        return;
    }
    console.log(selectedValue);

    const reviewText = reviewInput.value.trim();
    const reviewData = {
        message: reviewText,
        stars: selectedValue // Сохраняем выбранное значение    
    };

    // Отправляем отзыв на сервер через fetch
    fetch(`/lakes/lake_page/${lakeId}/reviews`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(reviewData),
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(errorText => {
                throw new Error(errorText || 'Ошибка при добавлении отзыва');
            });
        }
        return response.json();
    })
    .then(review => {
        console.log(review);
       
        const reviewElement = document.createElement('div');
        reviewElement.classList.add('review');
        reviewElement.innerHTML = `
            <div class="review-user">
                <img src="/assets/avatar.jpg" alt="Фото пользователя" class="user-photo">
                <div class="user-info">
                    <h4>${review.user.nickname}</h4>
                    <p>${new Date(review.date).toLocaleDateString('ru-RU')}</p>
                </div>
            </div>
            <div class="review-content">
                <div class="review-rating">
                    <span>${'★'.repeat(review.stars)}</span> 
                    <span>${'☆'.repeat(5 - review.stars)}</span>
                </div>
                <div class="review-text">
                    <p>${review.message}</p>
                </div>
            </div>
        `;
        document.getElementById('reviews-list').appendChild(reviewElement);
        reviewInput.value = '';
        // Сброс активных звезд после публикации отзыва
        starRating.querySelectorAll('span').forEach(span => span.classList.remove('active'));
        selectedValue = 0; // Сброс значения звезд
    })
    .catch(error => {
        alert(error.message);
    });
});


// Обработчик для клика на звезды
starRating.addEventListener('click', (e) => {
    const clickedSpan = e.target;
    if (clickedSpan.dataset.value) {
        selectedValue = parseInt(clickedSpan.dataset.value, 10); // Сохраняем выбранное количество звезд
        // Обновляем все звезды в соответствии с выбранной
        starRating.querySelectorAll('span').forEach(span => {
            if (parseInt(span.dataset.value, 10) <= selectedValue) {
                span.classList.add('active');
            } else {
                span.classList.remove('active');
            }
        });
    }
});

// Обработчик для наведения
starRating.addEventListener('mouseover', (e) => {
    const hoveredSpan = e.target;
    if (hoveredSpan.dataset.value) {
        const value = parseInt(hoveredSpan.dataset.value, 10);
        // Подсвечиваем все звезды до текущей при наведении
        starRating.querySelectorAll('span').forEach(span => {
            if (parseInt(span.dataset.value, 10) <= value) {
                span.classList.add('active');
            } else {
                span.classList.remove('active');
            }
        });
    }
});

// Обработчик для отмены подсветки при уходе мыши
starRating.addEventListener('mouseout', () => {
    // После ухода мыши, возвращаем закраску в соответствии с выбранной звездой
    starRating.querySelectorAll('span').forEach(span => {
        if (parseInt(span.dataset.value, 10) <= selectedValue) {
            span.classList.add('active'); // Подсвечиваем все выбранные звезды
        } else {
            span.classList.remove('active');
        }
    });
});
