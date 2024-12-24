const backButton = document.getElementById("back-button");
backButton.addEventListener("click", () => {
    window.location.href = '/main';
});

const logoutButton = document.getElementById("logout-button");
logoutButton.addEventListener("click", () => {
    window.location.href = '/logout';
});

const imp_expButton = document.getElementById("import-export-button");
imp_expButton.addEventListener("click", () => {
    window.location.href = '/users/imp_exp';
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
    saveProfileButton.addEventListener('click', async () => {
        const newName = nameInput.value.trim();
        const newEmail = emailInput.value.trim();

        // Проверяем заполненность полей
        if (!newName || !newEmail) {
            alert('Пожалуйста, заполните все поля.');
            return;
        }

        const currentEmail = document.getElementById('user-email').textContent;

        // Если email не изменился, пропускаем проверку на уникальность
        if (newEmail === currentEmail) {
            await updateProfile(newName, newEmail); // Просто обновляем профиль
        } else {
            // Проверка уникальности email
            const emailResponse = await fetch(`/users/profile/${userId}/check-email-unique`, {
                method: 'POST',
                body: JSON.stringify({ email: newEmail }),
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            const emailData = await emailResponse.json();
            if (!emailData.isUnique) {
                alert('Этот email уже занят.');
                return;
            }

            // Отправляем данные на сервер для обновления профиля
            await updateProfile(newName, newEmail);
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

async function updateProfile(newName, newEmail) {
    const pathParts = window.location.pathname.split('/');
    const userId = pathParts[pathParts.length - 1]; 
    const updateResponse = await fetch(`/users/profile/${userId}/update-profile?newName=${newName}&newEmail=${newEmail}`, {
        method: 'PUT',
    });

    if (updateResponse.ok) {
        const updateData = await updateResponse.json();
        alert(updateData.message); // Профиль успешно обновлен
        if (updateData.editDate) {
            const editDateElement = document.getElementById('edit-date');
            const date = new Date(updateData.editDate);

            const day = String(date.getDate()).padStart(2, '0');
            const month = String(date.getMonth() + 1).padStart(2, '0'); // Month is 0-indexed
            const year = date.getFullYear();

            const formattedDate = `${day}-${month}-${year}`;
            editDateElement.textContent = formattedDate;
        }
    } else {
        const errorData = await updateResponse.json();
        alert('Ошибка при обновлении профиля: ' + errorData.message);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const changePhotoButton = document.getElementById('change-photo-button');
    const savePhotoButton = document.getElementById('save-photo-button');
    const cancelPhotoButton = document.getElementById('cancel-photo-button');
    const photoUrlInput = document.getElementById('photo-url-input');

    // Показать поле для ввода ссылки
    changePhotoButton.addEventListener('click', () => {
        photoUrlInput.style.display = 'inline-block';
        savePhotoButton.style.display = 'inline-block';
        cancelPhotoButton.style.display = 'inline-block';
        changePhotoButton.style.display = 'none';
    });

    // Сохранить новую ссылку на фото
    savePhotoButton.addEventListener('click', async () => {
        const newPhotoUrl = photoUrlInput.value.trim();

        if (!newPhotoUrl) {
            alert('Введите ссылку на фото.');
            return;
        }

        const userId = window.location.pathname.split('/').pop();

        try {
            const response = await fetch(`/users/profile/${userId}/update-photo-url`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ photoUrl: newPhotoUrl }),
            });

            if (response.ok) {
                document.getElementById('profile-photo').src = newPhotoUrl; // Обновить фото на странице
                alert('Фото обновлено успешно!');
            } else {
                const errorData = await response.json();
                alert('Ошибка при обновлении фото: ' + errorData.message);
            }
        } catch (error) {
            alert('Ошибка подключения к серверу.');
        } finally {
            resetPhotoEditingState(); // Возвращаем интерфейс в исходное состояние
        }
    });

    // Отмена изменения фото
    cancelPhotoButton.addEventListener('click', () => {
        resetPhotoEditingState(); // Возвращаем интерфейс в исходное состояние
    });

    // Функция для сброса состояния редактирования фото
    function resetPhotoEditingState() {
        photoUrlInput.style.display = 'none';
        savePhotoButton.style.display = 'none';
        cancelPhotoButton.style.display = 'none';
        changePhotoButton.style.display = 'inline-block';
        photoUrlInput.value = ''; // Очистить поле ввода
    }
});


