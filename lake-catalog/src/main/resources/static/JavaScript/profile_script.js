const backButton = document.getElementById("back-button");
backButton.addEventListener("click", () => {
    window.location.href = '/main';
});

const logoutButton = document.getElementById("logout-button");
logoutButton.addEventListener("click", () => {
    window.location.href = '/logout';
});

document.addEventListener('DOMContentLoaded', () => {
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Убираем активный класс у всех кнопок и контента
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));

            // Добавляем активный класс к выбранной кнопке и контенту
            button.classList.add('active');
            document.getElementById(button.dataset.tab).classList.add('active');
        });
    });

    const editProfileButton = document.getElementById('edit-profile');
    const saveProfileButton = document.getElementById('save-profile');
    const nameField = document.getElementById('name-field');
    const emailField = document.getElementById('email-field');
    const nameInput = document.getElementById('name-input');
    const emailInput = document.getElementById('email-input');

    // Включаем режим редактирования
    editProfileButton.addEventListener('click', () => {
        nameInput.value = document.getElementById('user-name').textContent;
        emailInput.value = document.getElementById('user-email').textContent;

        nameField.style.display = 'none';
        emailField.style.display = 'none';
        nameInput.style.display = 'block';
        emailInput.style.display = 'block';
        saveProfileButton.style.display = 'inline-block';
        editProfileButton.style.display = 'none'; // Скрыть кнопку редактирования
    });

    // Сохраняем изменения и возвращаем режим просмотра
    saveProfileButton.addEventListener('click', () => {
        const newName = nameInput.value.trim();
        const newEmail = emailInput.value.trim();

        // Проверяем заполненность полей
        if (!newName || !newEmail) {
            alert('Пожалуйста, заполните все поля.');
            return;
        }

        // Обновляем значения в режиме просмотра
        document.getElementById('user-name').textContent = newName;
        document.getElementById('user-email').textContent = newEmail;

        nameField.style.display = 'block';
        emailField.style.display = 'block';
        nameInput.style.display = 'none';
        emailInput.style.display = 'none';
        saveProfileButton.style.display = 'none';
        editProfileButton.style.display = 'inline-block'; // Вернуть кнопку редактирования
    });
});


