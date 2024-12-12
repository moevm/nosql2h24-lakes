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
    window.history.back();
});

// Обработчики для кнопок "хочу посетить" и "уже посетил"
const heartButton = document.getElementById('heart-button');
const heartIcon = document.getElementById('heart-icon');
let isHeartFilled = false;

// document.addEventListener('DOMContentLoaded', () => {
//     const pathParts = window.location.pathname.split('/');
//     const lakeId = pathParts[pathParts.length - 1]; 

//     fetch(`/lakes/${lakeId}/status`)  // На сервере сделайте API для получения текущего состояния
//         .then(response => response.json())
//         .then(data => {
//             isHeartFilled = data.isWantVisit;  // Примерный формат данных от сервера
//             heartIcon.src = isHeartFilled ? '/assets/heart_filled.png' : '/assets/heart.png';
//         })
//         .catch(error => console.error('Ошибка при получении состояния:', error));
// });

// heartButton.addEventListener('click', () => {
//     const pathParts = window.location.pathname.split('/');
//     const lakeId = pathParts[pathParts.length - 1]; 

//     fetch('/check-auth', { method: 'GET' })
//         .then(response => response.json())
//         .then(data => {
//             if (!data.authenticated) {
//                 alert('Вы должны войти в систему, чтобы добавить в "Хочу посетить".');
//                 return;
//             }

//             isHeartFilled = !isHeartFilled;
//             heartIcon.src = isHeartFilled ? '/assets/heart_filled.png' : '/assets/heart.png';
            
//             fetch(`/lakes/${lakeId}/action`, {
//                 method: 'POST',
//                 headers: { 'Content-Type': 'application/json' },
//                 body: JSON.stringify({ action: isHeartFilled ? 'want_visit' : 'remove_want_visit' }),
//             })
//             .then(response => response.json())
//             .then(data => alert(data.message))
//             .catch(error => console.error('Ошибка:', error));
//         });
// });

const eyeButton = document.getElementById('eye-button');
const eyeIcon = document.getElementById('eye-icon');
let isEyeFilled = false;

// eyeButton.addEventListener('click', () => {
//     const pathParts = window.location.pathname.split('/');
//     const lakeId = pathParts[pathParts.length - 1]; 

//     fetch('/check-auth', { method: 'GET' })
//         .then(response => response.json())
//         .then(data => {
//             if (!data.authenticated) {
//                 alert('Вы должны войти в систему, чтобы добавить в "Уже посетил".');
//                 return;
//             }

//             isEyeFilled = !isEyeFilled;
//             eyeIcon.src = isEyeFilled ? '/assets/eye_filled.png' : '/assets/eye.png';
            
//             fetch(`/lakes/${lakeId}/action`, {
//                 method: 'POST',
//                 headers: { 'Content-Type': 'application/json' },
//                 body: JSON.stringify({ action: isEyeFilled ? 'visited' : 'remove_visited' }),
//             })
//             .then(response => response.json())
//             .then(data => alert(data.message))
//             .catch(error => console.error('Ошибка:', error));
//         });
// });

document.addEventListener('DOMContentLoaded', () => {
    const pathParts = window.location.pathname.split('/');
    const lakeId = pathParts[pathParts.length - 1]; 

    // Запрос состояния озера (хочет ли посетить и уже посетил)
    fetch(`/lakes/${lakeId}/status`)
        .then(response => response.json())
        .then(data => {
            isHeartFilled = data.isWantVisit;  // Статус кнопки "хочу посетить"
            isEyeFilled = data.isVisited;     // Статус кнопки "уже посетил"

            // Обновляем иконки на странице в зависимости от состояния
            heartIcon.src = isHeartFilled ? '/assets/heart_filled.png' : '/assets/heart.png';
            eyeIcon.src = isEyeFilled ? '/assets/eye_filled.png' : '/assets/eye.png';
        })
        .catch(error => console.error('Ошибка при получении состояния:', error));
});

heartButton.addEventListener('click', () => {
    const pathParts = window.location.pathname.split('/');
    const lakeId = pathParts[pathParts.length - 1]; 

    fetch('/check-auth', { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            if (!data.authenticated) {
                alert('Вы должны войти в систему, чтобы добавить в "Хочу посетить".');
                return;
            }

            isHeartFilled = !isHeartFilled;
            heartIcon.src = isHeartFilled ? '/assets/heart_filled.png' : '/assets/heart.png';

            fetch(`/lakes/${lakeId}/action`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ action: isHeartFilled ? 'want_visit' : 'remove_want_visit' }),
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                // После выполнения действия обновляем состояние на сервере
                fetch(`/lakes/${lakeId}/status`)
                    .then(response => response.json())
                    .then(data => {
                        // Обновляем иконки после успешного действия
                        heartIcon.src = data.isWantVisit ? '/assets/heart_filled.png' : '/assets/heart.png';
                    });
            })
            .catch(error => console.error('Ошибка:', error));
        });
});

eyeButton.addEventListener('click', () => {
    const pathParts = window.location.pathname.split('/');
    const lakeId = pathParts[pathParts.length - 1]; 

    fetch('/check-auth', { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            if (!data.authenticated) {
                alert('Вы должны войти в систему, чтобы добавить в "Уже посетил".');
                return;
            }

            isEyeFilled = !isEyeFilled;
            eyeIcon.src = isEyeFilled ? '/assets/eye_filled.png' : '/assets/eye.png';

            fetch(`/lakes/${lakeId}/action`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ action: isEyeFilled ? 'visited' : 'remove_visited' }),
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                // После выполнения действия обновляем состояние на сервере
                fetch(`/lakes/${lakeId}/status`)
                    .then(response => response.json())
                    .then(data => {
                        // Обновляем иконки после успешного действия
                        eyeIcon.src = data.isVisited ? '/assets/eye_filled.png' : '/assets/eye.png';
                    });
            })
            .catch(error => console.error('Ошибка:', error));
        });
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

const ratingElement = document.getElementById('average-rating');

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
    .then(data => {
        const review = data.review;
        console.log(review);
        console.log(data.lake.rating);
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
        const oldRating = data.lake.rating;
        const oldCount = document.getElementById('reviews-list').children.length - 1;
        const newRating = ((oldRating * oldCount) + review.stars) / (oldCount + 1);
        ratingElement.textContent = newRating.toFixed(2);
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
