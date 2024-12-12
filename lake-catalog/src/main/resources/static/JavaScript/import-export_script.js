
// Функция для обработки кнопки "Загрузить"
document.getElementById('upload-button').addEventListener('click', function () {
    const fileInput = document.getElementById('file-upload');
    if (fileInput.files.length === 0) {
        alert('Пожалуйста, выберите файл для загрузки');
        return;
    }
    alert(`Файл ${fileInput.files[0].name} будет загружен!`);
    // Здесь можно добавить логику для загрузки данных (например, через API)
});

document.getElementById('upload-button').addEventListener('click', async function () {
    const fileInput = document.getElementById('file-upload');
    const file = fileInput.files[0]; // Получаем выбранный файл

    if (!file) {
        alert('Пожалуйста, выберите файл для загрузки.');
        return;
    }

    try {
        // Читаем содержимое файла как текст
        const fileContent = await file.text();
        const lakesData = JSON.parse(fileContent); // Преобразуем текст в JSON

        // Отправляем данные на сервер
        const response = await fetch('/users/profile/{userId}/imp_exp/import', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(lakesData),
        });

        if (response.ok) {
            const message = await response.text();
            alert(message);
        } else {
            const errorMessage = await response.text();
            alert(`Ошибка: ${errorMessage}`);
        }
    } catch (error) {
        console.error('Ошибка при обработке файла:', error);
        alert('Произошла ошибка при загрузке файла.');
    }
});


function goToProfile(){
    window.location.href = '/users/profile/current';
}

document.getElementById('export-button').addEventListener('click', async () => {
    alert('База данных будет скачана!');

    try {
        // Отправляем запрос на экспорт данных
        const response = await fetch('/users/profile/{userId}/imp_exp/export', {
            method: 'GET',
        });

        if (!response.ok) {
            throw new Error('Ошибка при получении данных');
        }

        // Создаем Blob с типом json
        const blob = await response.blob();

        // Создаем ссылку для скачивания файла
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = 'lakes.json'; // Имя файла

        // Инициируем скачивание
        link.click();
    } catch (error) {
        alert('Ошибка при экспорте данных: ' + error.message);
    }
});

